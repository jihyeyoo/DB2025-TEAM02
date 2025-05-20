package DAO;

import DTO.StudyDetailDTO;
import main.AppMain;
import java.sql.*;

public class StudyDetailDAO {

    public StudyDetailDTO getStudyDetail(int studyId) {
        String sql = "SELECT name, description, start_date, end_date, cert_method, deposit FROM StudyGroups WHERE study_id = ?";

        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql)) {
            ps.setInt(1, studyId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StudyDetailDTO(
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("cert_method"),
                        rs.getInt("deposit")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
