package DB2025Team02DTO;

import java.sql.Date;

/**
 * 스터디 보증금 입금 내역을 나타내는 DTO 클래스입니다.
 */
public class DepositsDTO {
    private int userId;
    private int studyId;
    private int amount;
    private Date depositDate;

    public DepositsDTO(int userId, int studyId, int amount, Date depositDate) {
        this.userId = userId;
        this.studyId = studyId;
        this.amount = amount;
        this.depositDate = depositDate;
    }

     public int getUserId() {
        return userId;
    }

    public int getStudyId() {
        return studyId;
    }

    public int getAmount() {
        return amount;
    }

    public Date getDepositDate() {
        return depositDate;
    }

}
