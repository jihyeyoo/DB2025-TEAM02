package DB2025Team02util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CertDateUpdater implements Runnable {
    private final Connection conn;

    public CertDateUpdater(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
//        String sql = """
//            UPDATE DB2025Team02Rules r
//            JOIN DB2025Team02StudyGroups sg ON r.study_id = sg.study_id
//            SET r.next_cert_date =
//              CASE
//                WHEN CURDATE() <= sg.start_date THEN
//                  DATE_ADD(sg.start_date, INTERVAL r.cert_cycle DAY)
//                ELSE
//                  DATE_ADD(
//                    sg.start_date,
//                    INTERVAL CEIL(DATEDIFF(CURDATE(), sg.start_date) / r.cert_cycle) * r.cert_cycle DAY
//                  )
//              END
//        """;
//
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            int updated = pstmt.executeUpdate();
//            //System.out.println("next_cert_date 업데이트 완료 (행 수: " + updated + ")");
//        } catch (SQLException e) {
//            System.err.println("next_cert_date 업데이트 실패:");
//            e.printStackTrace();
//        }
    }
}
