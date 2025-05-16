package main;

import java.sql.*;

import GUI.SignUp;

public class AppMain {
    public static Connection conn;

    public static void main(String[] args) {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String DBURL = "jdbc:mysql://localhost:3306/db2025team02";
        String DBID = "root"; //본인 sql user 이름 입력하세요
        String DBPW = ""; //본인 sql pw 입력하세요

        try {
            Class.forName(DRIVER);

            conn = DriverManager.getConnection(DBURL, DBID, DBPW);
            System.out.println("연결되었습니다.");
            
            new SignUp();

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }
    }
}