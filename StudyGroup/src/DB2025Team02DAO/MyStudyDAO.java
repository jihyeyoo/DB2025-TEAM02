package DB2025Team02DAO;

import DB2025Team02DTO.MyStudyDTO;
import DB2025Team02DTO.UserDTO;
import DB2025Team02main.AppMain;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * MyStudy화면에서 사용되는 DAO 클래스입니다. JDBC를 사용한 기능을 제공합니다.
 */
public class MyStudyDAO {

    /**스터디에서 탈퇴하는 메서드입니다. 1. 본인이 스터디장이라면 StudyGroups.status = 'closed'로 변경됩니다. 2. 스터디장이 아닌 일반 멤버의 경우는 groupmembers의 상태가 'withdrawn'으로 변경됩니다. */
    public boolean withdrawFromStudy(int studyId, UserDTO user) {
        try {
            // 1. 본인이 스터디장인지 확인
            String checkLeaderSql = "SELECT leader_id FROM db2025team02StudyGroups WHERE study_id = ?";
            String updateMemberSql = "UPDATE db2025team02GroupMembers SET status = 'withdrawn' WHERE study_id = ? AND user_id = ?";
            String updateStudySql = "UPDATE db2025team02StudyGroups SET status = 'closed' WHERE study_id = ?";

            try (PreparedStatement checkStmt = AppMain.conn.prepareStatement(checkLeaderSql)) {
                checkStmt.setInt(1, studyId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    int leaderId = rs.getInt("leader_id");

                    AppMain.conn.setAutoCommit(false);

                    // 2. GroupMembers 상태 'withdrawn'으로 변경

                    try (PreparedStatement updateStmt = AppMain.conn.prepareStatement(updateMemberSql)) {
                        updateStmt.setInt(1, studyId);
                        updateStmt.setInt(2, user.getUserId());
                        updateStmt.executeUpdate();
                    }

                    // 3. 만약 스터디장이면 StudyGroups.status = 'closed'로 변경
                    if (user.getUserId() == leaderId) {
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
