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
                   fine_late, fine_absent, ptsettle_cycle, last_modified
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
                    rs.getDate("last_modified")
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


}
