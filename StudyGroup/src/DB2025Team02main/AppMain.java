package DB2025Team02main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DB2025Team02GUI.Login;
import DB2025Team02util.CertDateUpdater;

public class AppMain {
    public static Connection conn;

    public static void main(String[] args) {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String DBURL = "jdbc:mysql://localhost:3306/db2025team02";
        String DBID = "root"; // 본인 DB ID
        String DBPW = "1234"; // 본인 DB PW

        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DBURL, DBID, DBPW);
            System.out.println("connected");

            
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(
                new CertDateUpdater(conn),
                1, // delay (초)
                10, TimeUnit.SECONDS
            );

            new Login();

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }
    }
}
