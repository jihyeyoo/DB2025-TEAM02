package DAO;

import DTO.ReviewCertHistoryDTO;
import main.AppMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewCertHistoryDAO {
    public List<ReviewCertHistoryDTO> getUserCertHistory(int studyId, int userId) {
        List<ReviewCertHistoryDTO> list = new ArrayList<>();
        String sql = "SELECT cert_date, content, is_approved FROM DailyCerts WHERE study_id = ? AND user_id = ? ORDER BY cert_date DESC";

        try (Connection conn = AppMain.conn;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studyId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String date = rs.getString("cert_date");
                    String content = rs.getString("content");
                    boolean approved = rs.getBoolean("is_approved");

                    list.add(new ReviewCertHistoryDTO(date, content, approved));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
