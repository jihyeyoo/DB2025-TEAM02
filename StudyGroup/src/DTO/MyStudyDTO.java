package DTO;

import java.sql.Date;

public class MyStudyDTO {
    private int studyId;
    private String studyName;
    private String leaderName;
    private Date startDate;  // java.sql.Date로 변경
    private String status;

    public MyStudyDTO(int studyId, String studyName, String leaderName, Date startDate, String status) {
        this.studyId = studyId;
        this.studyName = studyName;
        this.leaderName = leaderName;
        this.startDate = startDate;
        this.status = status;
    }

    public int getStudyId() { return studyId; }
    public String getStudyName() { return studyName; }
    public String getLeaderName() { return leaderName; }
    public Date getStartDate() { return startDate; }  // getter도 Date로 유지
}
