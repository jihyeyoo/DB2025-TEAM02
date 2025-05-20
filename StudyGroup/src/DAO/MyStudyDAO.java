package DAO;

import DTO.MyStudyDTO;
import DTO.UserDTO;
import main.AppMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyStudyDAO {

    
    
    public List<MyStudyDTO> getMyStudies(UserDTO user) {
        List<MyStudyDTO> studyList = new ArrayList<>();
        String sql = "SELECT sg.study_id, sg.name AS study_name, u.user_name AS leader_name, sg.start_date, sg.status" +
                "FROM GroupMembers gm " +
                "JOIN StudyGroups sg ON gm.study_id = sg.study_id " +
                "JOIN Users u ON sg.leader_id = u.user_id " +
                "WHERE gm.user_id = ? " +
                "ORDER BY " +
                "  CASE sg.status " +
                "    WHEN '진행중' THEN 0 " +
                "    WHEN '모집중' THEN 1 " +
                "    WHEN '종료' THEN 2 " +
                "    ELSE 3 " +
                "  END, " +
                "  sg.start_date DESC"; //status 인덱스를 사용해 진행중, 모집중, 종료 순서로 출력되도록 수정

        try (PreparedStatement pstmt = AppMain.conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());  // userId 바로 사용
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MyStudyDTO dto = new MyStudyDTO(
                        rs.getInt("study_id"),
                        rs.getString("study_name"),
                        rs.getString("leader_name"),
                        rs.getDate("start_date"),
                        rs.getString("status")
                );
                studyList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studyList;
    }

    public boolean withdrawFromStudy(int studyId, UserDTO user) {
        String sql = "DELETE FROM GroupMembers WHERE study_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = AppMain.conn.prepareStatement(sql)) {
            pstmt.setInt(1, studyId);
            pstmt.setInt(2, user.getUserId());  // userId 바로 사용
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
