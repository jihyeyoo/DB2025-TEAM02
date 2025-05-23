package DAO;

import DTO.MyStudyDTO;
import DTO.UserDTO;
import main.AppMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyStudyDAO {

    public boolean createStudyGroup(String name, int leaderId, String description,
                                    Date startDate, Date endDate, String certMethod, int deposit) {

        String insertGroupSQL = "INSERT INTO StudyGroups (name, leader_id, description, start_date, end_date, cert_method, deposit) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String insertLeaderSQL = "INSERT INTO GroupMembers (study_id, user_id, status) VALUES (?, ?, 'active')";

        try (PreparedStatement groupStmt = AppMain.conn.prepareStatement(insertGroupSQL, Statement.RETURN_GENERATED_KEYS)) {
            groupStmt.setString(1, name);
            groupStmt.setInt(2, leaderId);
            groupStmt.setString(3, description);
            groupStmt.setDate(4, startDate);
            groupStmt.setDate(5, endDate);
            groupStmt.setString(6, certMethod);
            groupStmt.setInt(7, deposit);

            int rows = groupStmt.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = groupStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int studyId = rs.getInt(1);

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

    public List<MyStudyDTO> getMyStudies(UserDTO user) {
        List<MyStudyDTO> studyList = new ArrayList<>();

        String sql = "SELECT sg.study_id, sg.name AS study_name, u.user_name AS leader_name, " +
                "sg.start_date, sg.status, sg.leader_id " +
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
                "  sg.start_date DESC";

        try (PreparedStatement pstmt = AppMain.conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MyStudyDTO dto = new MyStudyDTO(
                        rs.getInt("study_id"),
                        rs.getString("study_name"),
                        rs.getString("leader_name"),
                        rs.getDate("start_date"),
                        rs.getInt("leader_id"),
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
            pstmt.setInt(2, user.getUserId());
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getStudyCountByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM GroupMembers WHERE user_id = ?";
        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
