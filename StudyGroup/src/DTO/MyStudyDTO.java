package DTO;

import java.sql.Date;

public class MyStudyDTO {
    private int studyId;
    private String studyName;
    private String leaderName;
    private Date startDate;
    private int leaderId;     // 개설자 ID
    private String status;    // 스터디 상태 (진행중, 모집중, 종료 등)

    
    public MyStudyDTO(int studyId, String studyName, String leaderName, Date startDate, int leaderId, String status) {
        this.studyId = studyId;
        this.studyName = studyName;
        this.leaderName = leaderName;
        this.startDate = startDate;
        this.leaderId = leaderId;
        this.status = status;
    }

    // Getter
    public int getStudyId() { return studyId; }
    public String getStudyName() { return studyName; }
    public String getLeaderName() { return leaderName; }
    public Date getStartDate() { return startDate; }
    public int getLeaderId() { return leaderId; }
    public String getStatus() { return status; }
}
