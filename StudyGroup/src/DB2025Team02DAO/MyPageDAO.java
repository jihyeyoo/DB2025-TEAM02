package DB2025Team02DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DB2025Team02DTO.DepositsDTO;
import DB2025Team02main.AppMain;

public class MyPageDAO {

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
    
    // 환급 정보 가져오는 메서드
    public List<DepositsDTO> getRefundedDepositsByUser(int userId) {
        String sql = "SELECT * FROM db2025team02Deposits WHERE user_id = ? AND is_refunded = TRUE";
        List<DepositsDTO> refundedDeposits = new ArrayList<>();

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DepositsDTO deposit = new DepositsDTO(
                        rs.getInt("deposit_id"),
                        rs.getInt("user_id"),
                        rs.getInt("study_id"),
                        rs.getInt("amount"),
                        rs.getDate("deposit_date"),
                        rs.getBoolean("is_refunded")
                    );
                    refundedDeposits.add(deposit);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return refundedDeposits;
    }

}
