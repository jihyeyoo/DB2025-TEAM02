package DAO;

import DTO.StudyDetailDTO;
import java.sql.*;

public class StudyDetailDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/DB2025Team02";
    private static final String DB_USER = "DB2025Team02";
    private static final String DB_PASS = "DB2025Team02";

    public StudyDetailDTO getStudyDetail(int studyId) {
        String sql = "SELECT name, description, start_date, end_date, cert_method, deposit FROM StudyGroups WHERE study_id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studyId);
            ResultSet rs = ps.executeQuery();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
