package com.bobocode.concurrency.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Thread-safe generic local in-memory cache with Least Recently Used (LRU) eviction policy.
 *
 * <p>Uses a {@link LinkedHashMap} configured in access-order mode (`accessOrder = true`)
 * guarded by a {@link ReentrantLock}. Because reading an entry in an LRU cache updates
 * the internal doubly-linked access list, both reads and writes acquire the lock to prevent
 * data corruption under concurrent access.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class LruCache<K, V> {

    private static final float LOAD_FACTOR = 0.75f;

    private final int maxCapacity;
    private final Map<K, V> storage;
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Creates a thread-safe LRU cache with the specified maximum capacity.
     *
     * @param maxCapacity maximum number of entries before the least recently used entry is evicted
     */
    public LruCache(final int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Cache maxCapacity must be positive, got: " + maxCapacity);
        }
        this.maxCapacity = maxCapacity;
        this.storage = new LinkedHashMap<>(Math.min(maxCapacity, 1024), LOAD_FACTOR, true) {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
                return size() > LruCache.this.maxCapacity;
            }
        };
    }

    /**
     * Retrieves a value by key and marks the entry as most recently used.
     *
     * @param key cache key
     * @return {@link Optional} containing the cached value, or empty if absent
     */
    public Optional<V> get(final K key) {
        lock.lock();
        try {
            return Optional.ofNullable(storage.get(key));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Associates the specified value with the specified key in this cache.
     * If the cache exceeds {@code maxCapacity}, the least recently used entry is automatically evicted.
     *
     * @param key   cache key
     * @param value value to cache
     */
    public void put(final K key, final V value) {
        lock.lock();
        try {
            storage.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the cached value for {@code key} if present; otherwise computes it using
     * {@code mappingFunction} and stores it in the cache.
     *
     * @param key             cache key
     * @param mappingFunction function to compute value when absent
     * @return existing or newly computed value
     */
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        lock.lock();
        try {
            V existing = storage.get(key);
            if (existing != null) {
                return existing;
            }
        } finally {
            lock.unlock();
        }

        // Compute outside lock to avoid holding the cache lock during slow DB/network calls,
        // then store atomically.
        V computed = mappingFunction.apply(key);
        if (computed != null) {
            lock.lock();
            try {
                V doubleCheck = storage.get(key);
                if (doubleCheck != null) {
                    return doubleCheck;
                }
                storage.put(key, computed);
            } finally {
                lock.unlock();
            }
        }
        return computed;
    }

    /**
     * Evicts a specific key from the cache.
     *
     * @param key key to remove
     */
    public void evict(final K key) {
        lock.lock();
        try {
            storage.remove(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        lock.lock();
        try {
            storage.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the current number of entries in the cache.
     *
     * @return current cache size
     */
    public int size() {
        lock.lock();
        try {
            return storage.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the configured maximum capacity of the cache.
     *
     * @return maximum capacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
}
