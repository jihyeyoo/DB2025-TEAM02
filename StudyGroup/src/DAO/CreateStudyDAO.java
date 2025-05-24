package DAO;

import DTO.CreateStudyDTO;
import DTO.RuleDTO;
import main.AppMain;

import java.sql.*;

public class CreateStudyDAO {

    public boolean createStudyGroup(CreateStudyDTO dto) {
        String insertGroupSQL = """
            INSERT INTO StudyGroups (name, leader_id, description, start_date, end_date, cert_method, deposit)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        String insertLeaderSQL = """
            INSERT INTO GroupMembers (study_id, user_id, status)
            VALUES (?, ?, 'active')
        """;

        String insertRuleSQL = """
            INSERT INTO Rules (study_id, cert_deadline, cert_cycle, grace_period, fine_late, fine_absent, ptsettle_cycle, last_modified)
            VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())
        """;

        try {
            AppMain.conn.setAutoCommit(false);  // 트랜잭션 시작

            // 1. StudyGroups INSERT
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

                        // 2. GroupMembers INSERT (리더)
                        try (PreparedStatement leaderStmt = AppMain.conn.prepareStatement(insertLeaderSQL)) {
                            leaderStmt.setInt(1, studyId);
                            leaderStmt.setInt(2, dto.getLeaderId());
                            leaderStmt.executeUpdate();
                        }

                        // 3. Rules INSERT
                        RuleDTO rule = dto.getRule();  // 🔥 포함된 rule DTO
                        try (PreparedStatement ruleStmt = AppMain.conn.prepareStatement(insertRuleSQL)) {
                            ruleStmt.setInt(1, studyId);
                            ruleStmt.setTime(2, rule.getCertDeadline());
                            ruleStmt.setInt(3, rule.getCertCycle());
                            ruleStmt.setInt(4, rule.getGracePeriod());
                            ruleStmt.setInt(5, rule.getFineLate());
                            ruleStmt.setInt(6, rule.getFineAbsent());
                            ruleStmt.setInt(7, rule.getPtSettleCycle());
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
