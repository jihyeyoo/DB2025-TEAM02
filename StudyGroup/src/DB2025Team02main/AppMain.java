package DB2025Team02main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DB2025Team02DTO.UserDTO;
import DB2025Team02GUI.Login;
import DB2025Team02util.CertDateUpdater;
import DB2025Team02util.StudyStatusUpdater;

/**
 * 애플리케이션의 진입점 클래스입니다.
 * - MySQL 데이터베이스에 연결하고,
 * - 인증 날짜 자동 갱신 스케줄러와 스터디 상태 갱신 스케줄러를 시작하며,
 * - 로그인 GUI를 실행합니다.
 */
public class AppMain {
    public static Connection conn;
    public static UserDTO currentUser;

    public static void main(String[] args) {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String DBURL = "jdbc:mysql://localhost:3306/db2025team02";
        String DBID = "DB2025Team02";
        String DBPW = "DB2025Team02";

        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DBURL, DBID, DBPW);
            System.out.println("connected");

            
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleAtFixedRate(
                new CertDateUpdater(conn),
                1,
                10, TimeUnit.SECONDS
            );

            scheduler.scheduleAtFixedRate(
                    new StudyStatusUpdater(conn),
                    1,
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
