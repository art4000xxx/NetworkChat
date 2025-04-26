package org.example.networkchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket socket;
    private final Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your username:");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous";
            }
            if (username.startsWith("GET ") || username.startsWith("POST ") || username.startsWith("HTTP/")) {
                logger.warn("HTTP request detected, rejecting client: {}", username);
                out.println("Error: This is not an HTTP server");
                return;
            }
            logger.info("User {} connected", username);
            server.broadcastMessage(username + " has joined the chat", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.trim().equalsIgnoreCase("/exit")) {
                    break;
                }
                String formattedMessage = formatMessage(message);
                server.broadcastMessage(formattedMessage, this);
            }
        } catch (IOException e) {
            logger.error("Error handling client {}: {}", username != null ? username : "Unknown", e.getMessage());
        } finally {
            server.removeClient(this);
            if (username != null && !username.startsWith("GET ") && !username.startsWith("POST ") && !username.startsWith("HTTP/")) {
                server.broadcastMessage(username + " has left the chat", this);
            }
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Error closing socket: {}", e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private String formatMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "[" + timestamp + "] " + username + ": " + message;
    }
}