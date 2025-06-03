package DB2025Team02util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 * 다음 인증 마감일을 현재 날짜와 스터디의 시작일, 인증 주기를 이용하여 계산해 자동으로 업데이트하는 클래스입니다.
 */
public class CertDateUpdater implements Runnable {
    private final Connection conn;

    public CertDateUpdater(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        String sql = """
            UPDATE DB2025Team02Rules r
            JOIN DB2025Team02StudyGroups sg ON r.study_id = sg.study_id
            SET r.next_cert_date =
              CASE
                WHEN CURDATE() <= sg.start_date THEN
                  DATE_ADD(sg.start_date, INTERVAL r.cert_cycle DAY)
                ELSE
                  DATE_ADD(
                    sg.start_date,
                    INTERVAL CEIL(DATEDIFF(CURDATE(), sg.start_date) / r.cert_cycle) * r.cert_cycle DAY
                  )
              END
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("next_cert_date 업데이트 실패:");
            e.printStackTrace();
        }
    }
}
