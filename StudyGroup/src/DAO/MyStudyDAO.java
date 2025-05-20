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
        String sql = "SELECT sg.study_id, sg.name AS study_name, u.user_name AS leader_name, sg.start_date " +
                     "FROM GroupMembers gm " +
                     "JOIN StudyGroups sg ON gm.study_id = sg.study_id " +
                     "JOIN Users u ON sg.leader_id = u.user_id " +
                     "WHERE gm.user_id = ?";

        try (PreparedStatement pstmt = AppMain.conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());  // userId 바로 사용
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MyStudyDTO dto = new MyStudyDTO(
                        rs.getInt("study_id"),
                        rs.getString("study_name"),
                        rs.getString("leader_name"),
                        rs.getDate("start_date")
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
