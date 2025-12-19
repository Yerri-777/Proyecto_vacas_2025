package com.example.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    // Allow public key retrieval for some MySQL configurations and keep useSSL=false for local dev
    private static final String URL = "jdbc:mysql://localhost:3306/tienda?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "12345";

  
    public static final String APP_CONTEXT = "/tienda-backend";

  
    public static String getApiBaseUrl() {
       
        return "http://localhost:8080" + APP_CONTEXT + "/api";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    static {
        try {
            // Ensure MySQL driver is available
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not found: " + e.getMessage());
        }
    }

    /**
     * Quick test to verify DB connectivity.
     */
    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (Exception e) {
            System.out.println("DB testConnection failed: " + e.getMessage());
            return false;
        }
    }
}

