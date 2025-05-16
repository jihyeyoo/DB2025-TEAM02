package main;

import java.sql.*;

public class AppMain {
    public static Connection conn;

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/db2025team02";
    static final String USER = "root";
    static final String PASS = "";

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            if (conn != null) {
                System.out.println("✅ Database connected successfully.");
            } else {
                System.out.println("❌ Failed to connect to the database.");
            }
        } catch (SQLException se) {
            System.out.println("❌ Database connection error.");
            se.printStackTrace();
        }
    }
}
