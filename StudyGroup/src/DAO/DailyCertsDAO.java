package DAO;

import DTO.DailyCertsDTO;
import main.AppMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DailyCertsDAO {
	// 사용자의 인증 자료 제출
	public boolean submitCertification(int userId, int studyId, String certDate, String content, String approvalStatus) {
		String sql = "INSERT INTO DailyCerts (user_id, study_id, cert_date, content, approval_status) VALUES (?, ?, ?, ?, ?)";

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

	// 특정 스터디에서 특정 사용자의 인증 내역 전체 조회
	public List<DailyCertsDTO> getCertificationsForUser(int studyId, int userId) {
		String sql = "SELECT * FROM DailyCerts WHERE study_id = ? AND user_id = ? ORDER BY cert_date DESC";
		List<DailyCertsDTO> certs = new ArrayList<>();

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			stmt.setInt(2, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String status = rs.getString("approval_status");  // 👈 ENUM 값으로 읽어옴

					DailyCertsDTO cert = new DailyCertsDTO(
							rs.getInt("cert_id"),
							rs.getInt("user_id"),
							rs.getInt("study_id"),
							rs.getDate("cert_date"),
							rs.getString("content"),
							status  // ✅ isApproved → approvalStatus 로 수정
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
		String sql = "SELECT * FROM DailyCerts WHERE study_id = ? AND approval_status = ?";
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
		String sql = "UPDATE DailyCerts SET approval_status = ? WHERE cert_id = ?";
		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setString(1, status);
			stmt.setInt(2, certId);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}






	// 승인 여부에 따라 인증 목록 조회
	// 승인 상태 (ENUM 값)에 따라 인증 목록 조회
	public List<DailyCertsDTO> getCertsByApprovalStatus(int studyId, String approvalStatus) {
		String sql = "SELECT * FROM DailyCerts WHERE study_id = ?";
		if (approvalStatus != null && !approvalStatus.isEmpty()) {
			sql += " AND approval_status = ?";
		}

		List<DailyCertsDTO> certs = new ArrayList<>();

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			if (approvalStatus != null && !approvalStatus.isEmpty()) {
				stmt.setString(2, approvalStatus);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					DailyCertsDTO cert = new DailyCertsDTO(
							rs.getInt("cert_id"),
							rs.getInt("user_id"),
							rs.getInt("study_id"),
							rs.getDate("cert_date"),
							rs.getString("content"),
							rs.getString("approval_status")  // ✅ 수정됨
					);
					certs.add(cert);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return certs;
	}}


