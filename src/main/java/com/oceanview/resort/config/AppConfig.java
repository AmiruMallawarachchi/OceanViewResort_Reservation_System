package com.oceanview.resort.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
    private static final String PROPERTIES_FILE = "app.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Missing " + PROPERTIES_FILE + " in classpath.");
            }
            PROPERTIES.load(input);
        } catch (IOException ex) {
            throw new ExceptionInInitializerError("Failed to load app configuration: " + ex.getMessage());
        }
    }

    private AppConfig() {
    }

    public static String getProperty(String key, String defaultValue) {
        String value = PROPERTIES.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = getProperty(key, String.valueOf(defaultValue));
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    public static int getInt(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}
