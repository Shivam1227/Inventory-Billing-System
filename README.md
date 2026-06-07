# Java Inventory & Billing System

A backend REST API for managing store inventory and generating GST-inclusive bills — built with Core Java, Spring Boot, JDBC, and MySQL.

---

## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.2.5
- **Database:** MySQL (via JDBC — no ORM)
- **Build Tool:** Maven
- **Testing:** Postman

---

## What It Does

- Add, update, search, and delete products across 3 categories
- Track real-time stock with configurable low-stock alerts
- Generate bills with automatic GST calculation per category
- Export formatted receipts as `.txt` files per transaction
- Expose all operations via a clean REST API

---

## Project Architecture

```
Controller → Service → DAO → MySQL
                 ↓
           .txt Bill File
```

| Layer | Package | Responsibility |
|---|---|---|
| Model | `model/` | OOP class hierarchy (abstract `Product` + 3 subclasses) |
| DAO | `dao/` | All JDBC database operations |
| Service | `service/` | Business logic, validation, GST calculation |
| Controller | `controller/` | REST API endpoints |

---

## OOP Design — Product Hierarchy

```
Product  (abstract)
├── Electronics   → 18% GST
├── Grocery       → 5%  GST
└── Clothing      → 12% GST
```

Each subclass implements `getGstRate()` — demonstrating **polymorphism** and **abstraction**.

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/products` | Get all products |
| `GET` | `/api/products/{id}` | Get product by ID (includes GST price) |
| `GET` | `/api/products/category/{category}` | Filter by ELECTRONICS / GROCERY / CLOTHING |
| `GET` | `/api/products/search?keyword=` | Search products by name |
| `GET` | `/api/products/low-stock` | Products below threshold |
| `POST` | `/api/products` | Add a new product |
| `PUT` | `/api/products/{id}/stock` | Update stock quantity |
| `DELETE` | `/api/products/{id}` | Delete a product |
| `POST` | `/api/billing/generate` | Generate a bill + export `.txt` receipt |

---

## Setup & Run

### 1. Prerequisites
- Java 17+
- MySQL running locally
- Maven

### 2. Database
```sql
CREATE DATABASE inventory_db;
```
Then run the full SQL script from `src/main/resources/schema.sql`.

### 3. Configure
Copy the example properties file and fill in your password:
```bash
cp src/main/resources/application-example.properties \
   src/main/resources/application.properties
```
Then edit `application.properties` with your MySQL password.

### 4. Run
```bash
./mvnw spring-boot:run
```
App starts at `http://localhost:8080`

---

## Sample Requests

**Add a product**
```json
POST /api/products
{
  "name": "Wireless Mouse",
  "category": "ELECTRONICS",
  "price": 799,
  "stock": 30,
  "threshold": 5
}
```

**Generate a bill**
```json
POST /api/billing/generate
[
  { "productId": 1, "quantity": 2 },
  { "productId": 5, "quantity": 1 }
]
```

**Response:**
```json
{
  "success": true,
  "billNumber": "B-20240528-143022",
  "subtotal": "1518.00",
  "totalGst": "288.60",
  "grandTotal": "1806.60",
  "billFile": "bills/B-20240528-143022.txt"
}
```

---

## Sample Bill Output

```
============================================================
              INVENTORY & BILLING SYSTEM
                  All In One Store
============================================================
Bill No : B-20260608-003616
Date    : 08-Jun-2026 00:36:16
------------------------------------------------------------
Product                   Qty   Price      Total     
------------------------------------------------------------
Bluetooth Speaker         2     1299.00    3065.64   
   GST @ 18% : 467.64
Sports Shoes              1     1899.00    2126.88   
   GST @ 12% : 227.88
------------------------------------------------------------
Subtotal (Excl. GST)                        4497.00
Total GST                                    695.52
============================================================
GRAND TOTAL                                 5192.52
============================================================
         Thank You For Your Purchase!
============================================================

```

---

## Author

**Shivam Pal** — [LinkedIn](https://www.linkedin.com/in/shivam-pal-a8408b290/) · [GitHub](https://github.com/Shivam1227)
