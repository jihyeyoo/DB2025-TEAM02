package DB2025Team02DTO;

import java.util.Date;
/**
 * 사용자의 개별 인증 기록을 나타내는 DTO 클래스입니다.
 */
public class DailyCertsDTO {
    private int certId;
    private int userId;
    private int studyId;
    private Date certDate;
    private String content;
    private String approvalStatus;

    public DailyCertsDTO(int certId, int userId, int studyId, Date certDate, String content, String approvalStatus) {
        this.certId = certId;
        this.userId = userId;
        this.studyId = studyId;
        this.certDate = certDate;
        this.content = content;
        this.approvalStatus = approvalStatus;
    }

    // Getter Methods
    public int getCertId() {
        return certId;
    }

    public int getUserId() {
        return userId;
    }

    public int getStudyId() {
        return studyId;
    }

    public Date getCertDate() {
        return certDate;
    }

    public String getContent() {
        return content;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

}
