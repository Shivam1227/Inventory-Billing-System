# 🛒 Inventory & Billing System

A Java-based Inventory Management and Billing System built using **Spring Boot**, **JDBC**, and **MySQL**. The project provides REST APIs for managing products, tracking inventory, generating bills, calculating GST, and exporting receipts as text files.

## 🚀 Features

* Product Management (Add, View, Search, Delete)
* Category-based Product Organization
* Inventory Tracking
* Low Stock Alerts
* GST Calculation by Product Category
* Bill Generation
* Automatic Stock Deduction After Purchase
* Bill Export to `.txt` File
* REST API Architecture
* MySQL Database Integration using JDBC

## 🛠️ Tech Stack

* Java 17
* Spring Boot
* JDBC
* MySQL
* Maven
* REST APIs
* Postman

## 📂 Project Structure

```text
src/main/java/com/inventory/billing_system
├── controller
├── service
├── dao
├── model
└── BillingSystemApplication.java
```

## 📊 Product Categories

| Category    | GST |
| ----------- | --- |
| Electronics | 18% |
| Grocery     | 5%  |
| Clothing    | 12% |

## 🔗 API Endpoints

| Method | Endpoint                            | Description        |
| ------ | ----------------------------------- | ------------------ |
| GET    | `/api/products`                     | Get all products   |
| GET    | `/api/products/{id}`                | Get product by ID  |
| GET    | `/api/products/category/{category}` | Filter by category |
| GET    | `/api/products/search?keyword=`     | Search products    |
| GET    | `/api/products/low-stock`           | Low stock alert    |
| POST   | `/api/products`                     | Add product        |
| PUT    | `/api/products/{id}/stock`          | Update stock       |
| DELETE | `/api/products/{id}`                | Delete product     |
| POST   | `/api/billing/generate`             | Generate bill      |

## ⚙️ Setup

1. Clone the repository

```bash
git clone <repository-url>
cd Inventory-Billing-System
```

2. Create `application.properties` from the example file

```bash
cp src/main/resources/application-example.properties \
src/main/resources/application.properties
```

3. Update your MySQL credentials in `application.properties`

4. Create the database using the provided SQL script

5. Run the application

```bash
./mvnw spring-boot:run
```

## 📄 Sample Bill Output

```text
Bill No : B-20260608-001859
Date    : 08-Jun-2026

Laptop Stand x2
Basmati Rice 5kg x1

Subtotal : 1518.00
GST      : 231.64

GRAND TOTAL : 1749.64
```

## 🎯 Learning Outcomes

* Object-Oriented Programming
* Spring Boot REST APIs
* JDBC Database Connectivity
* Maven Project Management
* Layered Architecture (Controller → Service → DAO → Database)
* File Handling in Java
* Inventory & Billing Workflows

## 👨‍💻 Author

**Shivam Pal**
