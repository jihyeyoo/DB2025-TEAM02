package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import main.AppMain;

public class MyPageDAO {

    public boolean chargePoints(int userId, int amount) {
        String sql = "UPDATE Users SET points = points + ? WHERE user_id = ?";

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
}
