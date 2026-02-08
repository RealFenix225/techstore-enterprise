# 🚀 TechStore Enterprise API

![Java](https://img.shields.io/badge/Java-17%20LTS-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=spring)
![Oracle Cloud](https://img.shields.io/badge/Database-Oracle_Cloud-red?style=for-the-badge&logo=oracle)
![Security](https://img.shields.io/badge/Security-JWT_Auth-blue?style=for-the-badge&logo=json-web-tokens)
![Coverage](https://img.shields.io/badge/Coverage-51%25-yellow?style=for-the-badge)

Welcome to the **TechStore Enterprise** backend. This is a robust RESTful API designed for high-level inventory management in a technological retail environment. This project simulates a real-world enterprise scenario, implementing industry best practices, **Hexagonal Architecture**, **Security**, and **Cloud Database Integration**.

---

## 🛠️ Tech Stack

Built using industry-standard technologies to ensure scalability, security, and performance:

* **Core:** Java 17 (LTS), Spring Boot 3.2.2
* **Database:** Oracle Autonomous Database (Oracle Cloud Infrastructure)
* **Security:** Spring Security 6, JWT (Stateless), BCrypt Hashing
* **Persistence:** Spring Data JPA & Hibernate
* **Testing:** JUnit 5, Mockito, JaCoCo (Code Coverage Reports)
* **Code Quality:** SonarLint, Lombok
* **Documentation:** OpenAPI 3.0 (Swagger UI)

---

## 🏗️ Project Architecture

The codebase follows a clean, scalable architecture, layered to ensure decoupling and maintainability:

1.  **Controller Layer:** Handles HTTP requests and exposes documented REST endpoints.
2.  **Service Layer:** Contains business logic, validations (e.g., Stock control), and exception handling.
3.  **Repository Layer:** Manages database interactions via JPA.
4.  **Security Layer:** Manages JWT filters, Authentication, and Authorization.
5.  **DTOs & Mappers:** Ensures secure data transfer without exposing JPA entities directly.

---

## 🔐 Security & Authentication

The API is secured using **Stateless JWT (JSON Web Tokens)**.

* **Public Endpoints:** Swagger UI, Login, Register.
* **Protected Endpoints:** Inventory management, Stock updates (Requires `Bearer Token`).

**Auth Flow:**
1.  Register/Login at `/api/auth/*`.
2.  Receive a `token`.
3.  Use the token in the `Authorization` header: `Bearer <your_token>`.

---

## 📚 API Documentation (Swagger)

Interactive documentation is automatically generated.

👉 **Local Access:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### How to use:
1.  Go to the **Auth** section and login.
2.  Copy the generated **Token**.
3.  Click the green **Authorize** button at the top right.
4.  Paste the token (e.g., `eyJhbGci...`) and click **Authorize**.
5.  Now you can test all protected endpoints (Products, Categories, Providers).

---

## 🧪 Testing & Quality Assurance

This project includes a comprehensive Unit Testing suite covering Services, Mappers, and Security.

To run tests and generate the **Coverage Report**:

```bash
mvn clean test jacoco:report