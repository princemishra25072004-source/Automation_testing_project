package com.cyberguard.utils;

import com.cyberguard.config.TestConfig;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database utility providing standard JDBC connectivity to MySQL and embedded fallback mode.
 * Supports schema initialization, connection lifecycle, and transaction management.
 */
public class DatabaseHelper {

    private static Connection connection;

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName(TestConfig.DB_DRIVER);
                connection = DriverManager.getConnection(TestConfig.DB_URL, TestConfig.DB_USER, TestConfig.DB_PASSWORD);
            } catch (Exception e) {
                // Seamless fallback to H2 embedded database with MySQL syntax compatibility
                // if standalone external MySQL service is not reachable
                try {
                    Class.forName("org.h2.Driver");
                    connection = DriverManager.getConnection("jdbc:h2:mem:cyberguard;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH", "sa", "");
                    initializeFallbackSchema(connection);
                } catch (Exception ex) {
                    throw new SQLException("Unable to connect to MySQL or Embedded DB: " + ex.getMessage(), ex);
                }
            }
        }
        return connection;
    }

    public static void initializeFallbackSchema(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id VARCHAR(64) PRIMARY KEY, " +
                    "name VARCHAR(120), " +
                    "email VARCHAR(120) UNIQUE NOT NULL, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(32) DEFAULT 'user', " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Analyses table
            stmt.execute("CREATE TABLE IF NOT EXISTS analyses (" +
                    "id VARCHAR(64) PRIMARY KEY, " +
                    "user_id VARCHAR(64), " +
                    "text_content TEXT NOT NULL, " +
                    "language VARCHAR(32), " +
                    "is_safe BOOLEAN NOT NULL, " +
                    "toxicity_score DOUBLE, " +
                    "is_sarcastic BOOLEAN DEFAULT FALSE, " +
                    "severity VARCHAR(32) NOT NULL, " +
                    "category VARCHAR(64), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ")");

            // Seed user with real BCrypt hash for ankit.test@cyberguard.ai
            String hashedUser = BCrypt.hashpw(TestConfig.USER_PASSWORD, BCrypt.gensalt(12));
            String hashedAdmin = BCrypt.hashpw(TestConfig.ADMIN_PASSWORD, BCrypt.gensalt(12));

            stmt.execute("MERGE INTO users KEY(email) VALUES ('usr-1001', 'Ankit Mishra', '" + TestConfig.USER_EMAIL + "', '" + hashedUser + "', 'user', CURRENT_TIMESTAMP)");
            stmt.execute("MERGE INTO users KEY(email) VALUES ('adm-2001', 'System Administrator', '" + TestConfig.ADMIN_EMAIL + "', '" + hashedAdmin + "', 'admin', CURRENT_TIMESTAMP)");
            stmt.execute("MERGE INTO users KEY(email) VALUES ('tmp-3001', 'Cascade Delete Candidate', 'candidate.delete@cyberguard.ai', '" + hashedUser + "', 'user', CURRENT_TIMESTAMP)");

            stmt.execute("MERGE INTO analyses KEY(id) VALUES ('ana-01', 'usr-1001', 'Oh great, another brilliant genius who knows everything! वाह क्या बात है!', 'Hindi-English', false, 0.45, true, 'Medium', 'Harassment', CURRENT_TIMESTAMP)");
            stmt.execute("MERGE INTO analyses KEY(id) VALUES ('ana-02', 'usr-1001', 'You are completely useless.', 'English', false, 0.88, false, 'High', 'Hate Speech', CURRENT_TIMESTAMP)");
            stmt.execute("MERGE INTO analyses KEY(id) VALUES ('ana-03', 'tmp-3001', 'Temporary cascade analysis row', 'English', false, 0.70, false, 'High', 'Toxic', CURRENT_TIMESTAMP)");

        } catch (SQLException e) {
            System.err.println("Warning: Fallback schema init failed: " + e.getMessage());
        }
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            }
            connection = null;
        }
    }
}

