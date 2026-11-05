package com.bobocode.concurrency.loadgen;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * Load generator that dispatches a constant number of HTTP requests per second (RPS)
 * independently of the target service's response time, and aggregates latency statistics
 * in a thread-safe manner.
 */
@Slf4j
public class LoadGenerator {

    private final HttpClient httpClient;
    private final int requestsPerSecond;
    private final Duration duration;

    public LoadGenerator(final int requestsPerSecond, final Duration duration) {
        if (requestsPerSecond <= 0) {
            throw new IllegalArgumentException("requestsPerSecond must be positive");
        }
        if (duration == null || duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("duration must be positive");
        }
        this.requestsPerSecond = requestsPerSecond;
        this.duration = duration;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                .build();
    }

    /**
     * Aggregated thread-safe report of a load test execution.
     *
     * @param totalSent        total number of requests dispatched
     * @param successCount     number of HTTP 2xx responses
     * @param rateLimitedCount number of HTTP 429 responses
     * @param errorCount       number of failed or non-2xx/429 responses
     * @param minLatencyMs     minimum response time in ms
     * @param avgLatencyMs     average response time in ms
     * @param p95LatencyMs     95th percentile response time in ms
     * @param p99LatencyMs     99th percentile response time in ms
     * @param maxLatencyMs     maximum response time in ms
     */
    public record LoadTestReport(
            long totalSent,
            long successCount,
            long rateLimitedCount,
            long errorCount,
            long minLatencyMs,
            double avgLatencyMs,
            long p95LatencyMs,
            long p99LatencyMs,
            long maxLatencyMs
    ) {
    }

    /**
     * Executes the load test against the given target URI.
     *
     * @param targetUri URI to send GET requests to
     * @param clientId  optional {@code X-Client-Id} header value
     * @return aggregated {@link LoadTestReport}
     * @throws InterruptedException if interrupted while awaiting test completion
     */
    public LoadTestReport runLoadTest(final URI targetUri, final String clientId) throws InterruptedException {
        LongAdder totalSent = new LongAdder();
        LongAdder successCount = new LongAdder();
        LongAdder rateLimitedCount = new LongAdder();
        LongAdder errorCount = new LongAdder();
        ConcurrentLinkedQueue<Long> latenciesMs = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<CompletableFuture<Void>> inFlightFutures = new ConcurrentLinkedQueue<>();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(targetUri)
                .timeout(Duration.ofSeconds(10))
                .GET();
        if (clientId != null && !clientId.isBlank()) {
            requestBuilder.header("X-Client-Id", clientId);
        }
        HttpRequest request = requestBuilder.build();

        long intervalNanos = Math.max(1L, TimeUnit.SECONDS.toNanos(1) / requestsPerSecond);
        long totalExpectedRequests = requestsPerSecond * Math.max(1L, duration.toSeconds());

        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            ScheduledFuture<?> rateTask = scheduler.scheduleAtFixedRate(() -> {
                if (totalSent.sum() >= totalExpectedRequests) {
                    return;
                }
                totalSent.increment();
                long startNanos = System.nanoTime();

                CompletableFuture<Void> future = httpClient
                        .sendAsync(request, HttpResponse.BodyHandlers.discarding())
                        .handle((response, ex) -> {
                            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
                            latenciesMs.add(elapsedMs);
                            if (ex != null || response == null) {
                                errorCount.increment();
                            } else if (response.statusCode() >= 200 && response.statusCode() < 300) {
                                successCount.increment();
                            } else if (response.statusCode() == 429) {
                                rateLimitedCount.increment();
                            } else {
                                errorCount.increment();
                            }
                            return null;
                        });
                inFlightFutures.add(future);
            }, 0L, intervalNanos, TimeUnit.NANOSECONDS);

            Thread.sleep(duration.toMillis());
            rateTask.cancel(false);
        }

        // Wait up to 10 seconds for all in-flight asynchronous requests to finish
        CompletableFuture.allOf(inFlightFutures.toArray(new CompletableFuture[0]))
                .orTimeout(10, TimeUnit.SECONDS)
                .exceptionally(ex -> null)
                .join();

        return buildReport(
                totalSent.sum(),
                successCount.sum(),
                rateLimitedCount.sum(),
                errorCount.sum(),
                new ArrayList<>(latenciesMs)
        );
    }

    static LoadTestReport buildReport(
            final long totalSent,
            final long successCount,
            final long rateLimitedCount,
            final long errorCount,
            final List<Long> samples
    ) {
        if (samples.isEmpty()) {
            return new LoadTestReport(totalSent, successCount, rateLimitedCount, errorCount, 0, 0.0, 0, 0, 0);
        }
        Collections.sort(samples);
        long min = samples.get(0);
        long max = samples.get(samples.size() - 1);
        double avg = samples.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long p95 = samples.get((int) Math.min(samples.size() - 1, Math.floor(samples.size() * 0.95)));
        long p99 = samples.get((int) Math.min(samples.size() - 1, Math.floor(samples.size() * 0.99)));

        return new LoadTestReport(totalSent, successCount, rateLimitedCount, errorCount, min, avg, p95, p99, max);
    }

    /**
     * CLI entry point to warm up the cache on product-service and generate constant RPS load.
     *
     * @param args optional: [targetUrl] [rps] [durationSeconds]
     */
    public static void main(final String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : "http://localhost:8082/api/v1/products/1";
        int rps = args.length > 1 ? Integer.parseInt(args[1]) : 200;
        int durationSec = args.length > 2 ? Integer.parseInt(args[2]) : 15;

        // First trigger cache warm-up so Old Generation in product-service is populated
        URI warmupUri = URI.create("http://localhost:8082/api/v1/products/cache/warmup?count=50000");
        try {
            HttpClient.newHttpClient().send(
                    HttpRequest.newBuilder().uri(warmupUri).POST(HttpRequest.BodyPublishers.noBody()).build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            log.info("Warmed up product-service cache via {}", warmupUri);
        } catch (Exception e) {
            log.warn("Could not call cache warmup endpoint ({}), proceeding with load test...", e.getMessage());
        }

        log.info("Starting LoadGenerator: target={}, RPS={}, duration={}s", url, rps, durationSec);
        LoadGenerator generator = new LoadGenerator(rps, Duration.ofSeconds(durationSec));
        LoadTestReport report = generator.runLoadTest(URI.create(url), null);

        log.info("==================== LOAD TEST REPORT ====================");
        log.info("Total Sent       : {}", report.totalSent());
        log.info("2xx Success      : {}", report.successCount());
        log.info("429 Rate Limited : {}", report.rateLimitedCount());
        log.info("Errors           : {}", report.errorCount());
        log.info("Latency Min      : {} ms", report.minLatencyMs());
        log.info("Latency Avg      : {} ms", String.format("%.2f", report.avgLatencyMs()));
        log.info("Latency p95      : {} ms", report.p95LatencyMs());
        log.info("Latency p99      : {} ms", report.p99LatencyMs());
        log.info("Latency Max      : {} ms", report.maxLatencyMs());
        log.info("==========================================================");
    }
}
