package DB2025Team02DAO;

import DB2025Team02DTO.UserDTO;
import DB2025Team02main.AppMain;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * MyPage 화면에서 사용되는 DAO 클래스입니다. JDBC를 사용한 기능을 제공합니다.
 */
public class MyPageDAO {

    /**회원 탈퇴하는 메서드입니다. 본인이 스터디장인 스터디가 있다면 해당 스터디들을 StudyGroups.status = 'closed'로 변경한 이후 해당 User를 Users테이블에서 삭제합니다. */
    public static boolean withdrawUser(UserDTO user) {
        String findLeaderStudiesSql = "SELECT study_id FROM db2025team02StudyGroups WHERE leader_id = ?";
        String closeStudySql = "UPDATE db2025team02StudyGroups SET status = 'closed' WHERE study_id = ?";
        String deleteUserSql = "DELETE FROM db2025team02Users WHERE user_id = ?";

        try {
            AppMain.conn.setAutoCommit(false);

            try (PreparedStatement findStmt = AppMain.conn.prepareStatement(findLeaderStudiesSql)) {
                findStmt.setInt(1, user.getUserId());
                ResultSet rs = findStmt.executeQuery();
                while (rs.next()) {
                    int studyId = rs.getInt("study_id");
                    try (PreparedStatement closeStmt = AppMain.conn.prepareStatement(closeStudySql)) {
                        closeStmt.setInt(1, studyId);
                        closeStmt.executeUpdate();
                    }
                }
            }

            try (PreparedStatement deleteStmt = AppMain.conn.prepareStatement(deleteUserSql)) {
                deleteStmt.setInt(1, user.getUserId());
                int deleted = deleteStmt.executeUpdate();
                if (deleted == 0) {
                    AppMain.conn.rollback();
                    return false;
                }
            }

            AppMain.conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                AppMain.conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                AppMain.conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
