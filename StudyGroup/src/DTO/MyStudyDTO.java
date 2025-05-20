package DTO;

import java.sql.Date;

public class MyStudyDTO {
    private int studyId;
    private String studyName;
    private String leaderName;
    private Date startDate;

    // ✅ 추가: 리더 ID (개설자인지 판단용)
    private int leaderId;

    // ✅ 생성자 수정: leaderId까지 포함
    public MyStudyDTO(int studyId, String studyName, String leaderName, Date startDate, int leaderId) {
        this.studyId = studyId;
        this.studyName = studyName;
        this.leaderName = leaderName;
        this.startDate = startDate;
        this.leaderId = leaderId;
    }

    // 기존 getter
    public int getStudyId() { return studyId; }
    public String getStudyName() { return studyName; }
    public String getLeaderName() { return leaderName; }
    public Date getStartDate() { return startDate; }

    // ✅ 추가된 getter
    public int getLeaderId() { return leaderId; }
}
