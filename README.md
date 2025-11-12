# 🛒 Small Store Management System (Socket-Based)

A lightweight, role-based store management system built with Java Sockets and MySQL. Designed for small businesses with two user roles: **Admin** and **Worker**. The system emphasizes security, modularity, and clean separation of concerns.

---

## ✨ Features

- **Secure Authentication**: Login via email and password  
- **Role-Based Authorization**:
  - **Admin**: Full access (user management, inventory, reports)
  - **Worker**: Limited access (sales, stock checks)
- **Session Management**: Each role enters a dedicated interactive session after login
- **Login History**: All successful logins are recorded with timestamps
- **Modular Design**: Separate session handlers for each role (easy to extend)
- **Input Validation**: Client-side validation for email format and password length (≥8 characters)
- **Database Integration**: MySQL backend with normalized schema

---

## 🗃️ Database Schema

The system uses 4 core tables:

| Table | Description |
|-------|-------------|
| `users` | Stores `id`, `username`, `email`, `password` (plaintext for demo — **use bcrypt in production**), `created_at` |
| `profile` | Stores user details: `first_name`, `last_name`, `birthday`, `address` (linked to `users.id`) |
| `user_roles` | Defines role per user: `admin` or `worker` (one-to-one with `users`) |
| `login_history` | Logs every successful login: `user_id` and `last_login` (timestamp) |

---

## 🔐 Security Notes

> ⚠️ **Important**: Passwords are currently compared in plaintext for simplicity.  
> In a production environment, **always**:
> - Hash passwords using **BCrypt** (or similar) before storing
> - Verify passwords in code using `BCrypt.checkpw(input, storedHash)`
> - Never store or transmit passwords in plain text

---

## 📦 How to Run

### Prerequisites
- Java 8+
- MySQL Server
- MySQL Connector/J (JDBC driver)

### Setup
1. Create a MySQL database and import the schema (see `schema.sql` if provided)
2. Update `Var_Env.java` with your DB credentials:
   ```java
   public class Var_Env {
       public static final String DB_NAME = "your_db";
       public static final String DB_USER = "your_user";
       public static final String DB_PASSWORD = "your_pass";
       public static final int DB_port = 3306;
   }

## 📦 How to Run

### 3. Compile and run the server:
```bash
javac -d . server/*.java
java server.Server


