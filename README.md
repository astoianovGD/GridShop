# 🛍️ GridShop Console Marketplace

GridShop is a Java console application that simulates a simple online marketplace with role-based access control. The project demonstrates object-oriented programming principles, layered architecture, authentication, CRUD operations, and collection manipulation without using a database.

## Features

### Authentication
- Sign In
- User Registration
- Email format validation
- Unique email validation

### Admin
- View all staff members
- Add new staff
- Edit staff information
- Remove staff members

### Staff
- Add products
- Edit products
- Remove products
- Browse products
- Sort products
- Filter products
- Search products
- View user profiles
- View users' shopping carts
- View users' purchase history

### User
- Browse products
- Sort products
- Filter products
- Search products
- Add products to shopping cart
- Remove products from shopping cart
- Purchase products
- View purchase history
- Edit personal profile
- Delete account

---

## Product Operations

Products can be:

- Sorted by price (ascending / descending)
- Sorted alphabetically (A-Z / Z-A)
- Filtered by:
    - First letter
    - Minimum price
    - Maximum price
- Searched by name

---

## Project Structure

```
src
├── Entities
│   ├── Menus
│   ├── Products
│   └── Users
│
├── Services
│   ├── Products
│   └── User
│
├── Utility
├── Exceptions
├── Enums
└── Main.java
```

---

## Architecture

The application follows a layered architecture:

```
Console (Menus)
        │
        ▼
Business Logic (Services)
        │
        ▼
Entities (Models)
```

### Layer Responsibilities

- **Menus** – user interaction.
- **Services** – business logic.
- **Entities** – application models.
- **Utility** – helper classes.
- **Exceptions** – custom exception handling.

---

## Technologies

- Java 17+
- Maven
- Lombok
- Java Collections Framework
- Stream API
- BigDecimal

---

## Validation

The application validates:

- Email format
- Email uniqueness
- Product existence
- User existence
- Numeric input
- Gender values

---

## Default Marketplace

The application starts with two predefined products:

| Product | Price |
|---------|------:|
| Grid Dynamics T-shirt | $20 |
| Grid Dynamics Keychain | $1 |

---

## How to Run

Clone the repository:

```bash
git clone https://github.com/your-username/GridShop.git
```

Open the project in IntelliJ IDEA (or any Java IDE).

Run:

```
Main.java
```

---

## Design Principles

This project demonstrates:

- Object-Oriented Programming (OOP)
- Separation of Concerns
- Single Responsibility Principle (SRP)
- Layered Architecture
- Encapsulation
- Custom Exception Handling
- Java Stream API

---

## Future Improvements

- PostgreSQL integration
- Spring Boot
- Hibernate / JPA
- REST API
- JWT Authentication
- Password hashing
- Product categories
- Shopping cart quantities
- Order entity instead of Bucket history
- Inventory management
- Unit testing (JUnit & Mockito)
- Docker support

---

## Author

**Aleksei Stoianov**

Java Backend Developer

GitHub: https://github.com/StoyanowAlexey