package DB2025Team02DAO;

import DB2025Team02DTO.DailyCertsDTO;
import DB2025Team02DTO.RuleDTO;
import DB2025Team02main.AppMain;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DailyCertsDAO {

	public LocalDate getStudyStartDate(int studyId) {
		String sql = "SELECT start_date FROM db2025team02StudyGroups WHERE study_id = ?";
		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getDate("start_date").toLocalDate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	//사용자의 인증 제출
	public boolean submitCertification(int userId, int studyId, String certDateStr, String content, String approvalStatus) {
		String sql = "INSERT INTO db2025team02DailyCerts (user_id, study_id, cert_date, content, approval_status, week_no) VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			LocalDate certDate = LocalDate.parse(certDateStr);

			RuleDTO rule = getRuleInfo(studyId);
			LocalDate nextCertDate = rule.getNextCertDate().toLocalDate();
			int certCycle = rule.getCertCycle();
			int gracePeriod = rule.getGracePeriod();
			int weekNo = calculateWeekNo(studyId, certDate);

			LocalDate prevCertEnd = nextCertDate.minusDays(certCycle);
			LocalDate graceStart = prevCertEnd.plusDays(1);
			LocalDate graceEnd = graceStart.plusDays(gracePeriod - 1);

			boolean inPrevWeekGrace = !certDate.isBefore(graceStart) && !certDate.isAfter(graceEnd);
			boolean prevWeekAlreadyCertified = hasCertifiedWeek(userId, studyId, weekNo - 1);


			int finalWeekNo = (inPrevWeekGrace && !prevWeekAlreadyCertified) ? weekNo - 1 : weekNo;
			if (weekNo == 1) {
				finalWeekNo = weekNo;
			}
			else if (inPrevWeekGrace && !prevWeekAlreadyCertified) {
				System.out.println(">>> 지각 인증으로 간주됨 → weekNo - 1");
			} else {
				System.out.println(">>> 정시 인증으로 간주됨 → weekNo");
			}

			System.out.println(">>> 최종 저장 주차: " + finalWeekNo);


			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setDate(3, Date.valueOf(certDate));
			stmt.setString(4, content);
			stmt.setString(5, approvalStatus);
			stmt.setInt(6, finalWeekNo);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	public int calculateWeekNo(int studyId, LocalDate certDate) {
		String sql = """
        SELECT sg.start_date, r.cert_cycle
        FROM DB2025Team02StudyGroups sg
        JOIN DB2025Team02Rules r ON sg.study_id = r.study_id
        WHERE sg.study_id = ?
    """;

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					LocalDate startDate = rs.getDate("start_date").toLocalDate();
					System.out.println(rs.getDate("start_date"));
					int certCycle = rs.getInt("cert_cycle");


					long daysBetween = ChronoUnit.DAYS.between(startDate, certDate);
					int resultWeek = (int)(daysBetween / certCycle) + 1;
					System.out.println("[calculateWeekNo] resultWeek: " + resultWeek);

					return resultWeek;
				} else {
					System.out.println("[calculateWeekNo] No rule/study found for studyId=" + studyId);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[calculateWeekNo] SQL error for studyId=" + studyId);
		}

		return -1; // 조회 실패 시
	}

	//인덱스 1번 활용  해당 주차에 인증한 내역이 있는지 조회하기
	public boolean hasCertifiedWeek(int userId, int studyId, int weekNo) {
		String sql = "SELECT COUNT(*) FROM db2025team02DailyCerts WHERE user_id = ? AND study_id = ? AND week_no = ? And approval_status != 'rejected'";

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setInt(3, weekNo);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	//반려된 인증
	public boolean hasRejectedCert(int userId, int studyId, int weekNo) {
		String sql = "SELECT COUNT(*) FROM db2025team02DailyCerts WHERE user_id = ? AND study_id = ? AND week_no = ? AND approval_status = 'rejected'";

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setInt(3, weekNo);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}



	// 승인된 인증 개수 조회
	public int getApprovedCertCount(int userId, int studyId) {
		String sql = "SELECT COUNT(*) FROM db2025team02DailyCerts WHERE user_id = ? AND study_id = ? AND approval_status= 'approved'";

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



	public RuleDTO getRuleInfo(int studyId) {
		String sql = """
            SELECT cert_cycle, grace_period,
                   fine_late, fine_absent, ptsettle_cycle, last_modified, next_cert_date
            FROM db2025team02Rules
            WHERE study_id = ?
        """;

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new RuleDTO(
						rs.getInt("cert_cycle"),
						rs.getInt("grace_period"),
						rs.getInt("fine_late"),
						rs.getInt("fine_absent"),
						rs.getInt("ptsettle_cycle"),
						rs.getDate("last_modified"),
						rs.getDate("next_cert_date")
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//지난주 정상 제출을 한 경우 - 인덱스 1번활용
	public boolean hasPrevWeekCertified(int userId, int studyId) {
		RuleDTO rule = getRuleInfo(studyId);  // 스터디의 인증 규칙 정보 가져오기

		int thisWeekNo = calculateWeekNo(studyId, LocalDate.now());
		if (thisWeekNo <= 1) return true;

		if (rule == null || rule.getNextCertDate() == null || rule.getCertCycle() <= 0) {
			return false;  // 규칙 정보 없으면 기본적으로 false 반환
		}
		LocalDate certEnd = rule.getNextCertDate().toLocalDate().minusDays(rule.getCertCycle()); // 기준일에서 한 주 전이 마지막 날
		LocalDate certStart = certEnd.minusDays(rule.getCertCycle() - 1); // 시작일

		LocalDate graceStart = certEnd.plusDays(1);
		LocalDate graceEnd = graceStart.plusDays(rule.getGracePeriod() - 1);


		java.sql.Date certStartDate = java.sql.Date.valueOf(certStart);
		java.sql.Date certEndDate = java.sql.Date.valueOf(certEnd);

		String sql = """
        SELECT COUNT(*) FROM DB2025Team02DailyCerts
        WHERE user_id = ? AND study_id = ? AND cert_date BETWEEN ? AND ?
        AND approval_status != 'rejected'
    """;

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setDate(3, certStartDate);
			stmt.setDate(4, certEndDate);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;  // 하나라도 인증했으면 true
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}


	//지난주 유예 기간 안에 지각 제출을 한 경우 - 인덱스 1번 활용
	public boolean hasPrevWeekCertifiedInGracePeriod(int userId, int studyId) {

		RuleDTO rule = getRuleInfo(studyId);
		if (rule == null || rule.getNextCertDate() == null) return false;

		LocalDate thisCertEnd = rule.getNextCertDate().toLocalDate();
		int certCycle = rule.getCertCycle();

		int thisWeekNo = calculateWeekNo(studyId, LocalDate.now());
		int lastWeekNo = thisWeekNo - 1;

		if (thisWeekNo <= 1) {
			return true;
		}


		// 지난 주차의 grace 기간 계산
		LocalDate lastWeekEnd = thisCertEnd.minusDays(certCycle);
		LocalDate graceStart = lastWeekEnd.plusDays(1);
		LocalDate graceEnd = graceStart.plusDays(rule.getGracePeriod() - 1);

		System.out.println("=== [지난 주차 유예기간 인증 여부 확인] ===");
		System.out.println("▶ userId: " + userId + ", studyId: " + studyId);
		System.out.println("▶ certCycle: " + certCycle + ", gracePeriod: " + rule.getGracePeriod());
		System.out.println("▶ thisWeekNo: " + thisWeekNo + ", lastWeekNo: " + lastWeekNo);
		System.out.println("▶ lastWeekEnd: " + lastWeekEnd);
		System.out.println("▶ graceStart: " + graceStart + ", graceEnd: " + graceEnd);

		String sql = """
        SELECT 1 FROM db2025team02DailyCerts
        WHERE user_id = ? AND study_id = ? 
        AND cert_date BETWEEN ? AND ? 
        AND week_no = ?
        AND approval_status != 'rejected'
        LIMIT 1
    """;

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setDate(3, Date.valueOf(graceStart));
			stmt.setDate(4, Date.valueOf(graceEnd));
			stmt.setInt(5, lastWeekNo);

			System.out.println("▶ SQL 조회 조건: cert_date BETWEEN '" + graceStart + "' AND '" + graceEnd + "', week_no = " + lastWeekNo);

			try (ResultSet rs = stmt.executeQuery()) {
				boolean result = rs.next();
				System.out.println("▶ 인증 존재 여부: " + result);
				return result;
			}
		} catch (SQLException e) {
			System.out.println("▶ SQL 실행 오류 발생");
			e.printStackTrace();
			return false;
		}
	}


	//지난 주차의 유예 기간이 아직 안 끝났는지 확인
	public boolean isPrevWeekInGracePeriod(int studyId) {
		RuleDTO rule = getRuleInfo(studyId);
		if (rule == null || rule.getNextCertDate() == null) return false;

		LocalDate thisCertEnd = rule.getNextCertDate().toLocalDate();
		LocalDate prevCertEnd = thisCertEnd.minusDays(rule.getCertCycle());
		LocalDate graceStart = prevCertEnd.plusDays(1);
		LocalDate graceEnd = graceStart.plusDays(rule.getGracePeriod() - 1);

		LocalDate today = LocalDate.now();
		return !today.isBefore(graceStart) && !today.isAfter(graceEnd);
	}




}


