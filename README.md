# 🛍️ Krishna Shop — Full Stack E-Commerce Platform

A complete, production-ready full-stack e-commerce web application inspired by Amazon, developed with **Java 17, Spring Boot 3.5, Spring Security, Spring Data JPA, Hibernate, Thymeleaf, Bootstrap 5, and MySQL**.

---

## 🌟 Key Features

### 🛒 Customer Storefront
- **Amazon-Inspired Modern UI**: Dark navigation header, responsive category highlights, banner carousels, and deals.
- **Product Catalog & Search**: Real-time keyword search, category filtering, price range filter, in-stock filter, and multi-option sorting.
- **Product Details**: High-resolution image view, pricing, discount calculation, stock status, ratings, and quantity selector.
- **User Authentication**: Secure user registration & login with BCrypt password hashing and Spring Security role-based authorization.
- **Shopping Cart**: User-specific persistent cart in MySQL, quantity updater with stock guards, and instant subtotal calculation.
- **Checkout & Orders**: Complete shipping address collection, Cash on Delivery (COD) payment option, unique order ID generation (`KS-YYYYMMDD-XXXX`), order history, and order cancellation.

### 🛡️ Admin Dashboard (`/admin`)
- **Executive Analytics**: Live metric cards for total sales revenue, total orders, active customers, and inventory count.
- **Product Management**: Full CRUD operations for products (Title, Category, Price, MRP, Stock, Image URL, Description, Trending/Best Seller flags).
- **Category Management**: Add, update, and delete categories with icons and descriptions.
- **Order Processing**: View orders across the platform, inspect customer details, and update statuses (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`).
- **Customer Directory**: View all registered users and their profiles.

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.5.x, Spring MVC, Spring Security, Spring Data JPA, Hibernate
- **Database**: MySQL / MariaDB (XAMPP / phpMyAdmin)
- **Frontend**: Thymeleaf, Bootstrap 5.3, Bootstrap Icons, HTML5, CSS3, JavaScript
- **Build Tool**: Apache Maven

---

## 🚀 Getting Started

### Prerequisites
1. **Java 17 or higher** installed (`java -version`).
2. **MySQL Server** (e.g. via XAMPP) running on `localhost:3306`.
3. Database `krishnashop_db` will be auto-created and migrated on boot.

### 1-Click Launch (Windows)
Double-click `run.bat` in the project root:
- Checks and starts MySQL daemon if stopped.
- Boots Spring Boot application on port `8080`.
- Automatically opens `http://localhost:8080` in your default browser.

### Credentials (Pre-seeded Data)
| Role | Email | Password |
|---|---|---|
| **Admin** | `admin@krishnashop.com` | `admin123` |
| **Customer** | `user@krishnashop.com` | `user123` |

---

## 📂 Project Architecture

```
krishnashop/
├── src/main/java/com/krishnashop/
│   ├── config/          # WebMvc, security, model attributes, seed data
│   ├── controller/      # Storefront & Admin MVC controllers
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entity models
│   ├── exception/       # Custom exceptions & global handler
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # CustomUserDetails & Spring Security config
│   └── service/         # Service interfaces & business logic
├── src/main/resources/
│   ├── static/          # CSS, JavaScript, and asset files
│   ├── templates/       # Thymeleaf HTML views (storefront & admin)
│   └── application.properties # Spring configuration & MySQL connection
├── pom.xml
└── run.bat
```

---

## 📄 License
This project is open-source and free to use under the [MIT License](LICENSE).
