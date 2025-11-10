package server;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {

    public static void main(String[] args) {
        // Server configuration
        final int PORT = 8020;
        final String HOST = "127.0.0.1";

        // Database connection details (loaded from Var_Env)
        String db_name = Var_Env.DB_NAME;
        String db_user = Var_Env.DB_USER;
        String db_pass = Var_Env.DB_PASSWORD;
        int db_port = Var_Env.DB_port;

        String dbUrl = "jdbc:mysql://localhost:" + db_port + "/" + db_name;

        ServerSocket serverSocket = null;
        Connection dbConnection = null;

        try {
            // Step 1: Load the MySQL JDBC driver (required once)
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[INFO] MySQL JDBC Driver loaded successfully.");

            // Step 2: Establish a single, reusable connection to the database
            dbConnection = DriverManager.getConnection(dbUrl, db_user, db_pass);
            System.out.println("[INFO] Successfully connected to the database.");

            // Step 3: Create and bind the server socket
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(HOST, PORT));
            System.out.println("[INFO] Server is running on " + HOST + ":" + PORT);
            System.out.println("[INFO] Waiting for clients... (Press Ctrl+C to stop)\n");

            // Step 4: Infinite loop to accept clients one by one
            while (true) {
                Socket clientSocket = null;
                BufferedReader input = null;
                BufferedWriter output = null;

                try {
                    // Wait for a new client to connect
                    clientSocket = serverSocket.accept();
                    System.out.println("[CLIENT] New connection from: " + clientSocket.getInetAddress().getHostAddress());

                    // Create I/O streams for this client
                    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    // Send login prompts
                    sendMessage(output, "Hello , Please Login");
                    sendMessage(output, "Enter Your Email: ");
                    String email = receiveMessage(input);

                    sendMessage(output, "Enter Your Password:");
                    String password = receiveMessage(input);

                    // Step 5: Authenticate user against the database
                    String authQuery = "SELECT * FROM users WHERE email = ? AND password = ?";
                    PreparedStatement authStmt = dbConnection.prepareStatement(authQuery);
                    authStmt.setString(1, email);
                    authStmt.setString(2, password);
                    ResultSet userRecord = authStmt.executeQuery();

                    if (userRecord.next()) {
                        int userId = userRecord.getInt("id");

                        // Log successful login in history table
                        String logQuery = "INSERT INTO login_history (user_id) VALUES (?)";
                        PreparedStatement logStmt = dbConnection.prepareStatement(logQuery);
                        logStmt.setInt(1, userId);
                        logStmt.executeUpdate();

                        // Fetch full user profile (including role)
                        String profileQuery =
                            "SELECT " +
                            "    u.id, u.username, u.email, " +
                            "    p.first_name, p.last_name, " +
                            "    r.role " +
                            "FROM users u " +
                            "LEFT JOIN profile p ON u.id = p.user_id " +
                            "LEFT JOIN user_roles r ON u.id = r.user_id " +
                            "WHERE u.id = ?";

                        PreparedStatement profileStmt = dbConnection.prepareStatement(profileQuery);
                        profileStmt.setInt(1, userId);
                        ResultSet profile = profileStmt.executeQuery();

                        if (profile.next()) {
                            String role = profile.getString("role");
                            if ("admin".equals(role)) {
                                sendMessage(output, "Hello Admin How Are You !");
                            } else {
                                sendMessage(output, "Hello Worker, Do Your Job !");
                            }
                        }
                    } else {
                        // Invalid credentials
                        sendMessage(output, "Invalid email or password!");
                    }

                    System.out.println("[CLIENT] Session completed for: " + (email != null ? email : "unknown"));

                } catch (IOException e) {
                    // Handle client-side communication errors (e.g. client disconnects early)
                    System.err.println("[ERROR] Client I/O error: " + e.getMessage());
                } catch (SQLException e) {
                    // Handle database errors during this session
                    System.err.println("[ERROR] Database error during client session: " + e.getMessage());
                    try {
                        if (output != null) {
                            sendMessage(output, "Server error: database failure.");
                        }
                    } catch (IOException ignored) {
                        // Can't notify client — just log and move on
                    }
                } finally {
                    // Always close client-specific resources (never the server or DB!)
                    try {
                        if (output != null) output.close();
                        if (input != null) input.close();
                        if (clientSocket != null) clientSocket.close();
                    } catch (IOException ignored) {
                        // Ignore cleanup errors
                    }
                }

                System.out.println("[INFO] Ready for next client...\n");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("[FATAL] MySQL JDBC driver not found. Make sure mysql-connector-java is in classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[FATAL] Unable to connect to the database at startup.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("[FATAL] Server socket failed to bind or encountered a critical I/O error.");
            e.printStackTrace();
        } finally {
            // Clean up only on server shutdown (e.g., Ctrl+C)
            try {
                if (dbConnection != null && !dbConnection.isClosed()) {
                    dbConnection.close();
                    System.out.println("[INFO] Database connection closed.");
                }
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                    System.out.println("[INFO] Server socket closed.");
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper: Send a message to the client
    private static void sendMessage(BufferedWriter writer, String message) throws IOException {
        writer.write(message + "\n");
        writer.flush();
    }

    // Helper: Read one line from the client
    private static String receiveMessage(BufferedReader reader) throws IOException {
        return reader.readLine();
    }
}