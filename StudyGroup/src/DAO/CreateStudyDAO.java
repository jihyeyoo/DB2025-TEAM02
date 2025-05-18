package DAO;

import DTO.CreateStudyDTO;
import java.sql.*;

/**
 * StudyGroups 테이블에 스터디를 삽입하는 DAO 클래스
 */
public class CreateStudyDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/DB2025Team02";
    private static final String DB_USER = "DB2025Team02";
    private static final String DB_PASS = "DB2025Team02";

    public boolean insertStudy(CreateStudyDTO dto) {
        String sql = "INSERT INTO StudyGroups (name, leader_id, description, start_date, end_date, cert_method, deposit) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dto.getName());
            ps.setInt(2, dto.getLeaderId());
            ps.setString(3, dto.getDescription());
            ps.setString(4, dto.getStartDate());
            ps.setString(5, dto.getEndDate());
            ps.setString(6, dto.getCertMethod());
            ps.setInt(7, dto.getDeposit());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
