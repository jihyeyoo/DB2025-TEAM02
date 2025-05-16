package main;

import java.sql.*;
import GUI.Login;

public class AppMain {
    public static Connection conn;

    public static void main(String[] args) {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String DBURL = "jdbc:mysql://localhost:3306/DB2025Team02";
        String DBID = "root"; // 본인 계정
        String DBPW = "cindy1004"; // 본인 비밀번호

        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DBURL, DBID, DBPW);
            System.out.println("DB 연결되었습니다.");

            new Login(); // 로그인 창부터 시작

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
            e.printStackTrace();
        }
    }
}
