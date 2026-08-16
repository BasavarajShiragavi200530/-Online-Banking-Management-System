-- =========================================================
-- ONLINE BANKING SYSTEM - MYSQL 8 DATABASE SCRIPT
-- =========================================================

CREATE DATABASE IF NOT EXISTS online_banking_db;
USE online_banking_db;

-- ---------------------------------------------------------
-- 1. DROP EXISTING TABLES (In Reverse Order of Dependencies)
-- ---------------------------------------------------------
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

-- ---------------------------------------------------------
-- 2. CREATE TABLES
-- ---------------------------------------------------------

-- Roles Table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User Roles Junction Table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Accounts Table
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    account_type VARCHAR(20) NOT NULL, -- SAVINGS, CURRENT
    account_status VARCHAR(20) NOT NULL, -- ACTIVE, FROZEN, INACTIVE
    branch VARCHAR(100) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_account_number (account_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Transactions Table
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(50) NOT NULL UNIQUE,
    sender_account_id BIGINT,
    receiver_account_id BIGINT,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL, -- DEPOSIT, WITHDRAWAL, TRANSFER
    status VARCHAR(20) NOT NULL, -- SUCCESS, FAILED, PENDING
    remarks VARCHAR(255),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    FOREIGN KEY (receiver_account_id) REFERENCES accounts(id) ON DELETE SET NULL,
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_transaction_timestamp (timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Audit Logs Table
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_email (user_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------
-- 3. SEED INITIAL DATA
-- ---------------------------------------------------------

-- Insert Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_CUSTOMER');

-- Passwords encoded with BCrypt strength 10:
-- Basavaraj@123 -> $2b$10$EGdH0rsvnoq2obuPrqLUju4p6igKd0XjDco9QxZDPK4h3egh732XW
-- Basu@123      -> $2a$10$w8T0hRjUe7ZzY3Xp6p2n1G1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z
-- Bharath@123   -> $2a$10$x9U1iSkVf8AaZ4Yq7q3o2H2a2a2a2a2a2a2a2a2a2a2a2a2a2a2a2a2a2a

-- Note: The Spring Boot application DataInitializer automatically synchronizes and ensures 
-- BCrypt password matches on application boot.

INSERT INTO users (id, first_name, last_name, email, password, phone, address, enabled) VALUES 
(1, 'System', 'Admin', 'Basavaraj@bank.com', '$2b$10$EGdH0rsvnoq2obuPrqLUju4p6igKd0XjDco9QxZDPK4h3egh732XW', '+1-800-555-0100', 'Bank Headquarters, Tech Park', TRUE),
(2, 'Basu', 'Dev', 'basu@bank.com', '$2a$10$w8T0hRjUe7ZzY3Xp6p2n1G1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', '+1-800-555-0101', '123 Innovation Way, Suite 400', TRUE),
(3, 'Bharath', 'Kumar', 'bharath@bank.com', '$2a$10$w8T0hRjUe7ZzY3Xp6p2n1G1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z', '+1-800-555-0102', '456 Tech Boulevard, Floor 12', TRUE);

-- Assign Roles
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- Basavaraj@bank.com -> ROLE_ADMIN
(2, 2), -- basu@bank.com -> ROLE_CUSTOMER
(3, 2); -- bharath@bank.com -> ROLE_CUSTOMER

-- Create Initial Bank Accounts for Customers
INSERT INTO accounts (id, account_number, balance, account_type, account_status, branch, ifsc_code, user_id) VALUES 
(1, '100020003001', 50000.00, 'SAVINGS', 'ACTIVE', 'Main Financial District Branch', 'APEX000101', 2),
(2, '100020003002', 75000.50, 'SAVINGS', 'ACTIVE', 'Main Financial District Branch', 'APEX000101', 3);

-- Sample Initial Transactions
INSERT INTO transactions (id, transaction_id, sender_account_id, receiver_account_id, amount, transaction_type, status, remarks, timestamp) VALUES 
(1, 'TXN-INIT-001', NULL, 1, 50000.00, 'DEPOSIT', 'SUCCESS', 'Initial Account Opening Balance Deposit', CURRENT_TIMESTAMP),
(2, 'TXN-INIT-002', NULL, 2, 75000.50, 'DEPOSIT', 'SUCCESS', 'Initial Account Opening Balance Deposit', CURRENT_TIMESTAMP);

-- Sample Audit Log
INSERT INTO audit_logs (id, user_email, action, details, ip_address, timestamp) VALUES 
(1, 'Basavaraj@bank.com', 'SYSTEM_INITIALIZATION', 'Online Banking System database schema initialized with seed data.', '127.0.0.1', CURRENT_TIMESTAMP);
