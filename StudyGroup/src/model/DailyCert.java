package model;

import java.util.Date;

public class DailyCert {
    private int certId;
    private int userId;
    private int studyId;
    private Date certDate;
    private String content;
    private boolean isApproved;

    public DailyCert(int certId, int userId, int studyId, Date certDate, String content, boolean isApproved) {
        this.certId = certId;
        this.userId = userId;
        this.studyId = studyId;
        this.certDate = certDate;
        this.content = content;
        this.isApproved = isApproved;
    }

    // Getter & Setter
}
