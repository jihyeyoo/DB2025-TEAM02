package DAO;

import DTO.RuleDTO;
import DTO.StudyDetailDTO;
import main.AppMain;
import java.sql.*;

public class StudyDetailDAO {

    public StudyDetailDTO getStudyDetail(int studyId) {
        String sql = "SELECT name, description, start_date, end_date, cert_method, deposit, status FROM StudyGroups WHERE study_id = ?";

        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql)) {
            ps.setInt(1, studyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 여기서 rule도 같이 조회
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
            SELECT cert_deadline, cert_cycle, grace_period,
                   fine_late, fine_absent, ptsettle_cycle, last_modified
            FROM Rules
            WHERE study_id = ?
        """;

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new RuleDTO(
                        rs.getTime("cert_deadline"),
                        rs.getInt("cert_cycle"),
                        rs.getInt("grace_period"),
                        rs.getInt("fine_late"),
                        rs.getInt("fine_absent"),
                        rs.getInt("ptsettle_cycle"),
                        rs.getDate("last_modified")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean isAlreadyJoined(int studyId, int userId) {
        String sql = "SELECT 1 FROM GroupMembers WHERE study_id = ? AND user_id = ? AND status = 'active'";

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
            String sql = """
            INSERT INTO GroupMembers (study_id, user_id, status, accumulated_fine)
            VALUES (?, ?, 'active', 0)
        """;

            try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
                stmt.setInt(1, studyId);
                stmt.setInt(2, userId);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

}
