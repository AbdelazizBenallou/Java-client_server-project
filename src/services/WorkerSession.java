/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import server.Server;
import java.net.*;
import java.io.*;
import java.sql.*;

/**
 *
 * @author root
 */
// WorkerSession.java
public class WorkerSession implements SessionHandler {
    private BufferedWriter writer;
    private BufferedReader reader;
    private Connection conn;
    private int userId;
    private String name;

    public WorkerSession(BufferedWriter writer, BufferedReader reader, Connection conn, int userId, String name) {
        this.writer = writer;
        this.reader = reader;
        this.conn = conn;
        this.userId = userId;
        this.name = name;
    }

    @Override
    public void handleSession() throws IOException {
        sendMessage("👷 Hello Worker " + name + "! Ready to work.");
        sendMessage("Options:\n1. Register sale\n2. Check stock\n3. Logout");

        while (true) {
            String choice = receiveMessage();
            if ("3".equals(choice)) {
                sendMessage("Goodbye, Worker!");
                break;
            } else if ("1".equals(choice)) {
                registerSale();
            } else if ("2".equals(choice)) {
                checkStock();
            } else {
                sendMessage("Invalid option. Choose 1, 2, or 3.");
            }
        }
    }

    private void registerSale() throws IOException {
        sendMessage("Starting new sale...");
    }

    private void checkStock() throws IOException {
        sendMessage("Checking current stock...");
    }

    private void sendMessage(String msg) throws IOException {
        writer.write(msg + "\n");
        writer.flush();
    }

    private String receiveMessage() throws IOException {
        return reader.readLine();
    }
}