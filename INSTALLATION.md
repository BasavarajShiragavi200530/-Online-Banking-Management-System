# 🛠️ Apex Online Banking System - Installation & Setup Guide

This document provides complete instructions for compiling, configuring, and running the Apex Online Banking System.

---

## 📋 Prerequisites

Ensure the following tools are installed on your machine:
- **Java Development Kit (JDK 17 or higher)** (`java -version`)
- **Maven 3.8+** (or use the Maven wrapper)
- **MySQL 8.0** (Optional - embedded H2 database mode is available for instant zero-config execution)
- **IntelliJ IDEA / Eclipse / VS Code**

---

## 🗄️ Database Configuration Options

### Option A: Embedded H2 Database (Default Zero-Config Mode)
The project comes pre-configured with Spring Profile `dev` pointing to an in-memory H2 database.
- Database URL: `jdbc:h2:mem:bankingdb`
- Username: `sa` | Password: *(blank)*
- H2 Web Console URL: `http://localhost:8080/h2-console`
- On application boot, `DataInitializer.java` automatically seeds the database with initial admin, customer accounts, and balances.

---

### Option B: MySQL 8 Database Configuration

1. **Start your local MySQL 8 server**.
2. **Execute `database.sql`**:
   Open MySQL Workbench or CLI and run the script located at the project root:
   ```bash
   mysql -u root -p < database.sql
   ```
3. **Switch Profile to `mysql` in `src/main/resources/application.properties`**:
   ```properties
   spring.profiles.active=mysql
   ```
4. **Update Credentials in `src/main/resources/application-mysql.properties`**:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/online_banking_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

---

## 🚀 Building & Running the Application

### 1. Command Line / Terminal

Run the following command from the project root directory:

```bash
# Compile and package
mvn clean package

# Run Spring Boot application
mvn spring-boot:run
```

If using Java directly:
```bash
java -jar target/online-banking-system-1.0.0.jar
```

---

### 2. Running in IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Select **File -> Open** and navigate to `d:\edge\Online Banking System`.
3. Allow IntelliJ to import Maven dependencies automatically.
4. Locate `src/main/java/com/banking/BankingApplication.java`.
5. Right-click `BankingApplication.java` and select **Run 'BankingApplication'**.

---

## 🌐 Accessing the Application

Once started, open your web browser and navigate to:

- **Public Home Page**: [http://localhost:8080/](http://localhost:8080/)
- **Customer / Admin Login**: [http://localhost:8080/login](http://localhost:8080/login)

### Login Credentials:
- **Admin**: `Basavaraj@bank.com` / `Basavaraj@123`
- **Customer 1**: `basu@bank.com` / `Basu@123`
- **Customer 2**: `bharath@bank.com` / `Bharath@123`
