package DB2025Team02DAO;

import DB2025Team02DTO.StudyEditDTO;
import DB2025Team02main.AppMain;

import java.sql.*;

public class StudyEditDAO {

    public boolean updateStudyInfo(StudyEditDTO dto) {
        String updateStudySql = "UPDATE db2025team02StudyGroups SET name=?, description=?, end_date=?, cert_method=? WHERE study_id=?";
        String updateRulesSql = "UPDATE db2025team02Rules SET ptsettle_cycle=?, last_modified=CURDATE() WHERE study_id=?";

        try {
            AppMain.conn.setAutoCommit(false);

            try (
                PreparedStatement studyStmt = AppMain.conn.prepareStatement(updateStudySql);
                PreparedStatement ruleStmt = AppMain.conn.prepareStatement(updateRulesSql)
            ) {
                // StudyGroups 업데이트
                studyStmt.setString(1, dto.getName());
                studyStmt.setString(2, dto.getDescription());
                studyStmt.setDate(3, dto.getEndDate());
                studyStmt.setString(4, dto.getCertMethod());
                studyStmt.setInt(5, dto.getStudyId());
                studyStmt.executeUpdate();

                // Rules 업데이트
                ruleStmt.setInt(1, dto.getSettlementCycle());
                ruleStmt.setInt(2, dto.getStudyId());
                ruleStmt.executeUpdate();

                AppMain.conn.commit();
                AppMain.conn.setAutoCommit(true);
                return true;
            } catch (Exception e) {
                AppMain.conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    public StudyEditDTO getStudyById(int studyId) {
        String sql = "SELECT sg.study_id, sg.name, sg.description, sg.start_date, sg.end_date, " +
                     "sg.cert_method, r.ptsettle_cycle, sg.leader_id " +
                     "FROM db2025team02StudyGroups sg " +
                     "JOIN db2025team02Rules r ON sg.study_id = r.study_id " +
                     "WHERE sg.study_id = ?";

        try (PreparedStatement pstmt = AppMain.conn.prepareStatement(sql)) {
            pstmt.setInt(1, studyId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new StudyEditDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("ptsettle_cycle"),
                    rs.getInt("leader_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
