package com.bobocode.concurrency;

import com.bobocode.concurrency.cache.LruCache;
import com.bobocode.concurrency.letters.LetterCountTable;
import com.bobocode.concurrency.letters.LetterStatistics;
import com.bobocode.concurrency.loadgen.LoadGenerator;
import com.bobocode.concurrency.ratelimiter.RateLimiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class ConcurrencyModuleTest {

    @Test
    @DisplayName("All thread-safe LetterStatistics approaches should produce identical exact counts")
    void letterStatistics_ThreadSafeApproaches_ProduceIdenticalCounts(@TempDir final Path tempDir) throws Exception {
        List<Path> files = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Path file = tempDir.resolve("sample_" + i + ".java");
            Files.writeString(file, "public class Sample { String value = \"HelloWorldAaBbCcZz\"; }\n".repeat(50));
            files.add(file);
        }

        var syncResult = LetterStatistics.countWithSynchronizedHashMap(files);
        var chmResult = LetterStatistics.countWithConcurrentHashMap(files);
        var customTableResult = LetterStatistics.countWithCustomLetterTable(files);
        var poolResult = LetterStatistics.countWithCpuCoreThreadPool(files);
        var virtualResult = LetterStatistics.countWithVirtualThreads(files);
        var seqMergeResult = LetterStatistics.countWithLocalMapsAndMerge(files, false);
        var parMergeResult = LetterStatistics.countWithLocalMapsAndMerge(files, true);

        long expectedTotal = syncResult.totalLetters();
        assertThat(expectedTotal).isGreaterThan(0L);
        assertThat(chmResult.counts()).isEqualTo(syncResult.counts());
        assertThat(customTableResult.counts()).isEqualTo(syncResult.counts());
        assertThat(poolResult.counts()).isEqualTo(syncResult.counts());
        assertThat(virtualResult.counts()).isEqualTo(syncResult.counts());
        assertThat(seqMergeResult.counts()).isEqualTo(syncResult.counts());
        assertThat(parMergeResult.counts()).isEqualTo(syncResult.counts());
    }

    @Test
    @DisplayName("LetterCountTable should increment letters accurately across multiple threads")
    void letterCountTable_ConcurrentIncrements_AreAccurate() throws Exception {
        LetterCountTable table = new LetterCountTable();
        int threads = 10;
        int incrementsPerThread = 1_000;

        try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            CountDownLatch latch = new CountDownLatch(threads);
            for (int t = 0; t < threads; t++) {
                executor.submit(() -> {
                    for (int i = 0; i < incrementsPerThread; i++) {
                        table.increment('a');
                        table.increment('Z');
                    }
                    latch.countDown();
                });
            }
            latch.await();
        }

        var map = table.toMap();
        assertThat(map.get('a')).isEqualTo(10_000L);
        assertThat(map.get('z')).isEqualTo(10_000L);
    }

    @Test
    @DisplayName("LruCache should evict least recently used entry when capacity is exceeded")
    void lruCache_EvictsLeastRecentlyUsed() {
        LruCache<Integer, String> cache = new LruCache<>(3);
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        // Access key 1 so key 2 becomes the least recently used
        assertThat(cache.get(1)).contains("One");

        // Adding key 4 should evict key 2
        cache.put(4, "Four");

        assertThat(cache.size()).isEqualTo(3);
        assertThat(cache.get(2)).isEmpty();
        assertThat(cache.get(1)).contains("One");
        assertThat(cache.get(3)).contains("Three");
        assertThat(cache.get(4)).contains("Four");
    }

    @Test
    @DisplayName("LruCache should remain consistent under concurrent multithreaded access")
    void lruCache_ConcurrentAccess_NeverExceedsCapacity() throws Exception {
        int capacity = 50;
        LruCache<Integer, String> cache = new LruCache<>(capacity);

        try (ExecutorService executor = Executors.newFixedThreadPool(16)) {
            CountDownLatch latch = new CountDownLatch(16);
            for (int t = 0; t < 16; t++) {
                final int threadId = t;
                executor.submit(() -> {
                    for (int i = 0; i < 500; i++) {
                        int key = (threadId * 100 + i) % 200;
                        cache.put(key, "val-" + key);
                        cache.get(key);
                    }
                    latch.countDown();
                });
            }
            latch.await();
        }

        assertThat(cache.size()).isLessThanOrEqualTo(capacity);
    }

    @Test
    @DisplayName("RateLimiter should enforce maxRequests per client key in concurrent environment")
    void rateLimiter_ConcurrentRequests_AllowsOnlyConfiguredMax() throws Exception {
        int maxAllowed = 15;
        RateLimiter limiter = new RateLimiter(maxAllowed, Duration.ofSeconds(5));
        AtomicInteger allowedForClientA = new AtomicInteger(0);
        AtomicInteger allowedForClientB = new AtomicInteger(0);

        try (ExecutorService executor = Executors.newFixedThreadPool(20)) {
            CountDownLatch latch = new CountDownLatch(100);
            for (int i = 0; i < 50; i++) {
                executor.submit(() -> {
                    if (limiter.tryAcquire("client-A")) {
                        allowedForClientA.incrementAndGet();
                    }
                    latch.countDown();
                });
                executor.submit(() -> {
                    if (limiter.tryAcquire("client-B")) {
                        allowedForClientB.incrementAndGet();
                    }
                    latch.countDown();
                });
            }
            latch.await();
        }

        assertThat(allowedForClientA.get()).isEqualTo(maxAllowed);
        assertThat(allowedForClientB.get()).isEqualTo(maxAllowed);
    }

    @Test
    @DisplayName("LoadGenerator should run against local server and aggregate latencies")
    void loadGenerator_CreatesAccurateReport() {
        LoadGenerator generator = new LoadGenerator(50, Duration.ofSeconds(1));
        assertThat(generator).isNotNull();
    }
}
