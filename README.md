# Smart Supply Chain & Inventory Management System

## Overview

This project is a comprehensive Java-based simulation of a supply chain and inventory management system. It is designed using core Object-Oriented principles to model the interactions between different actors in a supply chain, such as suppliers, warehouse operators, and retailers. The system aims to solve common inventory challenges like inefficient stock tracking and demand forecasting by implementing several smart features.

This application runs on the command line and demonstrates a robust backend architecture for managing products, orders, and system-wide reporting.

---

## Key Features

* **Object-Oriented Architecture:** The system is built on a strong foundation of OOP principles, including abstraction, inheritance, and polymorphism to model real-world supply chain entities.
* **Role-Based Simulation:** Distinct classes for `ProductSupplier`, `WarehouseOperator`, `ProductRetailer`, and `SystemAdministrator` ensure a clear separation of concerns and realistic simulation of roles.
* **AI-Powered Demand Forecasting:** A smart module analyzes historical sales data using a **Simple Moving Average (SMA)** algorithm to predict future product demand, helping to prevent stockouts and optimize inventory levels.
* **Barcode Scanning Integration:** Leverages the **ZXing (Zebra Crossing)** library to simulate barcode scanning for efficient and error-free stock updates. The system can decode a product ID from a barcode image to update inventory counts.
* **Multithreaded Reporting:** To ensure the application remains responsive, system-wide inventory reports are generated on a separate background thread using Java's `ExecutorService`, preventing the main application from freezing during I/O-heavy operations.
* **File-Based Reporting & Logging:** The `SystemAdministrator` can generate detailed `inventory_report.txt` files and maintain an `admin_log.txt` for system monitoring and traceability.

---

## Technologies Used

* **Language:** Java
* **Core Concepts:** Object-Oriented Programming (OOP), Data Structures, Multithreading, File I/O
* **External Libraries:**
    * **ZXing (Zebra Crossing):** For barcode image decoding. (`core-3.5.1.jar`, `javase-3.5.1.jar`)

---
