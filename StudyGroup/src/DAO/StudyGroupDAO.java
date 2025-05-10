package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import main.AppMain;
import model.StudyGroup;

public class StudyGroupDAO {

    // 스터디 개설하는 메서드입니다!
    public boolean createStudyGroup(String name, int leaderId, String description, 
                                    Date startDate, Date endDate, String certMethod, int deposit) {
        String insertGroupSQL = "INSERT INTO StudyGroups (name, leader_id, description, start_date, end_date, cert_method, deposit) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertLeaderSQL = "INSERT INTO GroupMembers (study_id, user_id, status) VALUES (?, ?, 'active')";

        try (PreparedStatement groupStmt = AppMain.conn.prepareStatement(insertGroupSQL, Statement.RETURN_GENERATED_KEYS)) {
            // 1. StudyGroups 테이블에 스터디 추가
            groupStmt.setString(1, name);
            groupStmt.setInt(2, leaderId);
            groupStmt.setString(3, description);
            groupStmt.setDate(4, startDate);
            groupStmt.setDate(5, endDate);
            groupStmt.setString(6, certMethod);
            groupStmt.setInt(7, deposit);

            int rows = groupStmt.executeUpdate();

            if (rows > 0) {
                // 2. 생성된 study_id 가져오기
                try (ResultSet rs = groupStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int studyId = rs.getInt(1);

                        // 3. 리더를 GroupMembers에 자동 등록
                        try (PreparedStatement leaderStmt = AppMain.conn.prepareStatement(insertLeaderSQL)) {
                            leaderStmt.setInt(1, studyId);
                            leaderStmt.setInt(2, leaderId);
                            int memberRows = leaderStmt.executeUpdate();

                            return memberRows > 0;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<StudyGroup> getAllStudyGroups() { //스터디 목록 조회하는 메서드입니다
        String sql = "SELECT * FROM StudyGroups";
        List<StudyGroup> studyGroups = new ArrayList<>();

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                StudyGroup group = new StudyGroup(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getInt("leader_id"),
                    rs.getString("description"),
                    rs.getDate("start_date"),
                    rs.getDate("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit")
                );
                studyGroups.add(group);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studyGroups;
    }
    
    public StudyGroup getStudyGroupById(int studyId) { //스터디그룹 하나를 조회 (스터디 상세 페이지)
        String sql = "SELECT * FROM StudyGroups WHERE study_id = ?";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new StudyGroup(
                        rs.getInt("study_id"),
                        rs.getString("name"),
                        rs.getInt("leader_id"),
                        rs.getString("description"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("cert_method"),
                        rs.getInt("deposit")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // 조회 실패 시 null 반환
    }    
    

}
