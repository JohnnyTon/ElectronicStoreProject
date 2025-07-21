# Electronic Store

A modular Spring Boot application simulating an electronic store backend, including admin and customer services.

---

## Prerequisites

- Java 17
- Maven 3.8+

---

## Run the Application (Admin and Client)

Each module can be run independently.
The Admin App must be run first to init the H2 database.

### 1. Run the Admin App

```bash
cd ElectronicStoreParent/ElectronicStoreAdmin
./mvnw spring-boot:run
```

### 2. Run the Client App

```bash
cd ElectronicStoreParent/ElectronicStoreClient
./mvnw spring-boot:run
```

Alternatively, you can run both modules from your IDE (e.g., Eclipse) by running the main class in:

- `net.electronicstore.admin.ElectronicStoreAdminApplication`
- `net.electronicstore.client.ElectronicStoreClientApplication`

---

## Run the Tests

Each module has its own set of unit and controller tests.

### 1. Run Admin tests:

```bash
cd ElectronicStoreParent/ElectronicStoreAdmin
./mvnw test
```

### 2. Run Client tests:

```bash
cd ElectronicStoreParent/ElectronicStoreClient
./mvnw test
```

You can also run tests via IDE by right-clicking the test class or package.

---

## H2 In-Memory Database

Both modules use the same in-memory H2 database (`storedb`) for testing and development.  
Access the H2 console (when running) at:

```
http://localhost:8080/h2-console
```

Use these credentials:
- JDBC URL: `jdbc:h2:mem:storedb`
- Username: `sa`
- Password: `123456`

---

## Building the Project

To build the project into a JAR file:
```sh
./mvnw clean package
```
The JAR file will be located in the `target/` directory.

Run the application from the JAR file:
```sh
java -jar target/*.jar
```