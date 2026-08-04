# 🛍️ GridShop

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue)
![Docker](https://img.shields.io/badge/Docker-2496ED)
![JUnit5](https://img.shields.io/badge/JUnit-5-success)
![Mockito](https://img.shields.io/badge/Mockito-Test-green)
![Maven](https://img.shields.io/badge/Maven-3-C71A36)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

GridShop is a **Spring Boot console marketplace application** with **role-based access control**.

The project demonstrates modern Java backend development practices, including layered architecture, dependency injection, Spring JDBC, PostgreSQL integration, Docker, authentication, CRUD operations, custom exception handling, unit testing, and static code analysis.

Although the application uses Spring Boot, it remains a **console application**, making it a good example of combining Java Core concepts with modern backend technologies.

---

# ✨ Features

## 🔐 Authentication

- User registration
- User sign in
- Email format validation
- Unique email validation
- Role-based authorization
- Automatic first administrator creation

---

## 👑 Administrator

Administrator capabilities include:

- View all staff members
- Create new staff members
- Edit staff information
- Remove staff members

---

## 👨‍💼 Staff

Staff members can:

- Browse products
- Search products
- Filter products
- Sort products
- Add products
- Edit products
- Remove products
- Manage categories
- View user profiles
- View users' shopping carts
- View purchase history

---

## 👤 User

Users can:

- Browse products
- Browse products by category
- Search products
- Filter products
- Sort products
- Add products to shopping cart
- Remove products from shopping cart
- Change product quantities
- Purchase products
- View purchase history
- Edit personal profile
- Delete account

---

# 🛒 Product Operations

Supported functionality:

### Search

- Product name

### Filter

- Category
- First letter
- Minimum price
- Maximum price

### Sort

- Price (Ascending)
- Price (Descending)
- Name (A–Z)
- Name (Z–A)

---

# 🗄️ Database

The application stores all data in **PostgreSQL**.

### Database Entities

- Roles
- Users
- Categories
- Products
- Shopping Buckets
- Bucket Items
- Orders
- Order Items

The database is initialized automatically using the SQL script located at:

```text
docker/postgres/init.sql
```

During the first startup Docker automatically creates:

- database schema
- tables
- relationships
- default roles
- default categories

---

# 🐳 Docker

The project contains a complete Docker environment.

### Included services

- PostgreSQL
- pgAdmin 4

Start infrastructure

```bash
docker compose up -d
```

Stop infrastructure

```bash
docker compose down
```

View running containers

```bash
docker ps
```

---

# 🌱 Spring Boot

The project uses Spring Boot for:

- Dependency Injection
- Bean Management
- Application Configuration
- JDBC Configuration
- Connection Pooling (HikariCP)
- Application Startup using `CommandLineRunner`

The console session starts automatically after the Spring context is initialized.

---

# 🏗️ Architecture

The project follows a layered architecture.

```text
Console Menus
        │
        ▼
Business Services
        │
        ▼
Repositories (Spring JDBC)
        │
        ▼
PostgreSQL Database
```

---

## Layers

### Console

Responsible for user interaction.

Examples:

- Login menu
- User menu
- Staff menu
- Administrator menu

---

### Services

Contain business logic.

Examples:

- Authentication
- Product management
- User management
- Shopping cart management
- Purchase processing

---

### Repository Layer

Responsible for communication with PostgreSQL.

Implemented using:

- Spring JDBC
- JdbcTemplate
- Prepared Statements

---

### Entities

Represent domain models.

Examples:

- User
- Staff
- Admin
- Product
- Category
- Bucket
- Order

---

### Utility

Contains helper classes such as:

- Validators
- Input helpers
- Formatting utilities

---

### Exceptions

Contains custom application exceptions.

---

# 🧪 Testing

The project includes comprehensive unit testing.

### Frameworks

- Spring Boot Test
- JUnit 5
- Mockito
- H2 Database

### Tested Components

- Authentication
- Product Services
- User Services
- Category Services
- Utility classes
- Console menus
- Main application

### Test Coverage

Includes testing of:

- CRUD operations
- Validation
- Business logic
- Authentication
- Product filtering
- Product sorting
- Shopping cart
- Purchase logic
- Exception handling

---

# 🛠️ Technologies

## Core

- Java 21
- Spring Boot 3
- Spring JDBC
- PostgreSQL
- HikariCP
- Maven
- Lombok
- Docker
- Docker Compose

---

## Testing

- Spring Boot Test
- JUnit 5
- Mockito
- H2 Database
- JaCoCo

---

## Code Quality

- Checkstyle
- PMD
- SpotBugs
- FindSecBugs

---

## Java Features

- Object-Oriented Programming
- Stream API
- Collections Framework
- BigDecimal
- Dependency Injection
- Custom Exceptions
- Generics
- Java Records (if applicable)
# ✅ Validation

The application validates:

- Email format
- Email uniqueness
- User existence
- Product existence
- Category existence
- Numeric input
- Gender values
- Product quantity

Validation errors are handled using custom exceptions and informative console messages.

---

# 📦 Initial Database Data

During the first startup, the application automatically creates default data.

## Roles

- ADMIN
- STAFF
- USER

## Categories

- Electronics
- Clothing
- Books

The first administrator account is created interactively if no administrator exists in the database.

---

# 🚀 Getting Started

## 1. Clone the repository

```bash
git clone https://github.com/astoianovGD/GridShop.git
```

---

## 2. Navigate to the project

```bash
cd GridShop
```

---

## 3. Configure Environment Variables

Create a `.env` file in the project root and provide your own configuration values.

> **⚠️ Important**
>
> The `.env` file contains sensitive information such as database credentials.
> Never commit this file to version control.
> Make sure it is included in your `.gitignore`.

Example:

```env
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=your_database_name

PGADMIN_DEFAULT_EMAIL=your_email@example.com
PGADMIN_PASSWORD=your_secure_password

DB_URL=jdbc:postgresql://localhost:5432/your_database_name
```

---

## 4. Start PostgreSQL and pgAdmin

```bash
docker compose up -d
```

Verify that the containers are running:

```bash
docker ps
```

---

## 5. Build the project

```bash
mvn clean install
```

---

## 6. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

or run the `Main` class directly from your IDE.

---

## 7. Open pgAdmin (Optional)

After starting Docker, pgAdmin is available at:

```
http://localhost:8080
```

Log in using the credentials specified in your `.env` file.

---

## 8. Time Zone Configuration (Optional)

If PostgreSQL reports an error similar to:

```text
invalid value for parameter "TimeZone"
```

set the JVM time zone.

### IntelliJ IDEA

Open:

```
Run → Edit Configurations
```

Add the following VM option:

```text
-Duser.timezone=Europe/Kyiv
```

Alternatively, the application already sets the default time zone programmatically during startup.

---

# 📂 Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.bobocode
│   │       ├── entities
│   │       ├── repositories
│   │       ├── services
│   │       ├── menus
│   │       ├── utility
│   │       ├── exceptions
│   │       ├── config
│   │       └── Main.java
│   │
│   └── resources
│       └── application.properties
│
├── test
│   └── java
│
├── docker
│   └── postgres
│       └── init.sql
│
├── docker-compose.yml
├── pom.xml
└── .env
```

---

# 📖 Design Principles

This project demonstrates:

- Object-Oriented Programming (OOP)
- SOLID Principles
- Encapsulation
- Separation of Concerns
- Layered Architecture
- Dependency Injection (Spring IoC)
- Repository Pattern
- JDBC Data Access
- Custom Exception Handling
- Clean Code Principles

---

# 🔍 Static Code Analysis

The project uses multiple tools to ensure high code quality.

- Checkstyle
- PMD
- SpotBugs
- FindSecBugs
- JaCoCo

These tools are executed during the Maven verification phase.

Run manually:

```bash
mvn verify
```

---

# 🔮 Future Improvements

Possible future enhancements include:

- Spring Data JPA
- Hibernate ORM
- REST API
- Swagger / OpenAPI
- JWT Authentication
- BCrypt Password Hashing
- Product Images
- Inventory Management
- Pagination
- Logging (SLF4J + Logback)
- Liquibase
- Flyway
- GitHub Actions CI/CD
- Integration Testing
- Testcontainers
- Email Notifications
- Product Reviews
- Favorites / Wishlist

---

# 🤝 Contributing

Contributions, suggestions, and improvements are welcome.

If you would like to contribute:

1. Fork the repository.
2. Create a feature branch.
3. Commit your changes.
4. Open a Pull Request.

---

# 📄 License

This project is intended for educational purposes.

Feel free to use it as a reference for learning Java, Spring Boot, PostgreSQL, and software architecture.

---

# 👨‍💻 Author

**Aleksei Stoianov**

Java Backend Developer

GitHub:
https://github.com/astoianovGD