package DAO;

import DTO.MyStudyDetailDTO;
import DTO.StudyMemberDTO;
import DTO.UserDTO;
import DTO.RuleDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.AppMain;

public class MyStudyDetailDAO {

    // 1. 스터디 통계 + 기본 정보 (스터디명, 스터디장, 인원수, 총벌금)
	 public MyStudyDetailDTO getStudySummary(int studyId) {
	        String sql = """
	            SELECT study_id, study_name, member_count, total_fine
	            FROM StudyMember_Summary
	            WHERE study_id = ?
	            LIMIT 1
	        """;
	        
	        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
	            stmt.setInt(1, studyId);
	            ResultSet rs = stmt.executeQuery();
	            if (rs.next()) {
	                return new MyStudyDetailDTO(
	                    rs.getInt("study_id"),
	                    rs.getString("study_name"),
	                    rs.getInt("member_count"),
	                    rs.getInt("total_fine"),
	                    null // 최근 규칙 수정일은 Rules 테이블에서 별도 조회 필요
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
	            SELECT user_name, accumulated_fine
	            FROM StudyMember_Summary
	            WHERE study_id = ?
	        """;

	        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
	            stmt.setInt(1, studyId);
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                list.add(new StudyMemberDTO(
	                    rs.getString("user_name"),
	                    rs.getInt("accumulated_fine")
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
            FROM Rules
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
            FROM StudyGroups
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

}
