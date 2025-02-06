package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final Properties prop = new Properties();

    // Static block to initialize properties
    static {
        try {
            // Construct absolute path
            String configPath = System.getProperty("user.dir") + "/src/test/resources/config/config.properties";
            File file = new File(configPath);

            // Check if file exists before reading
            if (!file.exists()) {
                throw new RuntimeException("Config file not found at: " + configPath);
            }

            // Auto-closing file stream
            try (FileInputStream fis = new FileInputStream(file)) {
                prop.load(fis);
            }

            LoggerLoad.info("Configuration file loaded successfully from: " + configPath);

        } catch (IOException e) {
            LoggerLoad.error("Could not load config.properties file: " + e.getMessage());
            throw new RuntimeException("Could not load config.properties file.", e);
        }
    }

    // Fetch individual properties (Pre-fetched values to avoid redundant calls)
    private static final String BASE_URL = getProperty("baseurl");
    private static final String USERNAME = getProperty("username");
    private static final String PASSWORD = getProperty("password");
    private static final String CHAINING_JSON_PATH = getProperty("chainingJsonPath");
    private static final String NON_CHAINING_JSON_PATH = getProperty("nonChainingJsonPath");

    public static String getBaseUrl() { return BASE_URL; }
    public static String getUsername() { return USERNAME; }
    public static String getPassword() { return PASSWORD; }
    public static String getChainingJsonPath() { return CHAINING_JSON_PATH; }
    public static String getNonChainingJsonPath() { return NON_CHAINING_JSON_PATH; }

    // Generic method to fetch properties
    private static String getProperty(String key) {
        String value = prop.getProperty(key);
        if (value == null || value.isEmpty()) {
            LoggerLoad.warn("Warning: Missing value for key: " + key);
        }
        return value;
    }

    // Print Configuration Details for Debugging
    public static void printConfigDetails() {
        LoggerLoad.info("Base URL: " + BASE_URL);
        LoggerLoad.info("Username: " + USERNAME);
        LoggerLoad.info("Chaining JSON Path: " + CHAINING_JSON_PATH);
        LoggerLoad.info("Non-Chaining JSON Path: " + NON_CHAINING_JSON_PATH);
    }

    // Validate If Config File Loaded Properly
    public static boolean isConfigLoaded() {
        return !prop.isEmpty();
    }
}
