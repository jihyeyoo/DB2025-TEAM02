package DB2025Team02DTO;

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