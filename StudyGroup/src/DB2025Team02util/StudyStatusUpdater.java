package DB2025Team02util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 종료일이 지난 스터디의 상태를 closed로 업데이트 하는 클래스입니다. 이 때 참여자의 보증금이 반환되고, 참여자의 상태도 completed로 변경됩니다.
 */
public class StudyStatusUpdater implements Runnable {
    private final Connection conn;

    public StudyStatusUpdater(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        try {
            conn.setAutoCommit(false);

            String closeStudySql = """
                UPDATE DB2025Team02StudyGroups
                SET status = 'closed'
                WHERE end_date < CURDATE() AND status != 'closed'
            """;
            try (PreparedStatement stmt = conn.prepareStatement(closeStudySql)) {
                stmt.executeUpdate();
            }

            String completeMembersSql = """
                UPDATE DB2025Team02GroupMembers gm
                JOIN DB2025Team02StudyGroups sg ON gm.study_id = sg.study_id
                SET gm.status = 'completed'
                WHERE sg.status = 'closed' AND gm.status = 'active'
            """;
            try (PreparedStatement stmt = conn.prepareStatement(completeMembersSql)) {
                stmt.executeUpdate();
            }

            String refundSql = """
                UPDATE DB2025Team02Users u
                JOIN DB2025Team02Deposits d ON u.user_id = d.user_id
                JOIN DB2025Team02StudyGroups sg ON d.study_id = sg.study_id
                SET u.points = u.points + sg.deposit,
                    d.is_refunded = TRUE
                WHERE sg.status = 'closed' AND d.is_refunded = FALSE
            """;
            try (PreparedStatement stmt = conn.prepareStatement(refundSql)) {
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
                System.err.println("스터디 상태 갱신 중 오류 발생. 롤백 수행.");
            } catch (SQLException rollbackEx) {
                System.err.println("롤백 중 오류 발생:");
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}