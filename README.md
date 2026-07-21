# 🛍️ GridShop Console Marketplace

GridShop is a Java console application that simulates an online marketplace with **role-based access control**. The project demonstrates object-oriented programming, layered architecture, authentication, CRUD operations, custom exception handling, and unit testing.

The application is designed to showcase Java Core skills without using a database or external frameworks such as Spring.

---

# ✨ Features

## 🔐 Authentication

- Sign In
- User Registration
- Email format validation
- Unique email validation

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
- Sort products
- Filter products
- Search products
- View user profiles
- View users' shopping carts
- View users' purchase history

---

## 👤 User

- Browse marketplace products
- Sort products
- Filter products
- Search products
- Add products to shopping cart
- Remove products from shopping cart
- Purchase products
- View purchase history
- Edit personal information
- Delete account

---

# 🛒 Product Operations

Products support:

- Sorting by price (Ascending / Descending)
- Sorting alphabetically (A-Z / Z-A)
- Filtering by:
  - First letter
  - Minimum price
  - Maximum price
- Searching by product name

---

# 🏗️ Project Structure

```
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
└── test
    └── java
        └── com.bobocode
            ├── Entities
            ├── Menus
            ├── Services
            ├── Utility
            └── MainTest.java
```

---

# 🏛️ Architecture

The project follows a classic layered architecture.

```
Console (Menus)
        │
        ▼
Business Logic (Services)
        │
        ▼
Entities (Models)
```

### Responsibilities

### Menus
Responsible for user interaction through the console.

### Services
Contain the application's business logic.

### Entities
Represent domain models and application data.

### Utility
Helper and validation classes.

### Exceptions
Custom exceptions for error handling.

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
- User management
- Email validation
- Exception handling

---

# 🛠️ Technologies

### Core

- Java 21
- Maven
- Lombok

### Testing

- JUnit 5
- Mockito
- JaCoCo

### Code Quality

- Checkstyle
- PMD
- SpotBugs
- FindSecBugs

### Java Features

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
- Numeric input
- Gender values

---

# 📦 Default Marketplace

The application starts with two predefined products.

| Product | Price |
|---------|------:|
| Grid Dynamics T-shirt | $20 |
| Grid Dynamics Keychain | $1 |

---

# 🚀 Getting Started

## Clone the repository

```bash
git clone https://github.com/your-username/GridShop.git
```

## Open the project

Open the project using **IntelliJ IDEA** (or any Java IDE with Maven support).

## Build the project

```bash
mvn clean install
```

## Run the application

Run

```
Main.java
```

or

```bash
mvn exec:java
```

(if the Maven Exec Plugin is configured)

---

# 📖 Design Principles

This project demonstrates:

- Object-Oriented Programming (OOP)
- Encapsulation
- Separation of Concerns
- Single Responsibility Principle (SRP)
- Layered Architecture
- Dependency Injection (manual)
- Custom Exception Handling
- Stream API
- Clean Code principles

---

# 🔮 Future Improvements

Possible future enhancements include:

- PostgreSQL integration
- Spring Boot migration
- Spring Data JPA
- Hibernate ORM
- REST API
- JWT Authentication
- Password hashing (BCrypt)
- Shopping cart quantities
- Product categories
- Inventory management
- Order entity instead of Bucket purchase history
- Docker support
- Logging (SLF4J + Logback)
- Pagination
- Product images
- CI/CD pipeline with GitHub Actions

---

# 👨‍💻 Author

**Aleksei Stoianov**

Java Backend Developer

GitHub: https://github.com/StoyanowAlexey