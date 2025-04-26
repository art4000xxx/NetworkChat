package org.example.networkchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            try {
                Server tcpServer = new Server();
                WebSocketChatServer wsServer = new WebSocketChatServer(8082, tcpServer);
                wsServer.start();
                SimpleHttpServer.start(8080);
                tcpServer.start();
            } catch (IOException e) {
                logger.error("Failed to start servers: {}", e.getMessage());
            }
        } else if (args.length > 0 && args[0].equalsIgnoreCase("client")) {
            new Client("localhost", Settings.getPort()).start();
        } else {
            System.out.println("Usage: specify 'server' or 'client' as argument");
        }
    }
}