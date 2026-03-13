# 🛒 Small Store Management System (Socket-Based)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-5.7%2B-blue.svg)](https://www.mysql.com/)
[![NetBeans](https://img.shields.io/badge/NetBeans-8.0+-green.svg)](https://netbeans.org/)

## 📖 Overview

**Small Store Management System** is a lightweight, role-based store management application built with **Java Sockets** and **MySQL**. Designed for small businesses, this system provides secure authentication and differentiated access levels for two user roles: **Admin** and **Worker**.

The architecture emphasizes **security**, **modularity**, and **clean separation of concerns**, making it an excellent reference project for learning client-server communication, database integration, and session management in Java.

> ⚠️ **Educational Purpose**: This project is designed for learning. For production use, implement additional security measures (password hashing, SSL/TLS, etc.).

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🔐 **Secure Authentication** | Login via email and password with database validation |
| 👥 **Role-Based Authorization** | Admin (full access) vs Worker (limited access) |
| 📝 **Session Management** | Dedicated interactive sessions per user role |
| 📊 **Login History** | All successful logins recorded with timestamps |
| 🧱 **Modular Design** | Separate session handlers for easy extension |
| ✅ **Input Validation** | Client-side validation for email & password (≥8 chars) |
| 🗄️ **Database Integration** | MySQL backend with normalized schema (4 tables) |

---

## 🏗 System Architecture

```
┌─────────────────┐      Socket (Port 8020)      ┌─────────────────┐
│   Client (GUI)  │ ◄──────────────────────────► │     Server      │
│  LoginPage.java │                              │   Server.java   │
│   (Swing UI)    │                              │  (Socket Listener)│
└─────────────────┘                              └────────┬────────┘
                                                         │
                                                         ▼
                                                  ┌─────────────┐
                                                  │   MySQL DB  │
                                                  │  (Localhost)│
                                                  └─────────────┘
```

### Role Flow
```
Login → Authenticate → Fetch Role → Start Session
                              │
                    ┌─────────┴─────────┐
                    ▼                   ▼
              AdminSession        WorkerSession
           (Full Access)        (Limited Access)
```

---

## 🗂 Repository Structure

```
Java-client_server-project.git/
├── nbproject/                    # NetBeans project configuration
│   ├── private/
│   ├── build-impl.xml
│   ├── project.properties
│   └── project.xml
├── src/
│   ├── server/
│   │   └── Server.java           # Main server socket handler
│   ├── services/
│   │   ├── AdminSession.java     # Admin role session logic
│   │   ├── WorkerSession.java    # Worker role session logic
│   │   └── SessionHandler.java   # Session interface
│   └── view/
│       └── LoginPage.java        # Client GUI login form
├── build.xml                     # Ant build configuration
├── manifest.mf                   # JAR manifest
└── README.md
```

---

## 🗃️ Database Schema

The system uses **4 normalized tables**:

| Table | Description | Key Columns |
|-------|-------------|-------------|
| `users` | User credentials | `id`, `username`, `email`, `password`, `created_at` |
| `profile` | User details | `user_id`, `first_name`, `last_name`, `birthday`, `address` |
| `user_roles` | Role assignment | `user_id`, `role` (admin/worker) |
| `login_history` | Login audit | `user_id`, `last_login` (timestamp) |

### Sample Schema SQL
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE profile (
    user_id INT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE user_roles (
    user_id INT PRIMARY KEY,
    role ENUM('admin', 'worker'),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE login_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 🔐 Security Notes

> ⚠️ **CRITICAL**: This is a learning project. **DO NOT use in production without modifications.**

| Current State | Production Requirement |
|---------------|----------------------|
| ❌ Plaintext passwords | ✅ Hash with **BCrypt** |
| ❌ No SSL/TLS encryption | ✅ Implement **SSL sockets** |
| ❌ Basic input validation | ✅ Sanitize all inputs |
| ❌ Single DB connection | ✅ Use **connection pooling** |

### Recommended Security Improvements
```java
// Password hashing example (BCrypt)
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
boolean isValid = BCrypt.checkpw(inputPassword, hashedPassword);
```

---

## 🛠 Prerequisites

| Requirement | Version | Purpose |
|-------------|---------|---------|
| **Java JDK** | 8 or higher | Runtime & compilation |
| **MySQL Server** | 5.7+ | Database backend |
| **MySQL Connector/J** | 9.5.0 | JDBC driver |
| **NetBeans IDE** | 8.0+ (optional) | Development & build |
| **Ant** | 1.8.0+ | Build automation |

---

## 🚀 Installation & Setup

### Step 1: Database Configuration

1. Create the database:
```sql
CREATE DATABASE store_management;
USE store_management;
```

2. Import the schema (create tables as shown above)

3. Insert a test admin user:
```sql
INSERT INTO users (username, email, password) VALUES ('admin', 'admin@store.com', 'admin123');
INSERT INTO profile (user_id, first_name, last_name) VALUES (1, 'System', 'Admin');
INSERT INTO user_roles (user_id, role) VALUES (1, 'admin');
```

### Step 2: Configure Environment Variables

Create or update `src/server/Var_Env.java`:

```java
public class Var_Env {
    public static final String DB_NAME = "store_management";
    public static final String DB_USER = "your_mysql_user";
    public static final String DB_PASSWORD = "your_mysql_password";
    public static final int DB_port = 3306;
}
```

### Step 3: Add MySQL Connector

Download **MySQL Connector/J 9.5.0** and add to project libraries:
```
file.reference.mysql-connector-j-9.5.0.jar=/path/to/mysql-connector-j-9.5.0.jar
```

### Step 4: Build & Run

#### Using NetBeans
1. Open project in NetBeans
2. Right-click → **Clean and Build**
3. Run `Server.java` first, then `LoginPage.java`

#### Using Command Line
```bash
# Compile Server
javac -cp ".:mysql-connector-j-9.5.0.jar" -d build src/server/*.java src/services/*.java

# Run Server
java -cp "build:mysql-connector-j-9.5.0.jar" server.Server

# Compile & Run Client (in new terminal)
javac -cp ".:mysql-connector-j-9.5.0.jar" -d build src/view/*.java
java -cp "build:mysql-connector-j-9.5.0.jar" view.LoginPage
```

#### Using Ant
```bash
ant clean
ant jar
ant run
```

---

## 📋 Usage Guide

### Server Console Output
```
[INFO] MySQL JDBC Driver loaded successfully.
[INFO] Successfully connected to the database.
[INFO] Server is running on 127.0.0.1:8020
[INFO] Waiting for clients... (Press Ctrl+C to stop)
[CLIENT] New connection from: 127.0.0.1
[CLIENT] Session completed for: admin@store.com
[INFO] Ready for next client...
```

### Admin Capabilities
| Option | Function |
|--------|----------|
| 1 | View all registered users |
| 2 | Manage inventory (add/update/remove users) |
| 3 | Logout |

### Worker Capabilities
| Option | Function |
|--------|----------|
| 1 | Register sale |
| 2 | Check stock |
| 3 | Logout |

---

## 🔧 Configuration Files

### nbproject/project.properties
```properties
javac.source=1.7
javac.target=1.7
main.class=server.Server
file.reference.mysql-connector-j-9.5.0.jar=/path/to/mysql-connector-j-9.5.0.jar
```

### manifest.mf
```
Manifest-Version: 1.0
Main-Class: server.Server
Class-Path: lib/mysql-connector-j-9.5.0.jar
```

---

## 🧪 Testing

### Connection Test
```bash
# Test server port
telnet 127.0.0.1 8020

# Test database connection
mysql -u your_user -p -h 127.0.0.1 store_management
```

### Login Test Cases
| Email | Password | Expected Result |
|-------|----------|-----------------|
| admin@store.com | admin123 | ✅ Admin session |
| worker@store.com | worker123 | ✅ Worker session |
| invalid@email.com | any | ❌ Invalid credentials |
| (empty) | (empty) | ❌ Validation error |

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Add MySQL Connector to classpath |
| `Communications link failure` | Check MySQL server is running |
| `Access denied for user` | Verify DB credentials in `Var_Env.java` |
| `Port 8020 already in use` | Change PORT in `Server.java` or kill existing process |
| `UI freezes on login` | Normal behavior (synchronous connection) |

---

## 🤝 Contributing

Contributions are welcome! Areas for improvement:

- [ ] Implement **BCrypt password hashing**
- [ ] Add **SSL/TLS encryption** for sockets
- [ ] Implement **connection pooling**
- [ ] Add **inventory management** features
- [ ] Create **sales reporting** module
- [ ] Add **unit tests** (JUnit)
- [ ] Improve **GUI design**

### How to Contribute
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License**.

**Copyright (c) 2025**

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---

## 📬 Contact & Support

For questions, issues, or suggestions:
- 📧 Open an issue on the repository
- 📖 Review the code documentation
- 🔍 Check the troubleshooting section

---

## 🎓 Learning Outcomes

By studying this project, you will understand:

| Concept | Implementation |
|---------|---------------|
| **Socket Programming** | `ServerSocket`, `Socket`, I/O streams |
| **Database Connectivity** | JDBC, `PreparedStatement`, `ResultSet` |
| **OOP Design** | Interfaces, inheritance, encapsulation |
| **Session Management** | Role-based session handlers |
| **GUI Development** | Swing, `JFrame`, event handling |
| **Build Systems** | Ant, NetBeans project structure |

---

**Happy Coding!** 💻🚀
