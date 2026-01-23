# TechStore Enterprise API

Welcome to the **TechStore Enterprise** backend. This is a robust RESTful API designed for high-level inventory management in a technological retail environment. This project simulates a real-world enterprise scenario, implementing industry best practices, layered architecture, and secure cloud database integration.

## Tech Stack

Built using industry-standard technologies to ensure scalability and performance:

* **Language:** Java 17 (LTS)
* **Framework:** Spring Boot 3.2.2
* **Database:** Oracle Autonomous Database (Oracle Cloud)
* **DB Security:** Oracle Wallet (Mutual TLS connection)
* **Persistence:** Spring Data JPA & Hibernate
* **Tools:** Lombok, Maven
* **Documentation:** OpenAPI 3.0 (Swagger UI)

## Project Architecture

The codebase follows a clean, scalable architecture, layered to ensure decoupling and maintainability:

* **Controller Layer:** Handles HTTP requests and REST endpoints.
* **Service Layer:** Contains business logic and validation rules.
* **Repository Layer:** Manages database interactions via JPA.
* **DTO (Data Transfer Objects):** Ensures secure data transfer without exposing JPA entities.
* **Exception Handling:** Global error handling via `@ControllerAdvice` for consistent JSON responses.

## API Endpoints (Swagger)

Interactive documentation is automatically generated.
If running locally, access it at:

`http://localhost:8080/swagger-ui.html`

### Core Features:
* **Products:** Full CRUD, pagination, and search filtering.
* **Categories:** Inventory classification and management.
* **Suppliers:** Supply chain administration.

## Installation & Setup

To run this project on your local machine:

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/RealFenix225/techstore-enterprise.git](https://github.com/RealFenix225/techstore-enterprise.git)
    ```

2.  **Database Configuration:**
    * This project uses **Oracle Cloud**. You will need your own instance and `Oracle Wallet`.
    * Place your unzipped `Wallet_...` folder in the project root.
    * Configure `src/main/resources/application.properties` with your Wallet path and credentials.

3.  **Build and Run:**
    ```bash
    ./mvnw spring-boot:run
    ```

## Author

**César Gálvez** - *Backend Developer*
* [LinkedIn Profile](https://www.linkedin.com/in/cesar-galvez)
* [GitHub Profile](https://github.com/RealFenix225)

---