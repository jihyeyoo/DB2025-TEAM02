package DTO;

import java.sql.Date;

/**
 * StudyGroups 테이블에 INSERT할 데이터를 담는 DTO 클래스
 */
public class DepositsDTO {
    private int depositId;
    private int userId;
    private int studyId;
    private int amount;
    private Date depositDate;
    private boolean isRefunded;

    public DepositsDTO(int depositId, int userId, int studyId, int amount, Date depositDate, boolean isRefunded) {
        this.depositId = depositId;
        this.userId = userId;
        this.studyId = studyId;
        this.amount = amount;
        this.depositDate = depositDate;
        this.isRefunded = isRefunded;
    }

    // Getter/Setter 추가 (필요 시)
}
