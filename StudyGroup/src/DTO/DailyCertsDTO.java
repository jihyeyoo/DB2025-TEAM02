package DTO;

import java.util.Date;

public class DailyCertsDTO {
    private int certId;
    private int userId;
    private int studyId;
    private Date certDate;
    private String content;
    private boolean isApproved;

    public DailyCertsDTO(int certId, int userId, int studyId, Date certDate, String content, boolean isApproved) {
        this.certId = certId;
        this.userId = userId;
        this.studyId = studyId;
        this.certDate = certDate;
        this.content = content;
        this.isApproved = isApproved;
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

    public boolean isApproved() {
        return isApproved;
    }

    // Optional: toString() for debugging
    @Override
    public String toString() {
        return "DailyCertsDTO{" +
                "certId=" + certId +
                ", userId=" + userId +
                ", studyId=" + studyId +
                ", certDate=" + certDate +
                ", content='" + content + '\'' +
                ", isApproved=" + isApproved +
                '}';
    }
}
