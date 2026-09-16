# 🛍️ GridShop — Microservices E-Commerce Platform

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-green?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.3-blue?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![Netflix Eureka](https://img.shields.io/badge/Service_Discovery-Eureka-red)
![Spring Cloud Gateway](https://img.shields.io/badge/API_Gateway-Spring_Cloud_Gateway-brightgreen)
![OpenFeign](https://img.shields.io/badge/Feign-Declarative_REST_Client-blueviolet)
![JUnit5](https://img.shields.io/badge/JUnit-5-success?logo=junit5)
![Mockito](https://img.shields.io/badge/Mockito-Test-green)
![JaCoCo](https://img.shields.io/badge/JaCoCo-Coverage-yellow)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

**GridShop** is a modern, distributed e-commerce backend built with **Java 21**, **Spring Boot 3**, and **Spring Cloud**. It follows a **microservices architecture** featuring dynamic Service Discovery, a unified API Gateway, declarative inter-service communication, client-side load balancing, containerized multi-instance deployment, and PostgreSQL data persistence.

---

## 🏛️ System Architecture

All external client traffic (Web / Mobile / Postman) enters through a single entry point (**API Gateway** on port `8080`). The Gateway discovers target service instances dynamically via **Eureka Server** and distributes the load across active replicas. Inter-service calls (e.g., `order-service` calling `user-service` and `product-service`) are executed via declarative **OpenFeign** clients with client-side load balancing.

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
      │ Queries Instances         │ Queries Instances         │ Queries Instances
      ▼                           ▼                           ▼
┌──────────────┐          ┌──────────────┐            ┌──────────────┐
│Eureka Server │          │Eureka Server │            │Eureka Server │
│ (Port 8761)  │          │ (Port 8761)  │            │ (Port 8761)  │
└──────────────┘          └──────────────┘            └──────────────┘
      ▲                           ▲                           ▲
      │ Registers                 │ Registers                 │ Registers
      │                           │                           │
      ▼ (lb://user-service)       ▼ (lb://product-service)    ▼ (lb://order-service)
┌──────────────────────┐   ┌──────────────────────┐   ┌──────────────────────┐
│     user-service     │   │   product-service    │   │    order-service     │
│   (Port 8081/8084)   │   │   (Port 8082/8085)   │   │     (Port 8083)      │
└──────────┬───────────┘   └──────────┬───────────┘   └──────────┬───────────┘
           │                          │                          │
           │                          ▲                          │
           │                          │ OpenFeign                │ OpenFeign
           │                          └──────────────────────────┤
           │                                                     │
           │─────────────────────────────────────────────────────┘
           │
           ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                       PostgreSQL 17 Database (:5432)                       │
│                     pgAdmin 4 Web Console (:5050)                          │
└────────────────────────────────────────────────────────────────────────────┘
```

---

## 🧩 Microservices Breakdown

| Service | Port(s) | Description | Key Technologies |
| :--- | :--- | :--- | :--- |
| **`api-gateway`** | `8080` | Single Entry Point, route predicates, reverse proxy, client-side load balancing (`lb://`) | Spring Cloud Gateway, WebFlux, Netty |
| **`eureka-server`**| `8761` | Service Registry & Discovery, heartbeat tracking, health checks | Netflix Eureka Server |
| **`user-service`** | `8081`, `8084` | User registration, profiles, roles, authentication management | Spring Data JPA, Hibernate, PostgreSQL |
| **`product-service`** | `8082`, `8085` | Products & categories catalog, filtering, sorting, pagination | Spring Data JPA, Hibernate, PostgreSQL |
| **`order-service`** | `8083` | Shopping buckets, order checkout, history, inter-service validation | Spring Cloud OpenFeign, LoadBalancer |
| **`postgres_db`** | `5432` | Relational database storage | PostgreSQL 17 |
| **`local_pgadmin`** | `5050` | Database management UI | pgAdmin 4 |

---

## ⚡ Key Architectural Patterns & Features

### 1. 🚪 API Gateway Pattern (`api-gateway`)
- **Single Entry Point**: Clients interact exclusively with `http://localhost:8080`.
- **Intelligent Routing**: Dynamic route predicates:
  - `/api/v1/users/**` ➡️ `lb://user-service`
  - `/api/v1/products/**`, `/api/v1/categories/**` ➡️ `lb://product-service`
  - `/api/v1/orders/**`, `/api/v1/buckets/**` ➡️ `lb://order-service`
- **Reactive & Non-blocking**: Built on Project Reactor and Netty for high-throughput request handling.

### 2. 📡 Service Discovery (`eureka-server`)
- Dynamic service registration on startup (`@EnableDiscoveryClient` / `@EnableEurekaServer`).
- Automated health monitoring via periodic heartbeats.
- Decouples microservice locations from static IP addresses and ports.
- Web dashboard accessible at `http://localhost:8761`.

### 3. ⚖️ Client-Side Load Balancing
- **Spring Cloud LoadBalancer**: Seamlessly integrates with Eureka to resolve service instances.
- **Round-Robin** default load balancing in `api-gateway`.
- **Custom Load Balancing Algorithms**: Pluggable balancing strategies (e.g., `RandomLoadBalancer`) via `@LoadBalancerClients`.
- **Multi-Instance Support**: Replicas configured in Docker Compose (`user-service-2` on `8084`, `product-service-2` on `8085`).

### 4. 🔗 Declarative Inter-Service Communication (`order-service`)
- Replaced manual HTTP calls with **Spring Cloud OpenFeign** interfaces:
  - `UserClient`: Validates user existence before creating orders.
  - `ProductClient`: Retrieves live product details (name, price) when managing buckets and checkout.
- Fully integrated with client-side load balancing.

### 5. 🗄️ Persistence & Data Access
- **Spring Data JPA & Hibernate**: Object-relational mapping with automated DDL and repository abstraction.
- **PostgreSQL 17**: Production-ready relational storage.
- Auto-initialization schema and seed data located at `docker/postgres/init.sql`.

### 6. 🛡️ Validation & Exception Handling
- Declarative input validation with Jakarta Bean Validation (`@Valid`, `@NotNull`, `@NotBlank`, `@Email`, etc.).
- Centralized `@RestControllerAdvice` returning standardized RFC-compliant error payloads.

---

## 🚀 Getting Started

### Prerequisites
- **Java 21 (JDK)**
- **Maven 3.9+**
- **Docker & Docker Compose**

---

### Step 1: Clone the Repository
```bash
git clone https://github.com/astoianovGD/GridShop.git
cd GridShop
```

---

### Step 2: Configure Environment Variables
Create a `.env` file in the project root:
```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=gridshop

PGADMIN_DEFAULT_EMAIL=admin@gridshop.com
PGADMIN_PASSWORD=admin

EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
```

---

### Step 3: Build the Multi-Module Project
Build all service JAR artifacts using Maven:
```bash
mvn clean package -DskipTests
```

---

### Step 4: Run the System with Docker Compose
Start all microservices, Eureka, PostgreSQL, and pgAdmin with a single command:
```bash
docker compose up --build -d
```

Verify that all containers are running:
```bash
docker ps
```

---

### Step 5: Verify Eureka Registration
Open your browser and navigate to the Eureka Dashboard:
👉 **[http://localhost:8761](http://localhost:8761)**

You should see all services registered:
- `API-GATEWAY`
- `USER-SERVICE` (2 instances)
- `PRODUCT-SERVICE` (2 instances)
- `ORDER-SERVICE`

---

## 📡 API Usage & Endpoints (via API Gateway)

> **💡 Note:** All client requests are directed to the **API Gateway** on port **`8080`**.

### 👤 User Service
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/users/{id}` | Get user by ID |
| `POST` | `/api/v1/users` | Register a new user |
| `PUT` | `/api/v1/users/{id}` | Update existing user profile |
| `DELETE` | `/api/v1/users/{id}` | Delete user by ID |

**Example:**
```bash
curl -i http://localhost:8080/api/v1/users/1
```

---

### 📦 Product & Category Service
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/products` | Get paginated products (`?page=0&size=10&sort=price,asc`) |
| `GET` | `/api/v1/products/{id}` | Get product details by ID |
| `POST` | `/api/v1/products` | Create a new product |
| `GET` | `/api/v1/categories` | List all product categories |
| `POST` | `/api/v1/categories` | Create a new category |

**Example:**
```bash
curl -i http://localhost:8080/api/v1/products
curl -i http://localhost:8080/api/v1/categories
```

---

### 🛒 Order & Bucket Service
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/buckets/{userId}` | Get items in user's shopping cart |
| `POST` | `/api/v1/buckets/{userId}/items` | Add product to cart (`productId`, `quantity`) |
| `DELETE`| `/api/v1/buckets/{userId}/items/{itemId}` | Remove item from cart |
| `POST` | `/api/v1/orders/{userId}` | Place order from current bucket |
| `GET` | `/api/v1/orders/{userId}` | Get order history for user |

**Example:**
```bash
curl -i http://localhost:8080/api/v1/buckets/1
```

---

### 🗄️ pgAdmin Web Console
- URL: **[http://localhost:5050](http://localhost:5050)**
- Email: `admin@gridshop.com` (or value from `.env`)
- Password: `admin` (or value from `.env`)

---

## 🧪 Testing

The repository contains automated unit and integration tests using JUnit 5, Mockito, and Spring Boot Test.

### Run All Tests
```bash
mvn clean test
```

### Test Coverage Highlights
- **Service Layer Mock Tests**: Mockito tests verifying business logic, validation rules, and error handling.
- **Feign Client Integration**: Validating data enrichment between `order-service`, `product-service`, and `user-service`.
- **Load Balancer Configuration**: Unit testing custom load balancing algorithm providers.
- **Context Bootstrapping**: Smoke tests verifying Spring context startup for all modules including `api-gateway` and `eureka-server`.
- **Code Coverage**: Enforced using **JaCoCo**.

---

## 📂 Project Structure

```text
GridShop
├── api-gateway                     # Spring Cloud Gateway (Port 8080)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/resources/application.yaml
│
├── eureka-server                   # Netflix Eureka Service Discovery (Port 8761)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/resources/application.properties
│
├── user-service                    # User management & authentication (Ports 8081, 8084)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/bobocode/...
│
├── product-service                 # Products & categories catalog (Ports 8082, 8085)
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/bobocode/...
│
├── order-service                   # Buckets, Orders & OpenFeign clients (Port 8083)
│   ├── Dockerfile
│   ├── pom.xml
│   ├── src/main/java/com/bobocode/clients/...
│   └── src/test/java/com/bobocode/...
│
├── docker
│   └── postgres
│       └── init.sql                # Database initialization script
│
├── docker-compose.yaml             # Complete orchestration configuration
├── pom.xml                         # Root Maven POM (Modules & Dependency Management)
└── README.md
```

---

## 👨‍💻 Author

**Alieksiei Stoianov**  
Java Backend Developer  
- GitHub (Work): [@astoianovGD](https://github.com/astoianovGD)  
- GitHub (Personal): [@StoyanowAlexey](https://github.com/StoyanowAlexey)
