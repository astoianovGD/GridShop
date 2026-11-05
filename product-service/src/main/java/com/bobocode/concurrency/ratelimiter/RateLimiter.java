package com.bobocode.concurrency.ratelimiter;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread-safe configurable Sliding Window Log Rate Limiter.
 *
 * <p>Tracks request timestamps per client key inside a {@link ConcurrentHashMap} and uses
 * {@link ConcurrentHashMap#compute(Object, java.util.function.BiFunction)} so that checking
 * and recording a request for a given client key executes atomically without blocking
 * requests from other clients.
 */
public class RateLimiter {

    private final int maxRequests;
    private final long windowDurationNanos;
    private final ConcurrentHashMap<String, Deque<Long>> clientTimestamps = new ConcurrentHashMap<>();

    /**
     * Creates a new RateLimiter with the given limit and time interval.
     *
     * @param maxRequests  maximum number of allowed requests within {@code timeInterval}
     * @param timeInterval sliding time window duration
     */
    public RateLimiter(final int maxRequests, final Duration timeInterval) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive, got: " + maxRequests);
        }
        Objects.requireNonNull(timeInterval, "timeInterval must not be null");
        if (timeInterval.isZero() || timeInterval.isNegative()) {
            throw new IllegalArgumentException("timeInterval must be positive");
        }
        this.maxRequests = maxRequests;
        this.windowDurationNanos = timeInterval.toNanos();
    }

    /**
     * Checks whether a request from {@code clientKey} is allowed at the current instant.
     *
     * @param clientKey unique identifier of the client (e.g., API key, user ID, or IP address)
     * @return {@code true} if the request is allowed, {@code false} if rate limit is exceeded
     */
    public boolean tryAcquire(final String clientKey) {
        Objects.requireNonNull(clientKey, "clientKey must not be null");
        long now = System.nanoTime();
        long windowStart = now - windowDurationNanos;
        AtomicBoolean allowed = new AtomicBoolean(false);

        clientTimestamps.compute(clientKey, (key, timestamps) -> {
            Deque<Long> queue = timestamps != null ? timestamps : new ArrayDeque<>(maxRequests);

            while (!queue.isEmpty() && queue.peekFirst() <= windowStart) {
                queue.pollFirst();
            }

            if (queue.size() < maxRequests) {
                queue.addLast(now);
                allowed.set(true);
            }
            return queue;
        });

        return allowed.get();
    }

    /**
     * Returns the configured maximum number of requests per interval.
     *
     * @return max requests
     */
    public int getMaxRequests() {
        return maxRequests;
    }

    /**
     * Returns the configured sliding window duration.
     *
     * @return time interval
     */
    public Duration getTimeInterval() {
        return Duration.ofNanos(windowDurationNanos);
    }
}
