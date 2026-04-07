# 🎓 Student Management System

A full-stack web application designed to manage students, courses, schedules, and enrollments with role-based access control.

This project demonstrates modern backend and frontend development practices using Spring Boot and React, including secure authentication, clean architecture, and real-world business logic.

---

# 🚀 Features

## 🔐 Authentication & Authorization

* JWT-based authentication
* Role-based access control (ADMIN / STUDENT)
* Secure login & registration system

## 👨‍🎓 Student Management

* Create, update, delete students
* Search students by name
* Pagination & sorting support

## 📚 Course Management

* Create and manage courses
* Assign courses to students via enrollment

## 📅 Schedule Management

* Create schedules with:

  * Day of week
  * Start & end time
  * Room & semester
* Students can view only their own schedules (`/api/schedules/my`)

## 📝 Enrollment System

* Students can enroll in courses
* Duplicate enrollment prevention
* Students can view only their own enrollments

## 📊 Testing & Coverage

* Unit tests with Mockito
* Integration tests with Spring Boot Test
* Code coverage with JaCoCo (~70%)

---

# 🖼️ UI Preview

## 🔐 Login Page

<img width="1909" height="908" alt="Screenshot 2026-04-07 141454" src="https://github.com/user-attachments/assets/5b899164-b571-48b0-8b5a-6503fde0ac6c" />

---

## 📝 Register Page

<img width="1904" height="907" alt="Screenshot 2026-04-07 141510" src="https://github.com/user-attachments/assets/d6d88602-c8dd-47d7-860b-92293a9e4ede" />

---

## 🎨 Dashboard Modes

<p align="center">
  <img width="1904" height="905" alt="Screenshot 2026-04-07 141540" src="https://github.com/user-attachments/assets/04527a99-3c61-4fdc-bc6b-ef978e687dd5" />
  <img width="1892" height="905" alt="Screenshot 2026-04-07 141557" src="https://github.com/user-attachments/assets/d2602d2d-0c0d-4f11-be68-cd7497087cfb" />
</p>  

---

## 📅 Student Schedule View

<img width="1894" height="904" alt="Screenshot 2026-04-07 141640" src="https://github.com/user-attachments/assets/6da7bd70-42b5-4096-87fe-c8dd32f1cad8" />

---

# 🛠️ Tech Stack

## Backend

* Java 17
* Spring Boot 3
* Spring Security + JWT
* Spring Data JPA (Hibernate)
* PostgreSQL
* Maven
* Swagger / OpenAPI
* JaCoCo (test coverage)

## Frontend

* React (Vite)
* Axios
* React Router
* CSS / Tailwind

---

# ⚙️ Setup Instructions

## 1️⃣ Clone the repository

```bash
git clone https://github.com/your-username/student-management-system.git
cd student-management-system
```

---

## 2️⃣ Backend Setup

```bash
cd backend/sms-backend
```

### 🔐 Environment Variables

Set the following variables:

| Variable    | Description        |
| ----------- | ------------------ |
| DB_USERNAME | Database username  |
| DB_PASSWORD | Database password  |
| JWT_SECRET  | Secret key for JWT |

### Example (Windows PowerShell)

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="1234"
$env:JWT_SECRET="mysecretkey"
```

---

### ▶️ Run Backend

```bash
./mvnw spring-boot:run
```

Backend runs at:

```
http://localhost:8080
```

---

## 3️⃣ Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

```
http://localhost:5173
```

---

# 📌 API Documentation (Swagger)

```
http://localhost:8080/swagger-ui/index.html
```

* Test all endpoints
* Authenticate using JWT
* View request/response models

---

# 🔑 Sample Credentials

### 👨‍💼 Admin

```
username: admin
password: admin123
```

### 👨‍🎓 Student

```
username: student1
password: student123
```

---

# 🧪 Running Tests

```bash
./mvnw.cmd clean test
```

---

# 📊 JaCoCo Coverage Report

```
target/site/jacoco/index.html
```

---

# 🏗️ Project Structure

```
backend/
 └── sms-backend/
     ├── controller/
     ├── service/
     ├── service/impl/
     ├── repository/
     ├── entity/
     ├── dto/
     ├── mapper/
     ├── security/
     └── exception/

frontend/
 ├── components/
 ├── pages/
 ├── services/
 └── router/
```

---

# 🔒 Security

* JWT-based stateless authentication
* Passwords encrypted using BCrypt
* Sensitive data stored in environment variables
* Role-based endpoint protection

---

# ❗ Error Handling

Global exception handling implemented using:

* `@ControllerAdvice`
* Custom exceptions:

  * `ResourceNotFoundException`
  * `DuplicateResourceException`

Each error returns:

* Proper HTTP status codes
* Meaningful messages

---

# 🎯 Key Highlights

✔ Clean layered architecture
✔ DTO + Mapper pattern
✔ Role-based data filtering
✔ Environment-based configuration
✔ Test coverage with JaCoCo
✔ Fully documented API

---

# 📌 Future Improvements

* Docker support
* CI/CD pipeline
* Advanced filtering & search
* UI improvements

---

# 👨‍💻 Author

Ahsen Nimet Elmas
Computer Engineering Student

---

# ⭐ Final Note

This project demonstrates a complete full-stack system with authentication, authorization, and real-world business logic, following best practices in modern software development.
