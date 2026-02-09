# TechStore Enterprise API

![Java](https://img.shields.io/badge/Java-17%20LTS-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green?style=for-the-badge&logo=spring)
![Azure](https://img.shields.io/badge/Azure-App_Service-0078D4?style=for-the-badge&logo=microsoftazure&logoColor=white)
![Oracle](https://img.shields.io/badge/Database-Oracle_Cloud-red?style=for-the-badge&logo=oracle)
![Status](https://img.shields.io/badge/Status-Live_Demo_Available-success?style=for-the-badge)

**TechStore Enterprise** is an advanced academic project designed to simulate a real-world e-commerce backend.

I developed this API to challenge myself and apply industry standards like **Clean Code**, **Cloud Deployment**, and **Security**. It represents my journey from learning Java fundamentals to deploying a functional application in the Cloud.

---

## ☁️ Live Demo (Deployed on Azure)

The application is currently deployed on **Microsoft Azure (Spain Central Region)** and connected to an **Oracle Autonomous Database**.

👉 **[ACCESS LIVE SWAGGER UI DOCUMENTATION](https://techstore-api-v2-axcreydeg0bvb3bj.spaincentral-01.azurewebsites.net/swagger-ui/index.html)**

> **Note:** This is a portfolio project running on a Basic Tier. Please be respectful with the data.

---

## 🎯 Project Goal

The main objective of this project is to demonstrate competency in:
1.  **Building RESTful APIs** with Spring Boot 3.
2.  **Connecting to Cloud Databases** (Oracle) securely.
3.  **Implementing Authentication** with Spring Security & JWT.
4.  **Deploying Java Apps** to a PaaS environment (Azure App Service).

---

## 🏗️ Technical Architecture

I implemented a **Layered Architecture** to ensure the code is organized and maintainable:

1.  **Controller Layer:** Exposes the endpoints documented with OpenAPI (Swagger).
2.  **Service Layer:** Handles business logic (e.g., Stock validation).
3.  **Repository Layer:** Manages data access using **Spring Data JPA**.
4.  **Security Layer:** Custom implementation of JWT Authentication.

**Key Concepts Applied:**
* **DTO Pattern:** To separate internal entities from API responses.
* **Dependency Injection:** Using Spring's IoC container.
* **Global Exception Handling:** Managing errors gracefully with `@ControllerAdvice`.

---

## 🛠️ Tech Stack

* **Language:** Java 17 (LTS)
* **Framework:** Spring Boot 3.2.2
* **Cloud Infrastructure:** Azure App Service (Linux)
* **Database:** Oracle Autonomous Database 19c
* **Security:** JWT & BCrypt
* **Tools:** Maven, Git, Postman/Swagger

---

## 🔐 How to Test (Security)

The API has public and protected endpoints. To test the secure ones:

1.  Go to `POST /auth/register` and create a user.
2.  Copy the `token` from the response.
3.  Click the **Authorize** button (lock icon).
4.  Type: `Bearer YOUR_TOKEN` (replace with actual token) and click **Authorize**.

---

## 🚀 Roadmap (Future Improvements)
Since this is an evolving academic project, the next steps I plan to implement are:
- [ ] Increase Unit Test coverage with JUnit & Mockito.
- [ ] Dockerize the application with a `Dockerfile`.
* [ ] Implement a CI/CD Pipeline with GitHub Actions.

---

## ⚙️ Local Setup

To run this project locally, you need to configure the following Environment Variables in your IDE (IntelliJ/Eclipse):

* `DB_URL`, `DB_USER`, `DB_PASSWORD` (For Oracle DB)
* `JWT_SECRET_KEY` (For token signing)
* `WALLET_PATH` (For secure Oracle connection)

```bash
# Clone the repository
git clone [https://github.com/RealFenix225/techstore-enterprise.git](https://github.com/RealFenix225/techstore-enterprise.git)

# Build & Run
mvn spring-boot:run
```
## 👨‍💻 Author

**César Gálvez**
*Java Backend Developer | DAM Student*

Currently seeking **FCT Internship** opportunities to continue learning and contributing with my skills in Java and Cloud technologies.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect_with_Me-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/cesar-galvez)

**Profile:** [www.linkedin.com/in/cesar-galvez](https://www.linkedin.com/in/cesar-galvez)