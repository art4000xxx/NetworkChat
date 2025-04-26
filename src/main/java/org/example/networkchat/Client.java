package org.example.networkchat;
import java.io.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            logger.info("Connected to server at {}:{}", host, port);

            System.out.println("Enter your username:");
            try {
                Console console = System.console();
                if (console != null) {
                    username = console.readLine().trim();
                    logger.info("Username entered via console: {}", username);
                } else {
                    logger.info("Console not available, using Scanner");
                    Scanner scanner = new Scanner(System.in);
                    if (scanner.hasNextLine()) {
                        username = scanner.nextLine().trim();
                        logger.info("Username entered via scanner: {}", username);
                    } else {
                        throw new IOException("No input available");
                    }
                }
            } catch (Exception e) {
                username = "Anonymous";
                logger.warn("Input failed, using default username: {}", username, e);
            }
            if (username.isEmpty()) {
                username = "Anonymous";
            }
            out.println(username);
            logger.info("Username sent: {}", username);

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        logger.info("Received: {}", message);
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    logger.error("Error reading from server: {}", e.getMessage());
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    if (scanner.hasNextLine()) {
                        String message = scanner.nextLine().trim();
                        out.println(message);
                        logger.info("Sent message: {}", message);
                        if (message.equalsIgnoreCase("/exit")) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error("Input error: {}", e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Client error: {}", e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                    logger.info("Socket closed");
                } catch (IOException e) {
                    logger.error("Error closing socket: {}", e.getMessage());
                }
            }
        }
    }
}