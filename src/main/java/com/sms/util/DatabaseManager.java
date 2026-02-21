package com.sms.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_PATH = "data/students.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        new File("data").mkdirs();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            initializeSchema();
            AppLogger.info("Database connected: " + DB_PATH);
        } catch (SQLException e) {
            AppLogger.error("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Cannot connect to database", e);
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeSchema() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS students (
                    student_id   TEXT PRIMARY KEY,
                    full_name    TEXT NOT NULL,
                    programme    TEXT NOT NULL,
                    level        INTEGER NOT NULL CHECK(level IN (100,200,300,400,500,600,700)),
                    gpa          REAL NOT NULL CHECK(gpa >= 0.0 AND gpa <= 4.0),
                    email        TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    date_added   TEXT NOT NULL,
                    status       TEXT NOT NULL DEFAULT 'Active'
                );
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                AppLogger.info("Database connection closed.");
            }
        } catch (SQLException e) {
            AppLogger.error("Error closing database: " + e.getMessage());
        }
    }
}
