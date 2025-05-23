package DAO;

import DTO.RefundInfoDTO;
import DTO.DepositsDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import main.AppMain;

public class RefundDAO {

    public List<RefundInfoDTO> getRefundInfoList(int userId) {
        MyPageDAO myPageDAO = new MyPageDAO();
        DailyCertsDAO certDAO = new DailyCertsDAO();

        List<DepositsDTO> deposits = myPageDAO.getRefundedDepositsByUser(userId);
        List<RefundInfoDTO> result = new ArrayList<>();

        for (DepositsDTO d : deposits) {
            String studyName = getStudyNameById(d.getStudyId());
            int approvedCount = certDAO.getApprovedCertCount(userId, d.getStudyId());
            int attendanceRate = approvedCount * 10; // 예: 총 10일 기준
            int refundAmount = d.getAmount();        // 전액 환급 정책 가정
            String refundDate = d.getDepositDate().toString();

            RefundInfoDTO dto = new RefundInfoDTO(
                studyName,
                d.getAmount(),
                attendanceRate,
                refundAmount,
                refundDate
            );
            result.add(dto);
        }

        return result;
    }

    private String getStudyNameById(int studyId) {
        String sql = "SELECT name FROM StudyGroups WHERE study_id = ?";
        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "(알 수 없음)";
    }
}

