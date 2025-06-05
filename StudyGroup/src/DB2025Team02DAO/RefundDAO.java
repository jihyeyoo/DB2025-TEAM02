package DB2025Team02DAO;

import DB2025Team02DTO.RefundInfoDTO;
import DB2025Team02DTO.DepositsDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DB2025Team02main.AppMain;
/**
 * Refundinfo 화면에서 사용되는 DAO 클래스입니다. JDBC를 사용한 기능을 제공합니다.
 */
public class RefundDAO {


    /**사용자의 환급 정보를 가져오는 메서드입니다*/
    public List<RefundInfoDTO> getRefundInfoList(int userId) {
        String sql = "SELECT d.amount, d.deposit_date, d.study_id, sg.name AS study_name " +
                "FROM db2025team02Deposits d " +
                "JOIN db2025team02StudyGroups sg ON d.study_id = sg.study_id " +
                "WHERE d.user_id = ? AND d.is_refunded = TRUE";

        List<RefundInfoDTO> result = new ArrayList<>();

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String studyName = rs.getString("study_name");
                    int depositAmount = rs.getInt("amount");
                    int refundAmount = depositAmount;  // 전액 환급 정책 가정
                    String refundDate = rs.getDate("deposit_date").toString();

                    RefundInfoDTO dto = new RefundInfoDTO(
                            studyName,
                            depositAmount,
                            refundAmount,
                            refundDate
                    );

                    result.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}

