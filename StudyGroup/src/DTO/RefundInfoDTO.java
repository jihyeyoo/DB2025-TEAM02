package DTO;

public class RefundInfoDTO {
    private String studyName;
    private int deposit;
    private int attendanceRate;
    private int refundAmount;
    private String refundDate;

    public RefundInfoDTO(String studyName, int deposit, int attendanceRate, int refundAmount, String refundDate) {
        this.studyName = studyName;
        this.deposit = deposit;
        this.attendanceRate = attendanceRate;
        this.refundAmount = refundAmount;
        this.refundDate = refundDate;
    }

    public String getStudyName() {
        return studyName;
    }

    public int getDeposit() {
        return deposit;
    }

    public int getAttendanceRate() {
        return attendanceRate;
    }

    public int getRefundAmount() {
        return refundAmount;
    }

    public String getRefundDate() {
        return refundDate;
    }
}