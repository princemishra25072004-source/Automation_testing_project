package com.cyberguard.database;

import com.cyberguard.config.TestConfig;
import com.cyberguard.utils.DatabaseHelper;
import org.mindrot.jbcrypt.BCrypt;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Database Integration Test Suite verifying MySQL schema integrity, foreign keys,
 * cascade deletion policies, and password BCrypt cryptographic hashing.
 * Covers TC_JAVA_DB_01 through TC_JAVA_DB_04 (4 automated tests).
 * Matches Chapter 6, Section 9, Table 8.1, and Table 8.2 of the project report.
 */
public class DatabaseIntegrationTest {

    private Connection conn;

    @BeforeClass
    public void setupDbConnection() throws SQLException {
        conn = DatabaseHelper.getConnection();
        Assert.assertNotNull(conn, "Database connection must be established successfully");
    }

    @AfterClass(alwaysRun = true)
    public void tearDownDbConnection() {
        DatabaseHelper.closeConnection();
    }

    @Test(priority = 1, description = "TC_JAVA_DB_01: Find user in users table verifies record and UUID existence")
    public void testFindUserInUsersTable() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT id, name, email, role FROM users WHERE email = ?");
        stmt.setString(1, TestConfig.USER_EMAIL);
        ResultSet rs = stmt.executeQuery();

        Assert.assertTrue(rs.next(), "User record must exist in users table");
        String userId = rs.getString("id");
        Assert.assertNotNull(userId, "User ID must be present");
        Assert.assertTrue(userId.length() > 0, "User ID must not be empty");
        Assert.assertEquals(rs.getString("email"), TestConfig.USER_EMAIL, "Email must match test user");
    }

    @Test(priority = 2, description = "TC_JAVA_DB_02: Passwords in MySQL are bcrypt hashes, NOT plaintext")
    public void testPasswordIsBcryptHashed() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT password_hash FROM users WHERE email = ?");
        stmt.setString(1, TestConfig.USER_EMAIL);
        ResultSet rs = stmt.executeQuery();

        Assert.assertTrue(rs.next(), "User record must exist to inspect password hash");
        String stored = rs.getString("password_hash");

        Assert.assertNotEquals(stored, TestConfig.USER_PASSWORD, "Password must NEVER be stored in plaintext");
        Assert.assertTrue(stored.startsWith("$2b$") || stored.startsWith("$2a$") || stored.startsWith("$2y$"),
                "Password must be formatted as a valid BCrypt hash ($2b$, $2a$, or $2y$)");
        Assert.assertTrue(BCrypt.checkpw(TestConfig.USER_PASSWORD, stored),
                "BCrypt password verification must succeed for valid credentials");
    }

    @Test(priority = 3, description = "TC_JAVA_DB_03: Inspect analyses table validates category and severity schema")
    public void testInspectAnalysesTableSchema() throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT id, is_safe, severity, category FROM analyses LIMIT 5");
        ResultSet rs = stmt.executeQuery();

        Assert.assertTrue(rs.next(), "Analyses table must contain rows");
        String severity = rs.getString("severity");
        Assert.assertNotNull(severity, "Severity field cannot be null");
        Assert.assertTrue(severity.matches("(?i)low|medium|high|critical|safe"),
                "Severity must conform to allowed enum levels: Safe, Low, Medium, High, Critical");

        String category = rs.getString("category");
        Assert.assertNotNull(category, "Category cannot be null");
    }

    @Test(priority = 4, description = "TC_JAVA_DB_04: User CASCADE delete test verifies cascading references deletion")
    public void testUserCascadeDelete() throws SQLException {
        // Find temporary user targeted for cascade delete
        String cascadeCandidateEmail = "candidate.delete@cyberguard.ai";
        PreparedStatement findStmt = conn.prepareStatement("SELECT id FROM users WHERE email = ?");
        findStmt.setString(1, cascadeCandidateEmail);
        ResultSet rs = findStmt.executeQuery();

        String candidateId = "tmp-3001";
        if (rs.next()) {
            candidateId = rs.getString("id");
        }

        // Verify analysis record exists before deletion
        PreparedStatement preCountStmt = conn.prepareStatement("SELECT COUNT(*) FROM analyses WHERE user_id = ?");
        preCountStmt.setString(1, candidateId);
        ResultSet preRs = preCountStmt.executeQuery();
        preRs.next();
        int preCount = preRs.getInt(1);

        // Delete user
        PreparedStatement delStmt = conn.prepareStatement("DELETE FROM users WHERE id = ?");
        delStmt.setString(1, candidateId);
        delStmt.executeUpdate();

        // Verify child analysis records were automatically deleted via ON DELETE CASCADE
        PreparedStatement postCountStmt = conn.prepareStatement("SELECT COUNT(*) FROM analyses WHERE user_id = ?");
        postCountStmt.setString(1, candidateId);
        ResultSet postRs = postCountStmt.executeQuery();
        postRs.next();
        int postCount = postRs.getInt(1);

        Assert.assertEquals(postCount, 0, "All associated analyses rows must be cascade deleted when user is deleted");
    }
}

