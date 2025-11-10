package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class LoginPage  extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8020;

    public LoginPage() {
        setTitle("Login - Simple Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 200);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(18);
        add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(18);
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        add(loginBtn, gbc);

        gbc.gridy = 3;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        add(statusLabel, gbc);

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        setVisible(true);
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill all fields.");
            return;
        }

        // Show warning: UI will freeze during connection
        statusLabel.setText("Connecting... (UI may freeze)");
        statusLabel.setForeground(Color.BLUE);

        Socket socket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Read: "Hello , Please Login"
            reader.readLine();

            // Read: "Enter Your Email: "
            reader.readLine();
            writer.write(email + "\n");
            writer.flush();

            // Read: "Enter Your Password:"
            reader.readLine();
            writer.write(password + "\n");
            writer.flush();

            // Read final response
            String response = reader.readLine();

            if (response != null && 
                (response.contains("Hello Admin") || response.contains("Hello Worker"))) {
                JOptionPane.showMessageDialog(this, response, "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close login window
            } else {
                statusLabel.setText(response != null ? response : "Login failed.");
                statusLabel.setForeground(Color.RED);
            }

        } catch (IOException ex) {
            statusLabel.setText("Server unreachable.");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Cannot connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (writer != null) writer.close();
                if (reader != null) reader.close();
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
        }
    }

    public static void main(String[] args) {
        // Set system look and feel (Java 7/8 compatible)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        new LoginPage();
    }
}