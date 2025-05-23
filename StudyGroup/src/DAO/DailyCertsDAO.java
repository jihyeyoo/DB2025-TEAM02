package DAO;

import DTO.DailyCertsDTO;
import main.AppMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DailyCertsDAO {
    // 사용자의 인증 자료 제출
    public boolean submitCertification(int userId, int studyId, String certDate, String content) {
        String sql = "INSERT INTO DailyCerts (user_id, study_id, cert_date, content, is_approved) VALUES (?, ?, ?, ?, FALSE)";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, studyId);
            stmt.setDate(3, Date.valueOf(certDate));
            stmt.setString(4, content);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 승인 또는 반려 처리
    public boolean updateCertificationStatus(int certId, boolean isApproved) {
        String sql = "UPDATE DailyCerts SET is_approved = ? WHERE cert_id = ?";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isApproved);
            stmt.setInt(2, certId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 승인된 인증 개수 조회
    public int getApprovedCertCount(int userId, int studyId) {
        String sql = "SELECT COUNT(*) FROM DailyCerts WHERE user_id = ? AND study_id = ? AND is_approved = TRUE";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, studyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    // 승인 여부에 따라 인증 목록 조회
    public List<DailyCertsDTO> getCertsByApprovalStatus(int studyId, Boolean isApproved) {
        String sql = "SELECT * FROM DailyCerts WHERE study_id = ?";
        if (isApproved != null) {
            sql += " AND is_approved = ?";
        }

        List<DailyCertsDTO> certs = new ArrayList<>();

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            if (isApproved != null) {
                stmt.setBoolean(2, isApproved);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DailyCertsDTO cert = new DailyCertsDTO(
                            rs.getInt("cert_id"),
                            rs.getInt("user_id"),
                            rs.getInt("study_id"),
                            rs.getDate("cert_date"),
                            rs.getString("content"),
                            rs.getBoolean("is_approved")
                    );
                    certs.add(cert);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return certs;
    }
}


