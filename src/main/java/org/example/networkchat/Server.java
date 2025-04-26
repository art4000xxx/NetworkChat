package org.example.networkchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private final List<ClientHandler> clients = new ArrayList<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(Settings.getPort())) {
            logger.info("Server started on port {}", Settings.getPort());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            logger.error("Server error: {}", e.getMessage());
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        logger.info("Broadcasting message: {}", message);
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        logger.info("Client disconnected, remaining clients: {}", clients.size());
    }
}