package org.example.networkchat;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class WebSocketChatServer extends WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketChatServer.class);
    private final Set<WebSocket> clients = new HashSet<>();
    private final Server tcpServer;

    public WebSocketChatServer(int port, Server tcpServer) {
        super(new InetSocketAddress(port));
        this.tcpServer = tcpServer;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        String username = handshake.getResourceDescriptor().replace("/", "").trim();
        if (username.isEmpty()) {
            username = "WebUser_" + conn.getRemoteSocketAddress().getPort();
        }
        conn.setAttachment(username);
        logger.info("WebSocket user {} connected", username);
        broadcast(username + " has joined the chat");
        tcpServer.broadcastMessage(username + " has joined via WebSocket", null);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String username = conn.getAttachment();
        if (username == null) {
            username = "WebUser_" + conn.getRemoteSocketAddress().getPort();
        }
        clients.remove(conn);
        logger.info("WebSocket user {} disconnected", username);
        broadcast(username + " has left the chat");
        tcpServer.broadcastMessage(username + " has left via WebSocket", null);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String username = conn.getAttachment();
        if (username == null) {
            username = "WebUser_" + conn.getRemoteSocketAddress().getPort();
        }
        String formattedMessage = "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + username + ": " + message;
        logger.info("WebSocket message: {}", formattedMessage);
        broadcast(formattedMessage);
        tcpServer.broadcastMessage(formattedMessage, null);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        String username = conn != null && conn.getAttachment() != null ? conn.getAttachment() : "Unknown";
        logger.error("WebSocket error for user {}: {}", username, ex.getMessage());
    }

    @Override
    public void onStart() {
        logger.info("WebSocket server started on port {}", getPort());
    }

    @Override
    public void broadcast(String message) {
        for (WebSocket client : clients) {
            client.send(message);
        }
    }
}