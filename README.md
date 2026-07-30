# 🛍️ GridShop Console Marketplace

GridShop is a Java console marketplace application with **role-based access control**. The project demonstrates object-oriented programming, layered architecture, authentication, CRUD operations, PostgreSQL integration, Docker, custom exception handling, and unit testing.

The application is built using **Java Core** with **PostgreSQL** for data persistence and follows a clean layered architecture without using external frameworks such as Spring.

---

# ✨ Features

## 🔐 Authentication

- User Registration
- Sign In
- Email format validation
- Unique email validation
- Role-based authorization

---

## 👑 Admin

- View all staff members
- Add new staff members
- Edit staff information
- Remove staff members

---

## 👨‍💼 Staff

- Browse marketplace products
- Add products
- Edit products
- Remove products
- Manage product categories
- Sort products
- Filter products
- Search products
- View user profiles
- View users' shopping carts
- View users' purchase history

---

## 👤 User

- Browse marketplace products
- Browse products by category
- Sort products
- Filter products
- Search products
- Add products to shopping cart
- Remove products from shopping cart
- Change product quantities
- Purchase products
- View purchase history
- Edit personal information
- Delete account

---

# 🛒 Product Operations

Products support:

- Searching by product name
- Filtering by:
  - Category
  - First letter
  - Minimum price
  - Maximum price
- Sorting by:
  - Price (Ascending / Descending)
  - Name (A–Z / Z–A)

---

# 🗄️ Database

The application uses **PostgreSQL** for persistent data storage.

### Main Entities

- Roles
- Users
- Categories
- Products
- Shopping Buckets
- Bucket Items
- Orders
- Order Items

The database schema is initialized automatically using **init.sql** when the Docker container starts for the first time.

---

# 🐳 Docker

The project includes a complete Docker Compose configuration.

### Services

- PostgreSQL
- pgAdmin 4

Start the application infrastructure:

```bash
docker compose up -d
```

Stop containers:

```bash
docker compose down
```

---

# 🏗️ Project Structure

```text
src
├── main
│   └── java
│       └── com.bobocode
│           ├── Entities
│           │   ├── Menus
│           │   ├── Products
│           │   └── Users
│           │
│           ├── Services
│           │   ├── Products
│           │   └── User
│           │
│           ├── Utility
│           ├── Exceptions
│           ├── Enums
│           └── Main.java
│
├── docker
│   └── postgres
│       └── init.sql
│
├── docker-compose.yml
└── .env
```

---

# 🏛️ Architecture

```
Console (Menus)
        │
        ▼
Business Logic (Services)
        │
        ▼
Database Layer (JDBC)
        │
        ▼
PostgreSQL
```

### Responsibilities

### Menus

Responsible for user interaction through the console.

### Services

Contain the application's business logic.

### Database

Handles data persistence using PostgreSQL.

### Entities

Represent domain models and application data.

### Utility

Contains helper and validation classes.

### Exceptions

Contains custom exceptions for error handling.

---

# 🧪 Testing

The project includes comprehensive **unit testing** using **JUnit 5** and **Mockito**.

### Tested Components

- Product Services
- User Services
- Authentication
- Utility classes
- Entity models
- Console menus
- Main class

### Test Coverage Includes

- Business logic
- CRUD operations
- Authentication
- Product filtering
- Product sorting
- Category operations
- Shopping cart logic
- Purchase operations
- User management
- Email validation
- Exception handling

---

# 🛠️ Technologies

## Core

- Java 21
- Maven
- PostgreSQL
- JDBC
- Lombok
- Docker
- Docker Compose

## Testing

- JUnit 5
- Mockito
- JaCoCo

## Code Quality

- Checkstyle
- PMD
- SpotBugs
- FindSecBugs

## Java Features

- Stream API
- Collections Framework
- BigDecimal
- Custom Exceptions

---

# ✅ Validation

The application validates:

- Email format
- Email uniqueness
- Product existence
- User existence
- Category existence
- Numeric input
- Gender values
- Product quantity

---

# 📦 Initial Database Data

The application automatically creates default data during database initialization.

### Roles

- ADMIN
- STAFF
- USER

### Categories

- Electronics
- Clothing
- Books

---

# 🚀 Getting Started

## 1. Clone the repository

```bash
git clone https://github.com/StoyanowAlexey/GridShop.git
```

## 2. Navigate to the project

```bash
cd GridShop
```

## 3. Configure Environment Variables

Create a `.env` file in the project root and provide your own configuration values.

> **⚠️ Important:** The `.env` file contains sensitive information (such as database credentials) and **must not** be committed to version control. Make sure it is included in your `.gitignore` file.

Example:

```env
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=your_database_name

PGADMIN_DEFAULT_EMAIL=your_email@example.com
PGADMIN_PASSWORD=your_pgadmin_password

DB_URL=jdbc:postgresql://localhost:5432/your_database_name
```

## 4. Start PostgreSQL and pgAdmin

```bash
docker compose up -d
```

## 5. Build the project

```bash
mvn clean install
```

## 6. Run the application

Run

```
Main.java
```

or

```bash
mvn exec:java
```

---
## 7. Time Zone (Optional)

If you encounter a PostgreSQL error related to the time zone (for example, `invalid value for parameter "TimeZone"`), add the following VM option to your IDE run configuration:

```text
-Duser.timezone=Europe/Kyiv
```

### IntelliJ IDEA

1. Open **Run → Edit Configurations...**
2. Select your application run configuration.
3. In the **VM options** field, add:

```text
-Duser.timezone=Europe/Kyiv
```

Then run the application again.

# 📖 Design Principles

This project demonstrates:

- Object-Oriented Programming (OOP)
- Encapsulation
- Separation of Concerns
- Single Responsibility Principle (SRP)
- Layered Architecture
- Manual Dependency Injection
- JDBC Data Access
- Custom Exception Handling
- Stream API
- Clean Code principles

---

# 🔮 Future Improvements

- Spring Boot
- Spring Data JPA
- Hibernate ORM
- REST API
- JWT Authentication
- BCrypt Password Hashing
- Inventory Management
- Product Images
- Logging (SLF4J + Logback)
- Pagination
- Liquibase
- GitHub Actions CI/CD
- Integration Testing
- Testcontainers

---

# 👨‍💻 Author

**Aleksei Stoianov**

Java Backend Developer

GitHub: https://github.com/StoyanowAlexey