package DB2025Team02DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import DB2025Team02main.AppMain;
/**
 * ChargePoint 화면에서 사용되는 포인트 관련 DAO 클래스입니다. JDBC를 사용한 기능을 제공합니다.
 */
public class ChargePointDAO {

    /** 사용자가 포인트를 충전할 수 있는 메서드입니다. */
    public boolean chargePoints(int userId, int amount) {
        String sql = "UPDATE db2025team02Users SET points = points + ? WHERE user_id = ?";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, userId);

            int result = stmt.executeUpdate();
            return result == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**사용자의 현재 포인트를 조회하는 메서드입니다.*/
    public int getUserPoints(int userId) {
        String sql = "SELECT points FROM db2025team02Users WHERE user_id = ?";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("points");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }



}
