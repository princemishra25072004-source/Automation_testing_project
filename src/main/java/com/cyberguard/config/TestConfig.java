package com.cyberguard.config;

/**
 * Global configuration constants for CyberGuard AI Automation Testing Framework.
 * Matches project report Chapter 1, 3, 5, and 6 specifications.
 */
public class TestConfig {

    // Application Endpoints
    public static final String BASE_UI_URL = System.getProperty("ui.url", "http://localhost:5173");
    public static final String BASE_API_URL = System.getProperty("api.url", "http://localhost:8000/api");
    
    // Database Configuration
    public static final String DB_URL = System.getProperty("db.url", "jdbc:mysql://127.0.0.1:3306/cyberguard?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
    public static final String DB_USER = System.getProperty("db.user", "root");
    public static final String DB_PASSWORD = System.getProperty("db.password", "root123");
    public static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Standard User Test Account
    public static final String USER_EMAIL = "ankit.test@cyberguard.ai";
    public static final String USER_PASSWORD = "Password@123";
    public static final String USER_NAME = "Ankit Mishra";

    // Administrator Test Account
    public static final String ADMIN_EMAIL = "admin@admin.cyberguard";
    public static final String ADMIN_PASSWORD = "AdminSecurePass!123";
    public static final String ADMIN_NAME = "System Administrator";

    // Timing and Synchronization
    public static final int PAGE_LOAD_TIMEOUT = 15;
    public static final int EXPLICIT_WAIT_TIMEOUT = 10;
    public static final int DEFAULT_DWELL_SECONDS = 4; // Required fixed 4-second hold on important pages

    // File / Evidence Paths
    public static final String SCREENSHOTS_DIR = "screenshots/";
    public static final String TEST_DATA_DIR = "src/main/resources/testdata/";
}

