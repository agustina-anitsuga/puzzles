package com.example.puzzles.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PuzzleProperties {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = PuzzleProperties.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static int getIntProperty(String key) {
        return properties.getProperty(key) != null ? Integer.parseInt(properties.getProperty(key)) : 0;
    }

}
