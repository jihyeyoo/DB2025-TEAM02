package DB2025Team02DAO;

import DB2025Team02DTO.DailyCertsDTO;
import DB2025Team02main.AppMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DailyCertsDAO {
	// 사용자의 인증 자료 제출
	public boolean submitCertification(int userId, int studyId, String certDate, String content, String approvalStatus) {
		String sql = "INSERT INTO db2025team02DailyCerts (user_id, study_id, cert_date, content, approval_status) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setDate(3, Date.valueOf(certDate));
			stmt.setString(4, content);
			stmt.setString(5, approvalStatus);  // "pending", "approved", "rejected" 중 하나

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean hasCertifiedBeforeDeadline(int userId, int studyId, Date deadlineDate) {
	    String sql = """
	        SELECT COUNT(*) FROM DB2025Team02DailyCerts
	        WHERE user_id = ? AND study_id = ? AND cert_date <= ?
	    """;

	    try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        stmt.setInt(2, studyId);
	        stmt.setDate(3, deadlineDate);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1) > 0; // 하나라도 있으면 인증한 것으로 간주
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}



	// 승인된 인증 개수 조회
	public int getApprovedCertCount(int userId, int studyId) {
		String sql = "SELECT COUNT(*) FROM db2025team02DailyCerts WHERE user_id = ? AND study_id = ? AND is_approved = TRUE";

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

	// 특정 스터디에서 특정 사용자의 인증 내역 전체 조회
	public List<DailyCertsDTO> getCertificationsForUser(int studyId, int userId) {
		String sql = "SELECT * FROM db2025team02DailyCerts WHERE study_id = ? AND user_id = ? ORDER BY cert_date DESC";
		List<DailyCertsDTO> certs = new ArrayList<>();

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			stmt.setInt(2, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String status = rs.getString("approval_status");

					DailyCertsDTO cert = new DailyCertsDTO(
							rs.getInt("cert_id"),
							rs.getInt("user_id"),
							rs.getInt("study_id"),
							rs.getDate("cert_date"),
							rs.getString("content"),
							status
					);
					certs.add(cert);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return certs;
	}

	public List<DailyCertsDTO> getCertsByStatus(int studyId, String status) {
		String sql = "SELECT * FROM db2025team02DailyCerts WHERE study_id = ? AND approval_status = ?";
		List<DailyCertsDTO> list = new ArrayList<>();

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			stmt.setString(2, status);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					list.add(new DailyCertsDTO(
							rs.getInt("cert_id"),
							rs.getInt("user_id"),
							rs.getInt("study_id"),
							rs.getDate("cert_date"),
							rs.getString("content"),
							rs.getString("approval_status")
					));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean updateCertificationStatus(int certId, String status) {
		String sql = "UPDATE db2025team02DailyCerts SET approval_status = ? WHERE cert_id = ?";
		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setString(1, status);
			stmt.setInt(2, certId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	//인덱스 1번 활용 - 인증 기한 내에 인증 제출을 했으면 제출이 되지 않도록함
	public boolean hasCertifiedWithinPeriod(int userId, int studyId, Date startDate, Date endDate) {
		String sql = """
            SELECT COUNT(*) FROM DB2025Team02DailyCerts
            WHERE user_id = ? AND study_id = ? AND cert_date BETWEEN ? AND ?
        """;

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setDate(3, startDate);
			stmt.setDate(4, endDate);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;  // 인증 기록이 하나라도 있으면 true
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}


