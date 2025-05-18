package main;

import java.sql.*;

<<<<<<< HEAD
import GUI.Login;

public class AppMain {
    public static Connection conn;

    public static void main(String[] args) {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String DBURL = "jdbc:mysql://localhost:3306/db2025team02";
        String DBID = "root"; //본인 sql user 이름 입력하세요
        String DBPW = "1234"; //본인 sql pw 입력하세요

        try {
            Class.forName(DRIVER);

            conn = DriverManager.getConnection(DBURL, DBID, DBPW);
            System.out.println("connected");
            
            new Login(); // 로그인 창부터 시작

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }
    }
}
=======
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
>>>>>>> suyeon
