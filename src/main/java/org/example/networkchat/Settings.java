package org.example.networkchat;

import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static final String SETTINGS_FILE = "settings.txt";

    public static int getPort() {
        Properties props = new Properties();
        try {
            props.load(Settings.class.getClassLoader().getResourceAsStream(SETTINGS_FILE));
            return Integer.parseInt(props.getProperty("port", "8081"));
        } catch (IOException | NumberFormatException | NullPointerException e) {
            System.err.println("Error reading settings, using default port 8081: " + e.getMessage());
            return 8081;
        }
    }
}