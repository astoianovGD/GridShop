# 🛍️ GridShop — Microservices E-Commerce Platform

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-green?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.3-blue?logo=spring)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?logo=springsecurity)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791?logo=postgresql)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-47A248?logo=mongodb)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-3.8-231F20?logo=apachekafka)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![Terraform](https://img.shields.io/badge/Terraform-AWS_ECS-7B42BC?logo=terraform)
![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF?logo=githubactions)
![Netflix Eureka](https://img.shields.io/badge/Service_Discovery-Eureka-red)
![Spring Cloud Gateway](https://img.shields.io/badge/API_Gateway-Spring_Cloud_Gateway-brightgreen)
![OpenFeign](https://img.shields.io/badge/Feign-Declarative_REST_Client-blueviolet)
![Prometheus](https://img.shields.io/badge/Prometheus-Metrics-E6522C?logo=prometheus)
![Grafana](https://img.shields.io/badge/Grafana-Dashboards-F46800?logo=grafana)
![Elastic](https://img.shields.io/badge/ELK_Stack-Logging-005571?logo=elastic)
![Zipkin](https://img.shields.io/badge/Zipkin-Tracing-FF6B6B)
![JUnit5](https://img.shields.io/badge/JUnit-5-success?logo=junit5)
![JaCoCo](https://img.shields.io/badge/JaCoCo-Coverage-yellow)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

**GridShop** is a modern, distributed e-commerce backend built with **Java 21**, **Spring Boot 3**, and **Spring Cloud**. It follows a **microservices architecture** featuring dynamic Service Discovery, a unified API Gateway, stateless JWT security, synchronous (**OpenFeign** + **Resilience4j**) and asynchronous (**Apache Kafka**) inter-service communication, polyglot persistence (**PostgreSQL**, **MongoDB**, **Redis**), full-stack observability (**ELK**, **Prometheus**, **Grafana**, **Zipkin**), JVM profiling, and automated **GitHub Actions CI/CD** with **Terraform** AWS ECS/Fargate infrastructure.

---

## 🏛️ System Architecture

All external client traffic enters through a single entry point (**API Gateway** on port `8080`). The Gateway discovers target service instances dynamically via **Eureka Server** and distributes the load across active replicas. Inter-service calls are executed synchronously via declarative **OpenFeign** clients (with JWT propagation and **Resilience4j** circuit breakers) and asynchronously via **Apache Kafka** event streams.

```
                                   [ External Clients ]
                                            │
                                            ▼ (HTTP 8080)
                                 ┌──────────────────────┐
                                 │     API Gateway      │
                                 │(Spring Cloud Gateway)│
                                 └──────────┬───────────┘
                                            │
                ┌───────────────────────────┼───────────────────────────┐
                │                           │                           │
                ▼ (lb://user-service)       ▼ (lb://product-service)    ▼ (lb://order-service)
      ┌──────────────────────┐   ┌──────────────────────┐   ┌──────────────────────┐
      │     user-service     │   │   product-service    │   │    order-service     │
      │   (Port 8081/8084)   │   │   (Port 8082/8085)   │   │     (Port 8083)      │
      └──────────┬───────────┘   └──────────┬───────────┘   └──────────┬───────────┘
                 │                          │                          │
                 │  OpenFeign + Resilience4j│  OpenFeign + Resilience4j│
                 ◄──────────────────────────┼──────────────────────────┤
                 │                          ◄──────────────────────────┤
                 │                          │                          │
                 │                          │   Apache Kafka (:9092)   │
                 │                          │◄────────────────────────►│
                 │                          │  (order-created-topic /  │
                 │                          │   product-removed-topic) │
                 ▼                          ▼                          ▼
      ┌────────────────────────────────────────────────────────────────────────────┐
      │  PostgreSQL 17 (:5432)  │  Redis 7 Cache (:6379)  │  MongoDB 7.0 (:27017)  │
      └────────────────────────────────────────────────────────────────────────────┘
      ┌────────────────────────────────────────────────────────────────────────────┐
      │  Eureka (:8761) │ Zipkin (:9411) │ Prometheus (:9090) & Grafana (:3000)    │
      │  ELK Stack: Elasticsearch (:9200), Logstash (:5000), Kibana (:5601)        │
      └────────────────────────────────────────────────────────────────────────────┘
```

---

## 🧩 Microservices & Infrastructure Breakdown

| Service / Component | Port(s) | Description | Key Technologies |
| :--- | :--- | :--- | :--- |
| **`api-gateway`** | `8080` | Single Entry Point, route predicates, reverse proxy, client-side load balancing (`lb://`) | Spring Cloud Gateway, WebFlux, Netty |
| **`eureka-server`** | `8761` | Service Registry & Discovery, heartbeat tracking, health checks | Netflix Eureka Server |
| **`user-service`** | `8081`, `8084` | User registration, profiles, staff management, JWT login & authentication | Spring Security, JWT, JPA, PostgreSQL |
| **`product-service`** | `8082`, `8085` | Products & categories catalog, Redis & LRU caching, MongoDB reviews & analytics, Kafka events, rate limiting | JPA, MongoDB, Redis, Kafka, Concurrency |
| **`order-service`** | `8083` | Shopping buckets, order checkout, history, OpenFeign validation, Kafka event producer/consumer | OpenFeign, Resilience4j, Kafka, JPA |
| **`postgres_db`** | `5432` | Primary relational database storage | PostgreSQL 17 |
| **`mongodb`** | `27017` | NoSQL document store for schema-less product reviews & rating aggregations | MongoDB 7.0 |
| **`redis`** | `6379` | Distributed cache for product & category catalog | Redis 7 Alpine |
| **`kafka`** | `9092`, `29092` | Distributed event streaming broker (KRaft mode) | Apache Kafka 3.8.0 |
| **`zipkin`** | `9411` | Distributed tracing collector and UI | OpenZipkin, Micrometer Tracing |
| **`prometheus` / `grafana`** | `9090` / `3000` | Metrics scraping and visualization dashboards | Prometheus, Grafana |
| **`elasticsearch` / `logstash` / `kibana`** | `9200` / `5000` / `5601` | Centralized structured log aggregation & search | Elastic Stack (ELK) 8.3.3 |
| **`local_pgadmin`** | `5050` | PostgreSQL database management UI | pgAdmin 4 |

---

## ⚡ Key Architectural Patterns & Features

### 1. 🚪 API Gateway & Service Discovery (`api-gateway`, `eureka-server`)
- **Single Entry Point**: Clients interact with `http://localhost:8080`.
- **Dynamic Route Predicates**:
  - `/api/v1/users/**`, `/api/v1/auth/**`, `/api/v1/staff/**` ➡️ `lb://user-service`
  - `/api/v1/products/**`, `/api/v1/categories/**` ➡️ `lb://product-service`
  - `/api/v1/orders/**`, `/api/v1/buckets/**` ➡️ `lb://order-service`
- **Client-Side Load Balancing**: Spring Cloud LoadBalancer resolves instances from **Eureka** (`8761`) across multi-instance replicas (`user-service-2` on `8084`, `product-service-2` on `8085`).

### 2. 🔐 Security, JWT Authentication & RBAC
- **Stateless JWT Authentication**: Users authenticate via `POST /api/v1/auth/login` to receive a signed JWT token (`JWTUtil`, `AuthTokenFilter`).
- **Role-Based Access Control (RBAC)**: Fine-grained authorization using `@PreAuthorize` and `OrderSecurity` for `ROLE_ADMIN`, `ROLE_STAFF`, and `ROLE_USER`.
- **Token Propagation**: `FeignClientInterceptorConfig` automatically forwards the `Authorization: Bearer <token>` header across downstream OpenFeign requests.

### 3. 📨 Event-Driven Messaging with Apache Kafka
- **Asynchronous Decoupling**: Configured in KRaft mode (`apache/kafka:3.8.0`) with partitioned topics (`KafkaConfig`):
  - **`product-removed-topic`**: Published by `product-service` (`ProductEventProducer`) when a product is deleted; consumed by `order-service` (`ProductEventConsumer`) to automatically purge the deleted product from all active shopping buckets.
  - **`order-created-topic`**: Published by `order-service` (`OrderEventProducer`) upon order checkout; consumed by `product-service` (`OrderEventConsumer`) for order activity tracking.

### 4. 🚀 Distributed & Local Caching (`Redis` + Custom `LruCache`)
- **Redis Distributed Cache (`RedisCacheConfig`)**:
  - Caches `productById`, `allProducts`, `categories`, and `categoryById` using `GenericJackson2JsonRedisSerializer` with a 10-minute TTL.
  - Automatic cache eviction (`@CacheEvict`) when products or categories are created, updated, or deleted.
  - **Resilient Fallback**: Custom `CacheErrorHandler` logs Redis connectivity failures and transparently falls back to PostgreSQL instead of failing requests.
- **Custom Thread-Safe `LruCache`**:
  - High-concurrency in-memory cache backed by `ReentrantReadWriteLock` with a synthetic warmup endpoint (`POST /api/v1/products/cache/warmup`) for GC and load testing.

### 5. 🍃 Polyglot Persistence: PostgreSQL + MongoDB (NoSQL)
- **PostgreSQL 17**: Relational storage for core transactional entities (users, products, categories, buckets, orders).
- **MongoDB 7.0 (`com.bobocode.nosql`)**:
  - Stores schema-less product reviews (`ProductReviewDocument` in collection `product_reviews`) supporting arbitrary dynamic `attributes` (`Map<String, Object>`).
  - Performs MongoDB aggregation pipelines (`ProductReviewRepository`) to compute real-time rating averages and review counts (`ProductAnalyticsSummaryDto`).

### 6. 🧵 Concurrency, Rate Limiting & JVM Profiling
- **Sliding-Window Rate Limiter**: `RateLimiter` and `RateLimitingFilter` protect `product-service` endpoints from traffic spikes (`HTTP 429 Too Many Requests`).
- **Concurrency Utilities**: Multi-threaded `LetterStatistics` / `LetterCountTable` processors and an asynchronous HTTP `LoadGenerator`.
- **JVM GC Tuning & Profiling (`profiling/`)**:
  - Configurable G1GC profiles in `docker-compose.yaml` (Baseline, High-Throughput, Low-Latency).
  - Detailed CPU and heap allocation flame graphs (`cpu-profile.html`, `alloc-profile.html`) and bottleneck analysis documented in [`profiling/PROFILING_REPORT.md`](./profiling/PROFILING_REPORT.md).

### 7. 🛡️ Fault Tolerance & Observability Stack
- **Resilience4j**: Circuit Breaker and Retry policies on OpenFeign clients in `order-service` with graceful fallback handlers.
- **Centralized Logging (ELK)**: Microservices stream JSON logs over TCP to **Logstash** (`:5000`), indexed in **Elasticsearch** (`:9200`), and visualized in **Kibana** (`:5601`).
- **Metrics (Prometheus & Grafana)**: Spring Boot Actuator exposes `/actuator/prometheus` endpoints scraped by **Prometheus** (`:9090`) and displayed in **Grafana** (`:3000`).
- **Distributed Tracing (Zipkin)**: Micrometer Tracing + Brave propagate trace/span IDs across HTTP and Kafka boundaries to **Zipkin** (`:9411`).

### 8. ☁️ CI/CD Pipelines & Terraform Cloud Infrastructure
- **GitHub Actions Workflows (`.github/workflows/`)**:
  - `maven_without_aws.yml`: Runs unit/integration tests (`mvn clean test`), packages JARs, and verifies Docker image builds on every push and pull request to `main`.
  - `maven.yml`: Builds and pushes Docker images to **Amazon ECR** and triggers a rolling deployment on **Amazon ECS**.
- **Terraform IaC (`terraform/main.tf`)**:
  - Provisions **Amazon ECR**, **Application Load Balancer (ALB)**, security groups, target groups with `/actuator/health` checks, and an **AWS ECS Fargate** cluster running 2 load-balanced tasks.

---

## 🚀 Getting Started

### Prerequisites
- **Java 21 (JDK)**
- **Maven 3.9+**
- **Docker & Docker Compose**

### Step 1: Clone the Repository
```bash
git clone https://github.com/astoianovGD/GridShop.git
cd GridShop
```

### Step 2: Configure Environment Variables (Optional)
`docker-compose.yaml` provides sensible defaults out of the box, or you can customize values in a `.env` file in the project root:
```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=gridshop

PGADMIN_DEFAULT_EMAIL=admin@gridshop.com
PGADMIN_PASSWORD=admin

EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
```

### Step 3: Build the Multi-Module Project
```bash
mvn clean package -DskipTests
```

### Step 4: Run the System with Docker Compose
Start all microservices, databases (PostgreSQL, MongoDB, Redis), Kafka, and observability tools:
```bash
docker compose up --build -d
```

### Step 5: Access Dashboards & UIs
- **API Gateway**: [http://localhost:8080](http://localhost:8080)
- **Eureka Dashboard**: [http://localhost:8761](http://localhost:8761)
- **Zipkin Tracing UI**: [http://localhost:9411](http://localhost:9411)
- **Prometheus**: [http://localhost:9090](http://localhost:9090)
- **Grafana**: [http://localhost:3000](http://localhost:3000) (`admin` / `admin`)
- **Kibana (Logs)**: [http://localhost:5601](http://localhost:5601)
- **pgAdmin 4**: [http://localhost:5050](http://localhost:5050) (`admin@gridshop.com` / `admin`)

---

## 📡 API Usage & Endpoints (via API Gateway)

> **💡 Note:** All client requests are directed to the **API Gateway** on port **`8080`**. Protected endpoints require an `Authorization: Bearer <JWT>` header obtained from `/api/v1/auth/login`.

### 🔐 Authentication & User Service
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Authenticate user and receive JWT token | Public |
| `POST` | `/api/v1/users` | Register a new user | Public |
| `GET` | `/api/v1/users/{id}` | Get user profile by ID | Authenticated |
| `PUT` | `/api/v1/users/{id}` | Update existing user profile | Authenticated |
| `DELETE` | `/api/v1/users/{id}` | Delete user by ID | `ADMIN` |

### 📦 Product, Category & NoSQL Review Service
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/products` | Get paginated products (`?page=0&size=10&sort=price,asc`) | Public |
| `GET` | `/api/v1/products/{id}` | Get product details by ID (Redis & LRU cached) | Public |
| `POST` | `/api/v1/products` | Create a new product | `ADMIN`, `STAFF` |
| `PUT` | `/api/v1/products/{id}` | Update product by ID (evicts cache) | `ADMIN`, `STAFF` |
| `DELETE` | `/api/v1/products/{id}` | Delete product & publish `ProductRemovedEvent` to Kafka | `ADMIN`, `STAFF` |
| `POST` | `/api/v1/products/cache/warmup` | Warm up local LRU cache (`?count=50000`) | Public |
| `GET` | `/api/v1/categories` | List all product categories (Redis cached) | Public |
| `POST` | `/api/v1/categories` | Create a new category | `ADMIN`, `STAFF` |
| `POST` | `/api/v1/products/{productId}/reviews` | Add schema-less product review in MongoDB | Public |
| `GET` | `/api/v1/products/{productId}/reviews` | Get product reviews (`?minRating=4`) from MongoDB | Public |
| `GET` | `/api/v1/products/{productId}/reviews/analytics` | Get aggregated review stats (avg rating, total reviews) | Public |

### 🛒 Order & Bucket Service
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/buckets/{userId}` | Get items in user's shopping cart | Owner / `ADMIN` |
| `POST` | `/api/v1/buckets/{userId}/items` | Add product to cart (`productId`, `quantity`) | Owner / `ADMIN` |
| `DELETE`| `/api/v1/buckets/{userId}/items/{itemId}` | Remove item from cart | Owner / `ADMIN` |
| `POST` | `/api/v1/orders/{userId}` | Place order from bucket & publish `OrderCreatedEvent` | Owner / `ADMIN` |
| `GET` | `/api/v1/orders/{userId}` | Get order history for user | Owner / `ADMIN` |

---

## 🧪 Testing

The project includes unit, integration, security, messaging, caching, concurrency, and NoSQL tests using **JUnit 5**, **Mockito**, **Spring Security Test**, and **Embedded Kafka**.

### Run All Tests
```bash
mvn clean test
```

### Test Coverage Highlights
- **Security & RBAC Integration Tests**: `SecurityIntegrationTest`, `ProductSecurityIntegrationTest`, `OrderSecurityIntegrationTest` verifying JWT authentication and role permissions.
- **Kafka Messaging Tests**: `ProductMessagingTest` and `OrderMessagingTest` verifying event publishing and consumption.
- **Redis Caching Tests**: `RedisCachingServiceTest` verifying cache hits, evictions, and resilient fallback when Redis is unavailable.
- **MongoDB NoSQL Tests**: `ProductReviewNoSqlTest` verifying dynamic document persistence and rating aggregation pipelines.
- **Concurrency & Rate Limiting Tests**: `ConcurrencyModuleTest` validating `LruCache`, `RateLimiter`, and `LetterStatistics` under multi-threaded load.

---

## 📂 Project Structure

```text
GridShop
├── .github/workflows               # GitHub Actions CI/CD pipelines (AWS ECS & standalone)
├── api-gateway                     # Spring Cloud Gateway (Port 8080)
├── eureka-server                   # Netflix Eureka Service Discovery (Port 8761)
├── user-service                    # Users, Staff & JWT Auth (Ports 8081, 8084)
├── product-service                 # Catalog, Redis/LRU Cache, MongoDB Reviews, Kafka, Concurrency (Ports 8082, 8085)
├── order-service                   # Buckets, Orders, OpenFeign, Resilience4j, Kafka (Port 8083)
├── profiling                       # JVM async-profiler scripts, flame graphs & PROFILING_REPORT.md
├── terraform                       # Terraform AWS infrastructure (ECR, ALB, ECS Fargate)
├── logstash                        # Logstash pipeline & configuration for ELK stack
├── prometheus                      # Prometheus scrape configuration
├── docker/postgres/init.sql        # PostgreSQL schema & seed initialization script
├── docker-compose.yaml             # Full-stack local orchestration
├── pom.xml                         # Root Maven POM (Modules, Checkstyle, SpotBugs, JaCoCo)
└── README.md
```

---

## 👨‍💻 Author

**Alieksiei Stoianov**  
Java Backend Developer  
- GitHub (Work): [@astoianovGD](https://github.com/astoianovGD)  
- GitHub (Personal): [@StoyanowAlexey](https://github.com/StoyanowAlexey)
