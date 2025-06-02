package DB2025Team02DTO;

/**
 * 사용자의 보증금 환불 정보를 담는 DTO 클래스입니다.
 * 스터디 종료 후 보증금 반환 내역을 조회할 때 사용됩니다.
 */
public class RefundInfoDTO {
    private String studyName;
    private int deposit;
    private int refundAmount;
    private String refundDate;

    public RefundInfoDTO(String studyName, int deposit,  int refundAmount, String refundDate) {
        this.studyName = studyName;
        this.deposit = deposit;
        this.refundAmount = refundAmount;
        this.refundDate = refundDate;
    }

    public String getStudyName() {
        return studyName;
    }

    public int getDeposit() {
        return deposit;
    }

    public int getRefundAmount() {
        return refundAmount;
    }

    public String getRefundDate() {
        return refundDate;
    }
}