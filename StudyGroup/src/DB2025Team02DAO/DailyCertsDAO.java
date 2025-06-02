package DB2025Team02DAO;

import DB2025Team02DTO.CertStatusDTO;
import DB2025Team02DTO.DailyCertsDTO;
import DB2025Team02DTO.MyStudyDTO;
import DB2025Team02DTO.RuleDTO;
import DB2025Team02main.AppMain;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 *  ManageCerts 화면에서 사용되며 사용자의 인증 내역을 조회하는 함수들을 포함한 DAO 클래스입니다. JDBC를 사용한 기능을 제공합니다.
 */
public class DailyCertsDAO {


	/**스터디의 시작일을 구하는 메서드입니다. 인증 기한은 스터디 시작 다음날 ~ (스터디 시작일 + 스터디 주기) 까지로 하기 때문에 스터디 시작 당일이나 스터디 시작 전에는 인증을 할 수 없도록 막아둡니다.*/
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


	/**사용자가 인증을 제출하는 메서드입니다. calculateCycleNo 함수에 의해 현재 몇 주기인지 계산되고, db2025team02DailyCerts 테이블에 인증 내역이 저장됩니다.*/
	public boolean submitCertification(int userId, int studyId, String certDateStr, String content, String approvalStatus) {
		String sql = "INSERT INTO db2025team02DailyCerts (user_id, study_id, cert_date, content, approval_status, cycle_no) VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			LocalDate certDate = LocalDate.parse(certDateStr);

			RuleDTO rule = getRuleInfo(studyId);
			LocalDate nextCertDate = rule.getNextCertDate().toLocalDate();
			int certCycle = rule.getCertCycle();
			int gracePeriod = rule.getGracePeriod();
			int cycleNo = calculateCycleNo(studyId, certDate);

			LocalDate prevCertEnd = nextCertDate.minusDays(certCycle);
			LocalDate graceStart = prevCertEnd.plusDays(1);
			LocalDate graceEnd = graceStart.plusDays(gracePeriod - 1);

			boolean inPrevCycleGrace = !certDate.isBefore(graceStart) && !certDate.isAfter(graceEnd);
			boolean prevCycleAlreadyCertified = hasCertifiedCycle(userId, studyId, cycleNo - 1);

			System.out.println("유예시작일 :"+ graceStart + "유예종료일"+ graceEnd);

			int finalCycleNo = (inPrevCycleGrace && !prevCycleAlreadyCertified) ? cycleNo - 1 : cycleNo;
			if (cycleNo == 1) {
				finalCycleNo = cycleNo;
			}
			else if (inPrevCycleGrace && !prevCycleAlreadyCertified) {
				System.out.println(">>> 지각 인증으로 간주됨 → CycleNo - 1");
			} else {
				System.out.println(">>> 정시 인증으로 간주됨 → cycleNo");
			}

			System.out.println(">>> 최종 저장 주차: " + finalCycleNo);


			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setDate(3, Date.valueOf(certDate));
			stmt.setString(4, content);
			stmt.setString(5, approvalStatus);
			stmt.setInt(6, finalCycleNo);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	/**현재 스터디가 몇 주기인지 계산하는 메서드입니다. studyId를 매개변수로 받아 db2025Team02StudyGroups 테이블에서 스터디 시작일을 가져오고,
	해당 스터디를 외래키로 가진 rule 테이블에서 인증 주기를 가져와 현재 스터디가 몇주기인지를 계산합니다.*/
	public int calculateCycleNo(int studyId, LocalDate certDate) {
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
					int certCycle = rs.getInt("cert_cycle");

					long daysBetween = ChronoUnit.DAYS.between(startDate, certDate);

					// 첫 주차 보정: certDate가 startDate로부터 certCycle일 미만일 경우 무조건 week 1
					if (daysBetween < certCycle) {
						System.out.println("[calculateCycleNo] 첫 주차: resultCycle = 1");
						return 1;
					}

					int resultCycle = (int) Math.ceil((daysBetween + 1) / (double) certCycle);
					System.out.println("[calculateCycleNo] resultCycle: " + resultCycle);
					return resultCycle;
				} else {
					System.out.println("[calculateCycleNo] No rule/study found for studyId=" + studyId);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[calculateCycleNo] SQL error for studyId=" + studyId);
		}

		return -1; // 조회 실패 시
	}

	/** 이미 해당 주차에 인증한 내역이 있으면 다시 인증을 막아두기 위한 메서드입니다. approval_status가 rejected인 경우는 인증하지 않은 것으로 처리하고, 사용자가 다시 인증을 제출할 수 있도록 합니다. */
	public boolean hasCertifiedCycle(int userId, int studyId, int cycleNo) {
		String sql = "SELECT COUNT(*) FROM db2025team02DailyCerts WHERE user_id = ? AND study_id = ? AND cycle_no = ? And approval_status != 'rejected'";

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setInt(3, cycleNo);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**내 인증 관리 페이지에서 인증의 상태를 조회할 때, 반려된 인증을 조회하기 위한 메서드입니다. 인증이 반려되었으면 '반려됨' 으로 ui에 표시되고, 다시 인증을 제출한 경우 '정상 제출 완료' 로 ui 상태가 바뀝니다. */
	public boolean hasRejectedCert(int userId, int studyId, int cycleNo) {
		String sql = "SELECT COUNT(*) FROM db2025team02DailyCerts WHERE user_id = ? AND study_id = ? AND cycle_no = ? AND approval_status = 'rejected'";

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setInt(3,cycleNo);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}


	/**스터디 인증 관리 페이지에서 특정 스터디의 내역 보기를 눌렀을 때 내가 제출한 모든 인증 내역을 조회하는 메서드입니다*/
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

	/**스터디장의 인증 승인 관리 페이지에서 대기중인 인증, 승인된 인증을 따로 불러올 수 있도록 approval_status 에 따라 인증을 조회하는 메서드입니다*/
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


	/** 스터디장의 인증 관리 페이지에서 인증을 승인하거나 반려했을 때 해당 인증의 approval_status를 업데이트하는 메서드입니다*/
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

	/** 스터디의 규칙 정보를 가져오는 메서드입니다, DailyCertsDAO 클래스 내에서 사용됩니다.*/
	private RuleDTO getRuleInfo(int studyId) {
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

	/**
	 * 인증 제출 상태를 판단하는 메서드입니다. calculateCycleNo 함수로 현재 주기를 계산하고,
	 * hasCertifiedCycle 함수로 이번 주기, 지난 주기의 인증 내역이 있는지 검사합니다.
	 * 1. 이번 주기가 첫 주기인 경우 : 지난 주기를 고려하지 않음
	 * 2. 이번 주기가 첫 주기가 아님 and 지난 주기 (정규 인증 기간) 내 인증 내역이 없음 and
	 *    지난 주기의 유예기간이 아직 지나지 않음 : 지난 주기 지각 제출할 수 있도록 함.
	 *    이 때 cycle_no는 지난 주기 기준으로 계산됩니다.
	 * 3. 이번주차 인증을 했지만 반려되었을 때 : 반려됨으로 표시
	 * 4. 이번주차 인증을 했지만 반려되었을 때 다시 제출을 했거나, 이번주차 인증을 처음 했을 때 :
	 *    정상 제출 완료로 표시 (approve_status는 pending이거나 approved)
	 * 5. 이번주차 제출 내역이 없을 때 : 미제출로 표시
	 */

	public CertStatusDTO getSubmitStatus(int userId, MyStudyDTO dto) {
		if (dto.getNextCertDate() == null) {
			return new CertStatusDTO("-", "-");
		}

		RuleDTO rule = getRuleInfo(dto.getStudyId());
		int certCycle = rule.getCertCycle();

		LocalDate thisCertStart = dto.getNextCertDate().toLocalDate().minusDays(certCycle - 1);
		int thisCycleNo = calculateCycleNo(dto.getStudyId(), thisCertStart);
		int prevCycleNo = thisCycleNo - 1;

		boolean hasPrevCycleCert = hasCertifiedCycle(userId, dto.getStudyId(), prevCycleNo);
		boolean hasThisCycleCert = hasCertifiedCycle(userId, dto.getStudyId(), thisCycleNo);
		boolean isRejected = hasRejectedCert(userId, dto.getStudyId(), thisCycleNo);
		boolean inPrevGrace = isPrevCycleInGracePeriod(dto.getStudyId());

		// 지각 제출 가능
		if (prevCycleNo > 1 && !hasPrevCycleCert && inPrevGrace) {
			LocalDate prevDeadline = dto.getNextCertDate().toLocalDate().minusDays(certCycle);
			LocalDate graceStart = prevDeadline.plusDays(1);
			return new CertStatusDTO(graceStart + " (지각)", "지난 주차 지각 제출 가능");
		}

		// 이번 주차 기준 상태
		String deadlineStr = dto.getNextCertDate().toString();
		if (isRejected && !hasThisCycleCert) {
			return new CertStatusDTO(deadlineStr, "반려됨");
		} else if (hasThisCycleCert) {
			return new CertStatusDTO(deadlineStr, "정상 제출 완료");
		} else {
			return new CertStatusDTO(deadlineStr, "미제출");
		}
	}


	/**벌금을 매길 때 지난주기 정규 인증 날짜 안에 제출을 했는지 검사하는 메서드입니다. thisCycleNo가 1보다 작을 때는 지난 주차가 없으므로 검사하지 않습니다. */
	public boolean hasPrevCycleCertified(int userId, int studyId) {
		RuleDTO rule = getRuleInfo(studyId);  // 스터디의 인증 규칙 정보 가져오기

		int thisCycleNo = calculateCycleNo(studyId, LocalDate.now());
		if (thisCycleNo <= 1){
			System.out.println("첫주차라 벌금 없음");
			return true;
		}
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


	/**벌금을 매길 때 지난주기 지각 인증 날짜 안에 제출을 했는지 검사하는 메서드입니다.
	위 메서드와 마찬가지로 thisCycleNo가 1보다 작을 때는 지난 주차가 없으므로 검사하지 않습니다. */
	public boolean hasPrevCycleCertifiedInGracePeriod(int userId, int studyId) {

		RuleDTO rule = getRuleInfo(studyId);
		if (rule == null || rule.getNextCertDate() == null) return false;

		LocalDate thisCertEnd = rule.getNextCertDate().toLocalDate();
		int certCycle = rule.getCertCycle();

		int thisCycleNo = calculateCycleNo(studyId, LocalDate.now());
		int lastCycleNo = thisCycleNo - 1;

		if (thisCycleNo <= 1) {
			System.out.println("첫주차라 벌금 없음");
			return true;
		}


		// 지난 주차의 grace 기간 계산
		LocalDate lastCycleEnd = thisCertEnd.minusDays(certCycle);
		LocalDate graceStart = lastCycleEnd.plusDays(1);
		LocalDate graceEnd = graceStart.plusDays(rule.getGracePeriod() - 1);

		System.out.println("=== [지난 주차 유예기간 인증 여부 확인] ===");
		System.out.println("▶ userId: " + userId + ", studyId: " + studyId);
		System.out.println("▶ certCycle: " + certCycle + ", gracePeriod: " + rule.getGracePeriod());
		System.out.println("▶ thisCycleNo: " + thisCycleNo + ", lastCycleNo: " + lastCycleNo);
		System.out.println("▶ lastCycleEnd: " + lastCycleEnd);
		System.out.println("▶ graceStart: " + graceStart + ", graceEnd: " + graceEnd);

		String sql = """
        SELECT 1 FROM db2025team02DailyCerts
        WHERE user_id = ? AND study_id = ? 
        AND cert_date BETWEEN ? AND ? 
        AND cycle_no = ?
        AND approval_status != 'rejected'
        LIMIT 1
    """;

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, userId);
			stmt.setInt(2, studyId);
			stmt.setDate(3, Date.valueOf(graceStart));
			stmt.setDate(4, Date.valueOf(graceEnd));
			stmt.setInt(5, lastCycleNo);

			System.out.println("▶ SQL 조회 조건: cert_date BETWEEN '" + graceStart + "' AND '" + graceEnd + "', week_no = " + lastCycleNo);

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

	/**getSubmitStatus 함수 안에서 inPreveGrace 변수를 계산하기 위한 메서드입니다. 현재 시간과 graceEnd를 비교하여 지난 주차의 유예 기간이 지났는지 검사합니다.*/
	public boolean isPrevCycleInGracePeriod(int studyId) {
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


