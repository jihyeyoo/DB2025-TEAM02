package DB2025Team02DAO;

import DB2025Team02DTO.RuleDTO;
import DB2025Team02DTO.StudyDetailDTO;
import DB2025Team02main.AppMain;
import java.sql.*;

public class StudyDetailDAO {

    public StudyDetailDTO getStudyDetail(int studyId) {
        String sql = "SELECT name, description, start_date, end_date, cert_method, deposit, status FROM db2025team02StudyGroups WHERE study_id = ?";

        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql)) {
            ps.setInt(1, studyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    RuleDTO rule = getRuleByStudyId(studyId);

                    return new StudyDetailDTO(
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("start_date"),
                            rs.getString("end_date"),
                            rs.getString("cert_method"),
                            rs.getInt("deposit"),
                            rs.getString("status"),
                            rule
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public RuleDTO getRuleByStudyId(int studyId) {
        String sql = """
            SELECT cert_cycle, grace_period,
                   fine_late, fine_absent, ptsettle_cycle, last_modified, next_cert_date
            FROM db2025team02Rules
            WHERE study_id = ?
        """;

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new RuleDTO(
                        rs.getInt("cert_cycle"),
                        rs.getInt("grace_period"),
                        rs.getInt("fine_late"),
                        rs.getInt("fine_absent"),
                        rs.getInt("ptsettle_cycle"),
                        rs.getDate("last_modified"),
                        rs.getDate("next_cert_date")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean isAlreadyJoined(int studyId, int userId) {
        String sql = "SELECT 1 FROM db2025team02GroupMembers WHERE study_id = ? AND user_id = ? AND status = 'active' or 'suspended'";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean iswWthdrawnUser(int studyId, int userId) {
        String sql = "SELECT 1 FROM db2025team02GroupMembers WHERE study_id = ? AND user_id = ? AND status = 'withdrawn'";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public boolean joinStudy(int studyId, int userId) {
        String deductPointSQL = """
        UPDATE db2025team02Users
        SET points = points - (
            SELECT deposit FROM db2025team02StudyGroups WHERE study_id = ?
        )
        WHERE user_id = ?
    """;

        String insertGroupMemberSQL = """
        INSERT INTO db2025team02GroupMembers (study_id, user_id, status, accumulated_fine)
        VALUES (?, ?, 'active', 0)
    """;

        String insertDepositSQL = """
        INSERT INTO db2025team02Deposits (user_id, study_id, amount, deposit_date, is_refunded)
        SELECT ?, ?, deposit, CURDATE(), FALSE
        FROM db2025team02StudyGroups
        WHERE study_id = ?
    """;

        try {
            AppMain.conn.setAutoCommit(false);

            // 1. 포인트 차감
            try (PreparedStatement deductStmt = AppMain.conn.prepareStatement(deductPointSQL)) {
                deductStmt.setInt(1, studyId);
                deductStmt.setInt(2, userId);
                int updated = deductStmt.executeUpdate();
                if (updated == 0) throw new SQLException("포인트 차감 실패");
                System.out.println(deductStmt);
            }

            // 2. 그룹 멤버 등록
            try (PreparedStatement insertGroupStmt = AppMain.conn.prepareStatement(insertGroupMemberSQL)) {
                insertGroupStmt.setInt(1, studyId);
                insertGroupStmt.setInt(2, userId);
                insertGroupStmt.executeUpdate();
            }

            // 3. 보증금 기록
            try (PreparedStatement depositStmt = AppMain.conn.prepareStatement(insertDepositSQL)) {
                depositStmt.setInt(1, userId);
                depositStmt.setInt(2, studyId);
                depositStmt.setInt(3, studyId);
                depositStmt.executeUpdate();
            }

            AppMain.conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                AppMain.conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;

        } finally {
            try {
                AppMain.conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean hasEnoughPoints(int userId, int studyId) {
        String sql = """
        SELECT u.points, s.deposit
        FROM db2025team02Users u
        JOIN db2025team02StudyGroups s ON 1=1
        WHERE u.user_id = ? AND s.study_id = ?
    """;

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, studyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int points = rs.getInt("points");
                int deposit = rs.getInt("deposit");
                return points >= deposit;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
