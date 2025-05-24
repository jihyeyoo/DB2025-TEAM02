package DB2025Team02DAO;

import DB2025Team02DTO.MyStudyDTO;
import DB2025Team02DTO.UserDTO;
import DB2025Team02main.AppMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MyStudyDAO {

    public List<MyStudyDTO> getMyStudies(UserDTO user) {
        List<MyStudyDTO> studyList = new ArrayList<>();

        String sql = "SELECT sg.study_id, sg.name AS study_name, u.user_name AS leader_name, " +
                "sg.start_date, sg.status, sg.leader_id " +
                "FROM db2025team02GroupMembers gm " +
                "JOIN db2025team02StudyGroups sg ON gm.study_id = sg.study_id " +
                "JOIN db2025team02Users u ON sg.leader_id = u.user_id " +
                "WHERE gm.user_id = ? AND sg.status = 'ongoing' " +
                "ORDER BY sg.start_date DESC";

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
        try {
            // 1. 본인이 스터디장인지 확인
            String checkLeaderSql = "SELECT leader_id FROM db2025team02StudyGroups WHERE study_id = ?";
            try (PreparedStatement checkStmt = AppMain.conn.prepareStatement(checkLeaderSql)) {
                checkStmt.setInt(1, studyId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    int leaderId = rs.getInt("leader_id");

                    AppMain.conn.setAutoCommit(false);

                    // 2. GroupMembers 상태 'withdrawn'으로 변경
                    String updateMemberSql = "UPDATE db2025team02GroupMembers SET status = 'withdrawn' WHERE study_id = ? AND user_id = ?";
                    try (PreparedStatement updateStmt = AppMain.conn.prepareStatement(updateMemberSql)) {
                        updateStmt.setInt(1, studyId);
                        updateStmt.setInt(2, user.getUserId());
                        updateStmt.executeUpdate();
                    }

                    // 3. 만약 스터디장이면 StudyGroups.status = 'closed'로 변경
                    if (user.getUserId() == leaderId) {
                        String updateStudySql = "UPDATE db2025team02StudyGroups SET status = 'closed' WHERE study_id = ?";
                        try (PreparedStatement updateStudyStmt = AppMain.conn.prepareStatement(updateStudySql)) {
                            updateStudyStmt.setInt(1, studyId);
                            updateStudyStmt.executeUpdate();
                        }
                    }

                    AppMain.conn.commit();
                    AppMain.conn.setAutoCommit(true);
                    return true;
                }
            }
        } catch (Exception e) {
            try {
                AppMain.conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        return false;
    }
}
