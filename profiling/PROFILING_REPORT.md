# JVM Profiling Report (`product-service`)

## 1. Test Setup & Methodology
- **Target Service:** `product-service` (`-Xms256m -Xmx256m -XX:+UseG1GC`, Java 21 bytecode)
- **Pre-warmed State:** Local `LruCache` pre-warmed with `20,000` product entries (`POST /api/v1/products/cache/warmup?count=20000`)
- **Load Generator:** [`LoadGenerator`](../product-service/src/main/java/com/bobocode/concurrency/loadgen/LoadGenerator.java) sending **300 RPS** asynchronous HTTP requests to `GET /api/v1/products`
- **Profiler:** `async-profiler` (`asprof`) capturing:
  1. **CPU Profile (`-e cpu`):** [`cpu-profile.html`](./cpu-profile.html) and [`cpu-summary.txt`](./cpu-summary.txt)
  2. **Memory Allocation Profile (`-e alloc`):** [`alloc-profile.html`](./alloc-profile.html) and [`alloc-summary.txt`](./alloc-summary.txt)

---

## 2. Comparison: CPU Profile vs. Memory Allocation Profile

| Dimension | CPU Profile (`cpu-profile.html`) | Memory Allocation Profile (`alloc-profile.html`) |
| :--- | :--- | :--- |
| **What it measures** | Where CPU threads spend execution/on-CPU cycles (`522` samples over window) | Which call paths allocate the most Heap memory (`~288 MB` across `550` allocation samples in 4s) |
| **Top Hotspots** | 1. `__psynch_cvwait` / `kevent` (`24.5%` — thread pool park/unpark & NIO poller)<br>2. `write` syscall (`6.70%` — synchronous console/logstash logging)<br>3. G1 GC (`G1HeapRegion`, `G1ParScanThreadState`) & JIT compilation<br>4. `HashMap.getNode` / `ObjectUtils.nullSafeEquals` / `String.getBytes` | 1. `PropagatingReceiverTracingObservationHandler` & Micrometer `KeyValue[]`/`Tag[]` (`~26.5%` of all heap allocations)<br>2. `byte[]` & `String` (`24.0%` — JSON serialization, HTTP buffers, debug log formatting)<br>3. `HashMap$Node` / `LinkedHashMap` / `ConcurrentHashMap` (`~9.6%`)<br>4. Hibernate JPA Criteria (`SqmQuerySpec`, `EntityResultInitializer`, `ResolvableType`) |

---

## 3. Places Taking More Resources Than Expected (Findings & Root Causes)

### Finding 1: 100% Distributed Tracing Sampling (`~26.5%` of All Heap Allocations)
- **Evidence in Allocation Profile:**
  - `io.micrometer.tracing.handler.PropagatingReceiverTracingObservationHandler`: **19.82%** (`57.1 MB` in 4s)
  - `io.micrometer.common.KeyValue[]` (**2.36%**), `io.micrometer.core.instrument.Tag[]` (**1.27%**), `ImmutableTag` (**1.09%**), `KeyValues` (**0.91%**), `brave.baggage.CorrelationUpdateScope$Multiple` (**0.55%**), `SimpleEvent` (**0.55%**)
- **Root Cause:**
  In [`product-service/src/main/resources/application.properties`](../product-service/src/main/resources/application.properties), `management.tracing.sampling.probability=1.0` is set to **100%**. Under 300 RPS, creating Brave spans, baggage scopes, and Micrometer observation tags for every single request consumes over a quarter of total heap allocations.
- **Recommendation:**
  Lower `management.tracing.sampling.probability` to `0.05`–`0.1` (5–10%) in production.

### Finding 2: `debug=true` Synchronous Logging Overhead (`6.70%` CPU in `write` syscall + High `byte[]`/`String` Allocations)
- **Evidence in CPU & Memory Profiles:**
  - CPU profile shows native `write` syscall taking **6.70%** of total CPU time, alongside `AbstractStringBuilder.append` and `String.getBytes`.
  - Allocation profile shows `byte[]` at **19.27%** (`55.5 MB`) and `java.lang.String` at **4.73%** (`13.6 MB`).
- **Root Cause:**
  [`product-service/src/main/resources/application-local.properties`](../product-service/src/main/resources/application-local.properties) sets `debug=true`. On every HTTP request, Spring Security (`FilterChainProxy`), Spring Web DispatcherServlet, and Hibernate format multi-line debug messages and write them synchronously to `stdout`.
- **Recommendation:**
  Disable `debug=true` under load and use asynchronous appenders (`AsyncAppender`) with `INFO`/`WARN` log levels.

### Finding 3: Uncached Paginated Endpoint (`GET /api/v1/products`) + Lazy Category N+1 Queries
- **Evidence in Memory & CPU Profiles:**
  - `org.hibernate.query.sqm.tree.select.SqmQuerySpec`, `org.hibernate.sql.results.graph.entity.internal.EntityResultInitializer`, `org.springframework.core.ResolvableType` (**1.45%**), and `org.h2.mvstore.tx.TxDecisionMaker$LockDecisionMaker` (**0.55%**).
- **Root Cause:**
  1. While `GET /api/v1/products/{id}` uses [`LruCache`](../product-service/src/main/java/com/bobocode/concurrency/cache/LruCache.java), `GET /api/v1/products` in [`MarketPlaceService.getProducts`](../product-service/src/main/java/com/bobocode/services/products/MarketPlaceService.java) rebuilds a JPA Criteria `Specification` via [`GenericSpecificationsBuilder`](../product-service/src/main/java/com/bobocode/services/filter_and_search/GenericSpecificationsBuilder.java) on every call.
  2. In [`Product`](../product-service/src/main/java/com/bobocode/entities/products/Product.java), `@ManyToOne(fetch = FetchType.LAZY) private Category category;` triggers lazy secondary lookups when `ProductMapper` accesses `product.getCategory().getName()`.
- **Recommendation:**
  Use `@EntityGraph(attributePaths = "category")` on `ProductRepository.findAll(Specification, Pageable)` to eliminate N+1 queries, and cache default first-page product listings.

### Finding 4: Unbounded Client Key Map in `RateLimiter` (`ConcurrentHashMap$Node` Allocations)
- **Evidence in Memory Profile:**
  - `java.util.concurrent.ConcurrentHashMap` (**0.91%**), `ConcurrentHashMap$Node` (**0.91%**), `ConcurrentHashMap$Node[]` (**0.73%**).
- **Root Cause:**
  In [`RateLimiter`](../product-service/src/main/java/com/bobocode/concurrency/ratelimiter/RateLimiter.java), `clientTimestamps.computeIfAbsent(clientKey, k -> new ConcurrentLinkedDeque<>())` creates a new deque per unique `X-Client-Id` header and removes expired timestamps inside the deque, but never removes the `clientKey` entry itself from the map when its deque becomes empty.
- **Recommendation:**
  Periodically evict empty deques or back `clientTimestamps` with a bounded LRU/TTL cache.
