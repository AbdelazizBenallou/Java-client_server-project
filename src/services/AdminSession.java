package services;

import java.io.*;
import java.sql.*;

public class AdminSession implements SessionHandler {
    private BufferedWriter writer;
    private BufferedReader reader;
    private Connection conn;
    private int userId;
    private String name;

    public AdminSession(BufferedWriter writer, BufferedReader reader, Connection conn, int userId, String name) {
        this.writer = writer;
        this.reader = reader;
        this.conn = conn;
        this.userId = userId;
        this.name = name;
    }

    @Override
    public void handleSession() throws IOException {
        sendMessage("== Hello Admin "+name+" Welcome to the admin panel ==");
        showMenu();
        while (true) {
            String choice = receiveMessage();
            if ("3".equals(choice)) {
                sendMessage("Goodbye, Admin!");
                break;
            } else if ("1".equals(choice)) {
                viewAllUsers();
                showMenu();
            } else if ("2".equals(choice)) {
                 handleInventoryManagement();
                 showMenu();
            } else {
                sendMessage("Invalid option. Choose 1, 2, or 3.");
                sendMessage("Your choice:");
                showMenu();
            }
        }
    }

    private void showMenu() throws IOException {
    String menu = 
        "Options\n" +
        "1. View all users\n" +
        "2. Manage inventory\n" +
        "Your choice:";
        sendMessage(menu);
    }
    
    private void handleInventoryManagement() throws IOException {
    showInventoryMenu();
    
    while (true) {
        String choice = receiveMessage();
        switch (choice) {
            case "1":
                sendMessage("Enter new user email:");
                String email = receiveMessage();
                
                if(isEmailExists(email)){
                    sendMessage("Error: Email already exists!");
                    showInventoryMenu();
                    break;
                }
                
                sendMessage("Enter full name (First Last):");
                String fullName = receiveMessage();
                String[] nameParts = fullName.trim().split("\\s+", 2);
                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                String lastName = nameParts.length > 1 ? nameParts[1] : "";
                
                sendMessage("Enter password (min 8 chars):");
                String password = receiveMessage();
                
                sendMessage("Enter role (Admin or Worker):");
                String role = receiveMessage();
                
                if (createNewUser(email, firstName, lastName, password, role)) {
                    sendMessage("User created successfully!");
                } else {
                    sendMessage("Error: Failed to create user.");
                }
                
                showInventoryMenu();
                break;
            case "2":
                sendMessage("Updating user info...");
                showInventoryMenu();
                break;
            case "3":
                sendMessage("Removing user...");
                showInventoryMenu();
                break;
            case "4":
                return;
            default:
                sendMessage("Invalid option. Choose 1-4.");
                showInventoryMenu();
        }
    }
}
    
    private void showInventoryMenu() throws IOException {
    sendMessage("Options Manage inventory");
    sendMessage("  1. add new user");
    sendMessage("  2. update information of user");
    sendMessage("  3. remove a user");
    sendMessage("  4. Principle Menu");
    sendMessage("Your choice:");
}

    private void viewAllUsers() {
    try {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username, email FROM users")) {
            sendMessage("Registered Users");
            while (rs.next()) {
                sendMessage("      - " + rs.getString("username") + " (" + rs.getString("email") + ")");
            }
        }
    } catch (SQLException e) {
        System.err.println("[ADMIN SESSION SQL ERROR] " + e.getMessage());
        e.printStackTrace(); 
        try {
            sendMessage("Error: Unable to load users.");
        } catch (IOException ignored) {}
    } catch (IOException e) {
        System.err.println("[ADMIN SESSION IO ERROR] " + e.getMessage());
    }
}
    

    private void sendMessage(String msg) throws IOException {
        writer.write(msg + "\n");
        writer.flush();
    }

    private String receiveMessage() throws IOException {
        return reader.readLine();
    }

    private boolean isEmailExists(String email) {
    String sql = "SELECT 1 FROM users WHERE email = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, email);
        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next();
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return true; 
    }
}
    
    private boolean createNewUser(String email, String firstName, String lastName, String password, String role) {
    try { 
        
        String insertUser = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement userStmt = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
            userStmt.setString(1, email.split("@")[0]); 
            userStmt.setString(2, email);
            userStmt.setString(3, password);
            userStmt.executeUpdate();

            
            try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    
                    if (!firstName.isEmpty() || !lastName.isEmpty()) {
                        String insertProfile = "INSERT INTO profile (user_id, first_name, last_name) VALUES (?, ?, ?)";
                        try (PreparedStatement profileStmt = conn.prepareStatement(insertProfile)) {
                            profileStmt.setInt(1, userId);
                            profileStmt.setString(2, firstName);
                            profileStmt.setString(3, lastName);
                            profileStmt.executeUpdate();
                        }
                    }

                    
                    String insertRole = "INSERT INTO user_roles (user_id, role) VALUES (?, ?)";
                    try (PreparedStatement roleStmt = conn.prepareStatement(insertRole)) {
                        roleStmt.setInt(1, userId);
                        roleStmt.setString(2, role);
                        roleStmt.executeUpdate();
                    }

                    return true;
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
    return false;
}

}