package Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    static Properties prop = new Properties();

    // ✅ Static block to initialize properties
    static {
        try {
            // Construct absolute path
            String configPath = System.getProperty("user.dir") + "/src/test/resources/config/config.properties";
            File file = new File(configPath);

            // ✅ Check if file exists before reading
            if (!file.exists()) {
                throw new RuntimeException("❌ Config file not found at: " + configPath);
            }

            FileInputStream fis = new FileInputStream(file);
            prop.load(fis);
            fis.close();
            System.out.println("✅ Configuration file loaded successfully from: " + configPath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Could not load config.properties file.");
        }
    }

    // ✅ Fetch individual properties (Fix for BaseTest)
    public static String getBaseUrl() {
        return getProperty("baseurl");
    }

    public static String getUsername() {
        return getProperty("username");
    }

    public static String getPassword() {
        return getProperty("password");
    }

    public static String getChainingJsonPath() {
        return getProperty("chainingJsonPath");
    }

    public static String getNonChainingJsonPath() {
        return getProperty("nonChainingJsonPath");
    }

    // ✅ Generic method to fetch properties
    public static String getProperty(String key) {
        String value = prop.getProperty(key);
        if (value == null || value.isEmpty()) {
            System.out.println("⚠ Warning: Missing value for key: " + key);
        }
        return value;
    }

    // ✅ Print Configuration Details for Debugging
    public static void printConfigDetails() {
        System.out.println("🔹 Base URL: " + getBaseUrl());
        System.out.println("🔹 Browser: " + getProperty("browser"));
        System.out.println("🔹 Username: " + getUsername());
        System.out.println("🔹 Chaining JSON Path: " + getChainingJsonPath());
        System.out.println("🔹 Non-Chaining JSON Path: " + getNonChainingJsonPath());
    }

    // ✅ Validate If Config File Loaded Properly
    public static boolean isConfigLoaded() {
        return !prop.isEmpty();
    }
}
