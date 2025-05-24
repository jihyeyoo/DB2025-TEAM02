package DB2025Team02main;

<<<<<<< HEAD:StudyGroup/src/main/AppMain.java
import GUI.Login;
import java.sql.*;
=======
import java.sql.*;

import DB2025Team02GUI.Login;
>>>>>>> bdb813e006aaa6e1725d7fc39feeb230c1585a70:StudyGroup/src/DB2025Team02main/AppMain.java

public class AppMain {
    public static Connection conn;

    public static void main(String[] args) {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String DBURL = "jdbc:mysql://localhost:3306/DB2025Team02";
        String DBID = "DB2025Team02";
        String DBPW = "DB2025Team02";

        try {
            Class.forName(DRIVER);

            conn = DriverManager.getConnection(DBURL, DBID, DBPW);
            System.out.println("connected");
            
            new Login();

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }
    }
}