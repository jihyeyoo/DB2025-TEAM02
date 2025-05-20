package DAO;

import DTO.CreateStudyDTO;
import main.AppMain;

import java.sql.*;

/**
 * StudyGroups 테이블에 스터디를 삽입하는 DAO 클래스
 */
public class CreateStudyDAO {

    public boolean createStudyGroup(CreateStudyDTO dto) {
        String insertGroupSQL = "INSERT INTO StudyGroups (name, leader_id, description, start_date, end_date, cert_method, deposit) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertLeaderSQL = "INSERT INTO GroupMembers (study_id, user_id, status) VALUES (?, ?, 'active')";

        try (PreparedStatement groupStmt = AppMain.conn.prepareStatement(insertGroupSQL, Statement.RETURN_GENERATED_KEYS)) {
            // 1. StudyGroups 테이블에 스터디 추가
            groupStmt.setString(1, dto.getName());
            groupStmt.setInt(2, dto.getLeaderId());
            groupStmt.setString(3, dto.getDescription());
            groupStmt.setDate(4, Date.valueOf(dto.getStartDate()));  // String → Date 변환
            groupStmt.setDate(5, Date.valueOf(dto.getEndDate()));
            groupStmt.setString(6, dto.getCertMethod());
            groupStmt.setInt(7, dto.getDeposit());

            int rows = groupStmt.executeUpdate();

            if (rows > 0) {
                // 2. 생성된 study_id 가져오기
                try (ResultSet rs = groupStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int studyId = rs.getInt(1);

                        // 3. 리더를 GroupMembers에 자동 등록
                        try (PreparedStatement leaderStmt = AppMain.conn.prepareStatement(insertLeaderSQL)) {
                            leaderStmt.setInt(1, studyId);
                            leaderStmt.setInt(2, dto.getLeaderId());
                            int memberRows = leaderStmt.executeUpdate();

                            return memberRows > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
