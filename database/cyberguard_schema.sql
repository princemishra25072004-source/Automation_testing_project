-- ====================================================================
-- CyberGuard AI Platform - MySQL Database Schema
-- Subject: Automation Software Testing (25CAH-705)
-- Evaluated via Selenium, REST-Assured, and MySQL Connector/J
-- ====================================================================

CREATE DATABASE IF NOT EXISTS cyberguard;
USE cyberguard;

-- Table: users
DROP TABLE IF EXISTS analyses;
DROP TABLE IF EXISTS chat_imports;
DROP TABLE IF EXISTS sender_reputation;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: analyses
CREATE TABLE analyses (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    text_content TEXT NOT NULL,
    language VARCHAR(32) DEFAULT 'English',
    is_safe BOOLEAN NOT NULL DEFAULT TRUE,
    toxicity_score DECIMAL(5, 4) DEFAULT 0.0000,
    is_sarcastic BOOLEAN NOT NULL DEFAULT FALSE,
    severity ENUM('Safe', 'Low', 'Medium', 'High', 'Critical') NOT NULL DEFAULT 'Safe',
    category VARCHAR(64) DEFAULT 'General',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_analyses_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: chat_imports
CREATE TABLE chat_imports (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    platform VARCHAR(32) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    total_messages INT DEFAULT 0,
    toxic_messages INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Table: sender_reputation
CREATE TABLE sender_reputation (
    id VARCHAR(64) PRIMARY KEY,
    sender_handle VARCHAR(120) NOT NULL,
    total_messages INT DEFAULT 0,
    toxic_messages INT DEFAULT 0,
    toxicity_percentage DECIMAL(5, 2) DEFAULT 0.00,
    risk_level ENUM('Safe', 'Moderate', 'High', 'Critical') DEFAULT 'Safe',
    status VARCHAR(64) DEFAULT 'Active',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ====================================================================
-- SEED DATA (BCrypt Hashed Passwords - NEVER Plaintext)
-- Password for ankit.test@cyberguard.ai: Password@123
-- Password for admin@admin.cyberguard: AdminSecurePass!123
-- ====================================================================

-- Hash for "Password@123": $2b$12$e6mZz5Z2b04x.54E8v4HmeZg94aC7Zeq33W3E7QomV60Z47J5q7yC
-- Hash for "AdminSecurePass!123": $2b$12$D25s23P83rQ1lQZ6qYI1l.JkXg11d08V2qN4YyPq9e3T7H5.Z3P0K

INSERT INTO users (id, name, email, password_hash, role) VALUES 
('usr-1001', 'Ankit Mishra', 'ankit.test@cyberguard.ai', '$2b$12$e6mZz5Z2b04x.54E8v4HmeZg94aC7Zeq33W3E7QomV60Z47J5q7yC', 'user'),
('adm-2001', 'System Administrator', 'admin@admin.cyberguard', '$2b$12$D25s23P83rQ1lQZ6qYI1l.JkXg11d08V2qN4YyPq9e3T7H5.Z3P0K', 'admin'),
('tmp-3001', 'Cascade Delete Candidate', 'candidate.delete@cyberguard.ai', '$2b$12$e6mZz5Z2b04x.54E8v4HmeZg94aC7Zeq33W3E7QomV60Z47J5q7yC', 'user');

INSERT INTO analyses (id, user_id, text_content, language, is_safe, toxicity_score, is_sarcastic, severity, category) VALUES
('ana-01', 'usr-1001', 'Oh great, another brilliant genius who knows everything! वाह क्या बात है!', 'Hindi-English', FALSE, 0.4500, TRUE, 'Medium', 'Harassment'),
('ana-02', 'usr-1001', 'You are completely useless, nobody wants you here. Get lost!', 'English', FALSE, 0.8800, FALSE, 'High', 'Hate Speech'),
('ana-03', 'usr-1001', 'Good morning everyone! Hope you have a wonderful and productive day ahead.', 'English', TRUE, 0.0200, FALSE, 'Safe', 'General'),
('ana-04', 'tmp-3001', 'Temporary cascade analysis row for foreign key integrity verification', 'English', FALSE, 0.7000, FALSE, 'High', 'Toxic');

INSERT INTO sender_reputation (id, sender_handle, total_messages, toxic_messages, toxicity_percentage, risk_level, status) VALUES
('rep-01', '@troll_account_99', 50, 44, 88.00, 'Critical', 'Flagged for Review'),
('rep-02', '@spammer_bot', 100, 74, 74.00, 'High', 'Flagged for Review'),
('rep-03', '+91 98765 43210 (WhatsApp)', 32, 20, 62.00, 'High', 'Flagged for Review');

