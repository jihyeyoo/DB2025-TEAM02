package DB2025Team02DAO;

import DB2025Team02DTO.CreateStudyDTO;
import DB2025Team02DTO.RuleDTO;
import DB2025Team02main.AppMain;

import java.sql.*;

public class CreateStudyDAO {

    public boolean createStudyGroup(CreateStudyDTO dto) {
        String insertGroupSQL = """
            INSERT INTO db2025team02StudyGroups (name, leader_id, description, start_date, end_date, cert_method, deposit)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        String insertLeaderSQL = """
            INSERT INTO db2025team02GroupMembers (study_id, user_id, status)
            VALUES (?, ?, 'active')
        """;

        String insertRuleSQL = """
            INSERT INTO db2025team02Rules (study_id,  cert_cycle, grace_period, fine_late, fine_absent, ptsettle_cycle, last_modified)
            VALUES (?, ?, ?, ?, ?, ?, CURDATE())
        """;

        try {
            AppMain.conn.setAutoCommit(false);

            try (PreparedStatement groupStmt = AppMain.conn.prepareStatement(insertGroupSQL, Statement.RETURN_GENERATED_KEYS)) {
                groupStmt.setString(1, dto.getName());
                groupStmt.setInt(2, dto.getLeaderId());
                groupStmt.setString(3, dto.getDescription());
                groupStmt.setDate(4, Date.valueOf(dto.getStartDate()));
                groupStmt.setDate(5, Date.valueOf(dto.getEndDate()));
                groupStmt.setString(6, dto.getCertMethod());
                groupStmt.setInt(7, dto.getDeposit());

                int rows = groupStmt.executeUpdate();
                if (rows == 0) throw new SQLException("스터디 삽입 실패");

                try (ResultSet rs = groupStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int studyId = rs.getInt(1);

                        try (PreparedStatement leaderStmt = AppMain.conn.prepareStatement(insertLeaderSQL)) {
                            leaderStmt.setInt(1, studyId);
                            leaderStmt.setInt(2, dto.getLeaderId());
                            leaderStmt.executeUpdate();
                        }

                        RuleDTO rule = dto.getRule();
                        try (PreparedStatement ruleStmt = AppMain.conn.prepareStatement(insertRuleSQL)) {
                            ruleStmt.setInt(1, studyId);
                            ruleStmt.setInt(2, rule.getCertCycle());
                            ruleStmt.setInt(3, rule.getGracePeriod());
                            ruleStmt.setInt(4, rule.getFineLate());
                            ruleStmt.setInt(5, rule.getFineAbsent());
                            ruleStmt.setInt(6, rule.getPtSettleCycle());
                            ruleStmt.executeUpdate();
                        }

                        AppMain.conn.commit();
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            try {
                AppMain.conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                AppMain.conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
