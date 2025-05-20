package DAO;

import DTO.StudyListDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudyListDAO {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/DB2025Team02";
    private static final String DB_USER = "DB2025Team02";
    private static final String DB_PASS = "DB2025Team02";

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<StudyListDTO> getAllStudies() {
        List<StudyListDTO> list = new ArrayList<>();
        String sql = "SELECT study_id, name, start_date, end_date, cert_method, deposit FROM StudyGroups";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StudyListDTO dto = new StudyListDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit")
                );
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
//인덱스를 이용한 검색 메서드 추가
    public List<StudyListDTO> searchStudiesByName(String keyword) {
        List<StudyListDTO> list = new ArrayList<>();
        String sql = "SELECT study_id, name, start_date, end_date, cert_method, deposit " +
                     "FROM StudyGroups WHERE name LIKE ? ORDER BY name ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StudyListDTO dto = new StudyListDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit")
                );
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
