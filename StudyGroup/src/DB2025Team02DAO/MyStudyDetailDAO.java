package DB2025Team02DAO;

import DB2025Team02DTO.MyStudyDetailDTO;
import DB2025Team02DTO.StudyMemberDTO;
import DB2025Team02DTO.UserDTO;
import DB2025Team02DTO.RuleDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DB2025Team02main.AppMain;

public class MyStudyDetailDAO {

    // 1. 스터디 통계 + 기본 정보 (스터디명, 스터디장, 인원수, 총벌금)
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



	// 2. 참여자별 정보 (이름, 누적벌금)
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
    // 3. 규칙 정보
    public RuleDTO getRuleInfo(int studyId) {
        String sql = """
            SELECT cert_deadline, cert_cycle, grace_period,
                   fine_late, fine_absent, ptsettle_cycle, last_modified, next_cert_date
            FROM db2025team02Rules
            WHERE study_id = ?
        """;

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new RuleDTO(
                    rs.getTime("cert_deadline"),
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
    
    public boolean isLeader(UserDTO user, int studyId) {
        String sql = """
            SELECT COUNT(*)
            FROM db2025team02StudyGroups
            WHERE study_id = ? AND leader_id = ?
        """;

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            stmt.setInt(2, user.getUserId()); 
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


	//강퇴시키기
	public boolean kickMember(int studyId, int targetUserId) {

		String sql = """
        UPDATE db2025team02GroupMembers
        SET status = 'withdrawn'
        WHERE study_id = ? AND user_id = ? AND status = 'active'
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
	
	// 벌금 부과
	public boolean imposeFineIfOverdue(int studyId) {
	    RuleDTO rule = getRuleInfo(studyId);
	    List<StudyMemberDTO> members = getMemberList(studyId);
	    DailyCertsDAO certDAO = new DailyCertsDAO();

	    String deductPointSQL = "UPDATE db2025team02Users SET points = points - ? WHERE user_id = ?";
	    String addFineSQL = "UPDATE db2025team02GroupMembers SET accumulated_fine = accumulated_fine + ? WHERE study_id = ? AND user_id = ?";
	    String insertFineSQL = "INSERT INTO db2025team02Fines (user_id, study_id, is_paid, reason, amount, date) VALUES (?, ?, FALSE, ?, ?, CURDATE())";
	    String checkPointSQL = "SELECT points FROM db2025team02Users WHERE user_id = ?";
	    String suspendUserSQL = "UPDATE db2025team02GroupMembers SET status = 'suspended' WHERE study_id = ? AND user_id = ?";

	    try {
	        AppMain.conn.setAutoCommit(false); // 트랜잭션 시작

	        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

	        for (StudyMemberDTO member : members) {
	            int userId = member.getUserId();

	            boolean certified = certDAO.hasCertifiedBeforeDeadline(userId, studyId, rule.getNextCertDate());
	            if (certified) continue; // 인증했으면 벌금 부과하지 않음

	            // 현재 시간이 마감 시각을 넘었는지 확인
	            boolean isLate = false;
	            Time now = new Time(System.currentTimeMillis());
	            if (now.before(rule.getCertDeadline())) {
	                continue; // 아직 인증 마감 안 됐으면 벌금 부과 안 함
	            } else {
	                isLate = true; // 인증 안 했고, 시간도 넘음 → 벌금 대상
	            }

	            int fine = isLate ? rule.getFineAbsent() : rule.getFineLate();

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
	                    insertStmt.setString(3, "미인증");
	                    insertStmt.setInt(4, fine);
	                    insertStmt.executeUpdate();
	                }

	            } else {
	                // 포인트 부족 → 정지 처리
	                try (PreparedStatement suspendStmt = AppMain.conn.prepareStatement(suspendUserSQL)) {
	                    suspendStmt.setInt(1, studyId);
	                    suspendStmt.setInt(2, userId);
	                    suspendStmt.executeUpdate();
	                }
	            }
	        }

	        AppMain.conn.commit(); // 트랜잭션 커밋
	        AppMain.conn.setAutoCommit(true);
	        return true;

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

	    return false;
	}


}
