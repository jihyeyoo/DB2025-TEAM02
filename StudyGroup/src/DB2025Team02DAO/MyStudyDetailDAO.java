package DB2025Team02DAO;

import DB2025Team02DTO.MyStudyDetailDTO;
import DB2025Team02DTO.StudyMemberDTO;
import DB2025Team02DTO.UserDTO;
import DB2025Team02DTO.RuleDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import DB2025Team02main.AppMain;

public class MyStudyDetailDAO {

    /*마이 스터디 상세 페이지에서 스터디명, 스터디 총 멤버 수, 총 벌금을 가져오기 위해 db2025team02StudySummary View를 사용합니다.*/
	public MyStudyDetailDTO getStudySummary(int studyId) {
		String summarySql = """
        SELECT ss.study_id, ss.study_name,
               ss.member_count,
               ss.total_fine
        FROM db2025team02StudySummary ss
        WHERE ss.study_id = ?
        """;

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(summarySql)) {
			stmt.setInt(1, studyId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new MyStudyDetailDTO(
						rs.getInt("study_id"),
						rs.getString("study_name"),
						rs.getInt("member_count"),
						rs.getInt("total_fine")
				);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	/* 스터디별로 GroupMembers의 정보를 가져오는 메서드입니다. GroupMembers 테이블에서 누적 벌금을 가져오고,
	각 GroupMembers의 이름을 조회하기 위해 User테이블을 join하는 쿼리를 사용합니다.
	 */
	 public List<StudyMemberDTO> getMemberList(int studyId) {
	        List<StudyMemberDTO> list = new ArrayList<>();
	        String sql = """
	            SELECT
                 u.user_id,
                 u.user_name,
                 gm.accumulated_fine
             FROM db2025team02GroupMembers gm
             JOIN db2025team02Users u ON gm.user_id = u.user_id
             WHERE gm.study_id = ?
               AND gm.status = 'active'
             
	        """;


		 try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			 stmt.setInt(1, studyId);
			 ResultSet rs = stmt.executeQuery();
			 while (rs.next()) {
				 list.add(new StudyMemberDTO(
						 rs.getString("user_name"),
						 rs.getInt("accumulated_fine"),
						 rs.getInt("user_id")
				 ));
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 return list;
	    }

    /* 스터디의 규칙 정보를 가져와 MyStudyDetailPage에 표시하기 위한 메서드입니다.*/
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


	/*다른 사용자를 강퇴시키는 메서드입니다. GropMembers의 상태가 'withdrawn'으로 변경됩니다*/
	public boolean kickMember(int studyId, int targetUserId) {

		String sql = """
        UPDATE db2025team02GroupMembers
        SET status = 'withdrawn'
        WHERE study_id = ? AND user_id = ? AND status = 'active' or 'suspended'
    """;

		System.out.println("studyId: " + studyId + ", targetUserId: " + targetUserId);


		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			stmt.setInt(2, targetUserId);
			int updated = stmt.executeUpdate();
			return updated > 0; // 강퇴 성공 시 true
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*특정 스터디 그룹에 대해 벌금을 부과하는 로직을 수행하는 메서드입니다. 다음과 같은 흐름으로 작동합니다.
	 1. 해당 스터디의 규칙 정보(RuleDTO)를 불러오고, 현재 스터디에 속한 status= 'active'인 멤버들을 조회합니다.
	 2. 인증 정보를 판별하기 위해 DailyCertsDAO의 hasPrevCycleCertified, hasPrevCycleCertifiedInGracePeriod 함수를 통해 개별 멤버의 지난 주기 인증 상태를 확인합니다.
	 3. 인증 상태는 1. 지난 주차 인증 기간 내에 정상적으로 인증을 마친 경우, 2. 정상 인증은 하지 않았지만 유예 기간 내 인증을 한 경우,  3.인증도 하지 않았고 유예 기간 내에도 인증 기록이 없는 경우가 있습니다.
	 4. 아직 지난 주기 유예 기간이 종료되지 않은 경우, 벌금이 부과되지 않습니다.
	 5. 유예 기간이 종료된 후
	    인증 상태가 1인 경우 -> 벌금을 부과하지 않고 다음 멤버로 넘어갑니다.
	    인증 상태가 2인 경우 -> ‘지각’으로 간주하고 fine_late만큼 벌금을 부과합니다.
	    인증 상태가 3인 경우 -> ‘미제출’로 간주하고 fine_absent만큼 벌금을 부과합니다.
	 6. 벌금 부과 전에는 동일한 사유로 해당 기간 내에 이미 벌금이 부과된 적이 조회하여 중복 부과를 방지합니다.
	 7. 벌금 부과 전에 사용자의 point를 조회합니다.
	    포인트가 벌금 금액보다 많거나 같은 경우 -> 포인트를 차감한 후 GroupMembers 테이블의 accumulated_fine을 갱신하고 Fines 테이블에 벌금 내역을 기록합니다.
	    포인트가 부족한 경우
	     1) 사용자가 스터디 리더인 경우 -> 리더인 경우에는 포인트가 부족해도 정지 처리되지 않고 벌금도 부과되지 않습니다. 이는 리더 특권으로 간주되어 예외적으로 처리됩니다.
	     2) 사용자가 스터디 리더가 아닌 경우 -> 해당 멤버의 GroupMembers 상태를 suspended로 변경하여 활동을 정지시킵니다.
	*/
	public String imposeFineIfOverdue(int studyId) {
		StringBuilder resultMsg = new StringBuilder();
		int finedCount = 0;

	    RuleDTO rule = getRuleInfo(studyId);
	    List<StudyMemberDTO> members = getMemberList(studyId);
	    DailyCertsDAO certDAO = new DailyCertsDAO();


	    String deductPointSQL = "UPDATE db2025team02Users SET points = points - ? WHERE user_id = ?";
	    String addFineSQL = "UPDATE db2025team02GroupMembers SET accumulated_fine = accumulated_fine + ? WHERE study_id = ? AND user_id = ?";
		String insertFineSQL = "INSERT INTO db2025team02Fines (user_id, study_id, reason, amount, date) VALUES (?, ?,  ?, ?, ?)";

		String checkPointSQL = "SELECT points FROM db2025team02Users WHERE user_id = ?";
	    String suspendUserSQL = "UPDATE db2025team02GroupMembers SET status = 'suspended' WHERE study_id = ? AND user_id = ?";
		String checkAlreadyFinedSQL = "SELECT 1 FROM db2025team02Fines WHERE user_id = ? AND study_id = ? AND reason = ? AND date BETWEEN ? AND ?";


		LocalDate certEnd = rule.getNextCertDate().toLocalDate().minusDays(rule.getCertCycle()); // 기준일에서 한 주 전이 마지막 날
		LocalDate certStart = certEnd.minusDays(rule.getCertCycle() - 1); // 시작일

		LocalDate graceStart = certEnd.plusDays(1);
		LocalDate graceEnd = graceStart.plusDays(rule.getGracePeriod() - 1);
		java.sql.Date certStartDate = java.sql.Date.valueOf(certStart);
		java.sql.Date certEndDate = java.sql.Date.valueOf(certEnd);



		try {
	        AppMain.conn.setAutoCommit(false); // 트랜잭션 시작


			for (StudyMemberDTO member : members) {
				int userId = member.getUserId();
				String userName = member.getUserName();

				boolean certifiedOnTime = certDAO.hasPrevCycleCertified(userId, studyId);
				boolean certifiedInGrace = certDAO.hasPrevCycleCertifiedInGracePeriod(userId, studyId);

				String reason = null;
				int fine = 0;
//
				System.out.println("──────────── 인증 평가 ──────────────");
				System.out.println("스터디원 ID: " + userId);
				System.out.println("지난 인증 기간: " + certStartDate + " ~ " + certEndDate);

				LocalDate now = LocalDate.now();
				if (now.isBefore(graceEnd.plusDays(1))) {
					System.out.println("아직 유예 기간이 지나지 않았음 → 벌금 부과 보류");
					continue;
				}

				if (certifiedOnTime) {
					System.out.println("인증 상태: 정상 인증");
					continue;
				} else if (certifiedInGrace) {
					reason = "지각";
					fine = rule.getFineLate();
					System.out.println("인증 상태: 유예 기간 내 인증 (지각)");
				} else {
					reason = "미인증";
					fine = rule.getFineAbsent();
					System.out.println("인증 상태: 인증 미제출");
				}

				System.out.println("벌금 사유: " + reason);
				System.out.println("벌금 금액: " + fine);
				System.out.println("────────────────────────────────────");
				try (PreparedStatement checkFinedStmt = AppMain.conn.prepareStatement(checkAlreadyFinedSQL)) {
					checkFinedStmt.setInt(1, userId);
					checkFinedStmt.setInt(2, studyId);

					if (reason != null) {
						checkFinedStmt.setString(3, reason);
					} else {
						checkFinedStmt.setNull(3, java.sql.Types.VARCHAR);
					}

					checkFinedStmt.setDate(4, certStartDate);
					checkFinedStmt.setDate(5, certEndDate);

					ResultSet rs = checkFinedStmt.executeQuery();
					if (rs.next()) {
						System.out.println("이미 벌금 있음 → 스킵");
						continue;
					}
				}


				// 포인트 확인
	            int userPoints = 0;
	            try (PreparedStatement checkStmt = AppMain.conn.prepareStatement(checkPointSQL)) {
	                checkStmt.setInt(1, userId);
	                ResultSet rs = checkStmt.executeQuery();
	                if (rs.next()) {
	                    userPoints = rs.getInt("points");
	                }
	            }

	            // 포인트 차감 가능하면 벌금 부과
	            if (userPoints >= fine) {
	                try (PreparedStatement deductStmt = AppMain.conn.prepareStatement(deductPointSQL)) {
	                    deductStmt.setInt(1, fine);
	                    deductStmt.setInt(2, userId);
	                    deductStmt.executeUpdate();
	                }

	                try (PreparedStatement fineStmt = AppMain.conn.prepareStatement(addFineSQL)) {
	                    fineStmt.setInt(1, fine);
	                    fineStmt.setInt(2, studyId);
	                    fineStmt.setInt(3, userId);
	                    fineStmt.executeUpdate();
	                }

	                try (PreparedStatement insertStmt = AppMain.conn.prepareStatement(insertFineSQL)) {
	                    insertStmt.setInt(1, userId);
	                    insertStmt.setInt(2, studyId);
	                    insertStmt.setString(3, reason);
	                    insertStmt.setInt(4, fine);
						insertStmt.setDate(5, certEndDate);
	                    insertStmt.executeUpdate();
	                }

					resultMsg.append("💸 ").append(userName)
							.append(" (ID ").append(userId).append(") → '")
							.append(reason).append("' 벌금 ").append(fine).append("원 부과\n");

					finedCount++;

	            } else {
	                // 포인트 부족 → 정지 처리
					if (!isLeader(userId, studyId)) {
						try (PreparedStatement suspendStmt = AppMain.conn.prepareStatement(suspendUserSQL)) {
							suspendStmt.setInt(1, studyId);
							suspendStmt.setInt(2, userId);
							suspendStmt.executeUpdate();
						}
						resultMsg.append("💀 ").append(userName)
								.append(" (ID ").append(userId).append(") → 포인트 부족(")
								.append(userPoints).append("P) → 정지 처리됨\n");

						finedCount++;
					} else {
						resultMsg.append("⚠️ ").append(userName)
								.append(" (ID ").append(userId).append(") → 포인트 부족하지만 스터디 리더이므로 정지되지 않음\n");
						finedCount++;
					}
	            }
	        }

	        AppMain.conn.commit(); // 트랜잭션 커밋
	        AppMain.conn.setAutoCommit(true);
			return finedCount > 0 ? resultMsg.toString() : null;


		} catch (Exception e) {
	        try {
	            AppMain.conn.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	        e.printStackTrace();
	    }

	    try {
	        AppMain.conn.setAutoCommit(true);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return null;
	}

	/*사용자가 Leader인지 조회하는 메서드입니다. 만약 Leader인 경우, MyStudyDeatilPage에서 다른 사용자를 강퇴하는 버튼, 벌금을 부과하는 버튼이 활성화됩니다.
	또한 벌금 부과 함수에서 포인트가 부족해도 Leader인경우 suspend 상태로 변하지 않는 로직을 처리하는데도 사용됩니다.*/
	public boolean isLeader(int userId, int studyId) {
		String sql = """
        SELECT COUNT(*)
        FROM db2025team02StudyGroups
        WHERE study_id = ? AND leader_id = ?
    """;

		try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
			stmt.setInt(1, studyId);
			stmt.setInt(2, userId);
			ResultSet rs = stmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}



}
