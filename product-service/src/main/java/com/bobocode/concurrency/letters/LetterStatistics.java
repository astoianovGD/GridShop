package com.bobocode.concurrency.letters;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

/**
 * Benchmarks different concurrency approaches for counting letters across multiple files:
 * <ol>
 *   <li>Thread per file + unsynchronized {@link HashMap} (demonstrates race conditions)</li>
 *   <li>Thread per file + {@code synchronized} {@link HashMap} (correct, high lock contention)</li>
 *   <li>Thread per file + {@link ConcurrentHashMap} (correct, lock striping + CAS)</li>
 *   <li>Thread per file + custom {@link LetterCountTable} with 26 per-letter locks</li>
 *   <li>Fixed Thread Pool (CPU core count) + {@link ConcurrentHashMap}</li>
 *   <li>Java 21 Virtual Threads + {@link ConcurrentHashMap}</li>
 *   <li>Shared-nothing local maps per task + sequential merge</li>
 *   <li>Shared-nothing local maps per task + parallel merge</li>
 * </ol>
 */
@Slf4j
public final class LetterStatistics {

    private LetterStatistics() {
    }

    /**
     * Result of a single benchmark run.
     *
     * @param approachName     name of the concurrency strategy
     * @param counts           letter frequencies ('a'..'z')
     * @param totalLetters     sum of all counted letters
     * @param elapsedTimeMs    execution time in milliseconds
     */
    public record BenchmarkResult(
            String approachName,
            Map<Character, Long> counts,
            long totalLetters,
            long elapsedTimeMs
    ) {
    }

    /**
     * Recursively collects regular readable text/source files from a directory.
     *
     * @param rootDir root directory to scan
     * @return list of file paths
     * @throws IOException if directory traversal fails
     */
    public static List<Path> collectFilesRecursively(final Path rootDir) throws IOException {
        try (Stream<Path> walk = Files.walk(rootDir)) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> !path.toString().contains("/.git/")
                            && !path.toString().contains("/target/")
                            && !path.toString().contains("/.idea/"))
                    .filter(path -> {
                        String name = path.getFileName().toString();
                        return name.endsWith(".java")
                                || name.endsWith(".xml")
                                || name.endsWith(".yaml")
                                || name.endsWith(".yml")
                                || name.endsWith(".sql")
                                || name.endsWith(".md")
                                || name.endsWith(".properties");
                    })
                    .toList();
        }
    }

    /**
     * 1. Unsafe approach: creates a new thread per file and mutates a shared unsynchronized HashMap.
     * Produces inaccurate counts or exceptions due to data races.
     */
    public static BenchmarkResult countWithUnsafeHashMap(final List<Path> files) throws InterruptedException {
        long start = System.nanoTime();
        Map<Character, Long> sharedMap = new HashMap<>(64);
        List<Thread> threads = new ArrayList<>(files.size());

        for (Path file : files) {
            Thread thread = new Thread(() -> {
                String content = readFileSafely(file);
                for (int i = 0; i < content.length(); i++) {
                    char ch = Character.toLowerCase(content.charAt(i));
                    if (ch >= 'a' && ch <= 'z') {
                        try {
                            Long current = sharedMap.get(ch);
                            sharedMap.put(ch, current == null ? 1L : current + 1L);
                        } catch (Exception ignored) {
                            // Unsynchronized HashMap resize can throw internal exceptions under race
                        }
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        Map<Character, Long> safeCopy = new TreeMap<>();
        sharedMap.forEach((k, v) -> {
            if (k != null && v != null) {
                safeCopy.put(k, v);
            }
        });
        return new BenchmarkResult("1. Unsafe HashMap (Thread per file)", safeCopy, sumCounts(safeCopy), elapsedMs);
    }

    /**
     * 2. Synchronized HashMap approach: wraps every letter increment in a synchronized block.
     * Accurate, but threads contend heavily on a single monitor.
     */
    public static BenchmarkResult countWithSynchronizedHashMap(final List<Path> files) throws InterruptedException {
        long start = System.nanoTime();
        Map<Character, Long> sharedMap = new HashMap<>(64);
        Object lock = new Object();
        List<Thread> threads = new ArrayList<>(files.size());

        for (Path file : files) {
            Thread thread = new Thread(() -> {
                String content = readFileSafely(file);
                for (int i = 0; i < content.length(); i++) {
                    char ch = Character.toLowerCase(content.charAt(i));
                    if (ch >= 'a' && ch <= 'z') {
                        synchronized (lock) {
                            sharedMap.put(ch, sharedMap.getOrDefault(ch, 0L) + 1L);
                        }
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        return new BenchmarkResult(
                "2. Synchronized HashMap (Thread per file)",
                new TreeMap<>(sharedMap),
                sumCounts(sharedMap),
                elapsedMs
        );
    }

    /**
     * 3. ConcurrentHashMap approach: uses ConcurrentHashMap with LongAdder counters.
     */
    public static BenchmarkResult countWithConcurrentHashMap(final List<Path> files) throws InterruptedException {
        long start = System.nanoTime();
        ConcurrentHashMap<Character, LongAdder> sharedMap = new ConcurrentHashMap<>(64);
        List<Thread> threads = new ArrayList<>(files.size());

        for (Path file : files) {
            Thread thread = new Thread(() -> countFileIntoConcurrentMap(file, sharedMap));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        Map<Character, Long> result = convertAdderMap(sharedMap);
        return new BenchmarkResult(
                "3. ConcurrentHashMap (Thread per file)",
                result,
                sumCounts(result),
                elapsedMs
        );
    }

    /**
     * 4. Custom data structure with a separate lock for each of the 26 letters ('a'..'z').
     */
    public static BenchmarkResult countWithCustomLetterTable(final List<Path> files) throws InterruptedException {
        long start = System.nanoTime();
        LetterCountTable table = new LetterCountTable();
        List<Thread> threads = new ArrayList<>(files.size());

        for (Path file : files) {
            Thread thread = new Thread(() -> {
                String content = readFileSafely(file);
                for (int i = 0; i < content.length(); i++) {
                    table.increment(content.charAt(i));
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        Map<Character, Long> result = table.toMap();
        return new BenchmarkResult(
                "4. Custom 26-Lock Table (Thread per file)",
                result,
                sumCounts(result),
                elapsedMs
        );
    }

    /**
     * 5. Fixed Thread Pool sized to the number of available CPU cores + ConcurrentHashMap.
     */
    public static BenchmarkResult countWithCpuCoreThreadPool(final List<Path> files)
            throws InterruptedException, ExecutionException {
        long start = System.nanoTime();
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ConcurrentHashMap<Character, LongAdder> sharedMap = new ConcurrentHashMap<>(64);

        try (ExecutorService executor = Executors.newFixedThreadPool(cpuCores)) {
            List<Future<?>> futures = new ArrayList<>(files.size());
            for (Path file : files) {
                futures.add(executor.submit(() -> countFileIntoConcurrentMap(file, sharedMap)));
            }
            for (Future<?> future : futures) {
                future.get();
            }
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        Map<Character, Long> result = convertAdderMap(sharedMap);
        return new BenchmarkResult(
                "5. FixedThreadPool (" + cpuCores + " CPU cores) + ConcurrentHashMap",
                result,
                sumCounts(result),
                elapsedMs
        );
    }

    /**
     * 6. Java 21 Virtual Threads + ConcurrentHashMap.
     */
    public static BenchmarkResult countWithVirtualThreads(final List<Path> files)
            throws InterruptedException, ExecutionException {
        long start = System.nanoTime();
        ConcurrentHashMap<Character, LongAdder> sharedMap = new ConcurrentHashMap<>(64);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = new ArrayList<>(files.size());
            for (Path file : files) {
                futures.add(executor.submit(() -> countFileIntoConcurrentMap(file, sharedMap)));
            }
            for (Future<?> future : futures) {
                future.get();
            }
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        Map<Character, Long> result = convertAdderMap(sharedMap);
        return new BenchmarkResult(
                "6. Virtual Threads (Java 21) + ConcurrentHashMap",
                result,
                sumCounts(result),
                elapsedMs
        );
    }

    /**
     * 7 & 8. Shared-nothing approach: each task computes letter counts into its own thread-local Map,
     * followed by either a sequential or a parallel merge step.
     */
    public static BenchmarkResult countWithLocalMapsAndMerge(final List<Path> files, final boolean parallelMerge)
            throws InterruptedException, ExecutionException {
        long start = System.nanoTime();
        int cpuCores = Runtime.getRuntime().availableProcessors();
        List<Map<Character, Long>> partialMaps = new ArrayList<>(files.size());

        try (ExecutorService executor = Executors.newFixedThreadPool(cpuCores)) {
            List<Callable<Map<Character, Long>>> tasks = files.stream()
                    .<Callable<Map<Character, Long>>>map(file -> () -> countSingleFileLocally(file))
                    .toList();

            List<Future<Map<Character, Long>>> futures = executor.invokeAll(tasks);
            for (Future<Map<Character, Long>> future : futures) {
                partialMaps.add(future.get());
            }
        }

        Map<Character, Long> merged;
        if (parallelMerge) {
            merged = partialMaps.parallelStream()
                    .reduce(
                            Collections.emptyMap(),
                            LetterStatistics::mergeTwoMaps,
                            LetterStatistics::mergeTwoMaps
                    );
        } else {
            merged = new HashMap<>(64);
            for (Map<Character, Long> partial : partialMaps) {
                partial.forEach((ch, count) -> merged.merge(ch, count, Long::sum));
            }
        }

        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        Map<Character, Long> sorted = new TreeMap<>(merged);
        String label = parallelMerge
                ? "8. Shared-Nothing Local Maps + Parallel Merge"
                : "7. Shared-Nothing Local Maps + Sequential Merge";
        return new BenchmarkResult(label, sorted, sumCounts(sorted), elapsedMs);
    }

    private static void countFileIntoConcurrentMap(
            final Path file,
            final ConcurrentHashMap<Character, LongAdder> sharedMap
    ) {
        String content = readFileSafely(file);
        for (int i = 0; i < content.length(); i++) {
            char ch = Character.toLowerCase(content.charAt(i));
            if (ch >= 'a' && ch <= 'z') {
                sharedMap.computeIfAbsent(ch, k -> new LongAdder()).increment();
            }
        }
    }

    private static Map<Character, Long> countSingleFileLocally(final Path file) {
        Map<Character, Long> localMap = new HashMap<>(64);
        String content = readFileSafely(file);
        for (int i = 0; i < content.length(); i++) {
            char ch = Character.toLowerCase(content.charAt(i));
            if (ch >= 'a' && ch <= 'z') {
                localMap.merge(ch, 1L, Long::sum);
            }
        }
        return localMap;
    }

    private static Map<Character, Long> mergeTwoMaps(
            final Map<Character, Long> left,
            final Map<Character, Long> right
    ) {
        Map<Character, Long> combined = new HashMap<>(left);
        right.forEach((ch, count) -> combined.merge(ch, count, Long::sum));
        return combined;
    }

    private static Map<Character, Long> convertAdderMap(final Map<Character, LongAdder> adderMap) {
        Map<Character, Long> result = new TreeMap<>();
        adderMap.forEach((k, v) -> result.put(k, v.sum()));
        return result;
    }

    private static long sumCounts(final Map<Character, Long> map) {
        return map.values().stream().mapToLong(Long::longValue).sum();
    }

    private static String readFileSafely(final Path file) {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Entry point to execute all 8 concurrency benchmarks and print a comparison table.
     *
     * @param args optional first argument specifying directory to scan (defaults to current project root)
     */
    public static void main(final String[] args) throws Exception {
        Path rootDir = args.length > 0 ? Path.of(args[0]) : Path.of(".");
        List<Path> baseFiles = collectFilesRecursively(rootDir);

        // Repeat the file list so we have ~1000+ file tasks to make concurrency differences clearly visible
        List<Path> files = new ArrayList<>();
        int multiplier = Math.max(1, 1000 / Math.max(1, baseFiles.size()));
        for (int i = 0; i < multiplier; i++) {
            files.addAll(baseFiles);
        }

        log.info("Running Letter Statistics benchmarks on {} file tasks (CPU cores: {})...",
                files.size(), Runtime.getRuntime().availableProcessors());

        List<BenchmarkResult> results = List.of(
                countWithUnsafeHashMap(files),
                countWithSynchronizedHashMap(files),
                countWithConcurrentHashMap(files),
                countWithCustomLetterTable(files),
                countWithCpuCoreThreadPool(files),
                countWithVirtualThreads(files),
                countWithLocalMapsAndMerge(files, false),
                countWithLocalMapsAndMerge(files, true)
        );

        log.info("================================================================================");
        log.info(String.format("%-52s | %12s | %8s", "Concurrency Approach", "Total Letters", "Time (ms)"));
        log.info("--------------------------------------------------------------------------------");
        for (BenchmarkResult r : results) {
            log.info(String.format("%-52s | %12d | %6d ms",
                    r.approachName(), r.totalLetters(), r.elapsedTimeMs()));
        }
        log.info("================================================================================");
    }
}
