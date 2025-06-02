package DB2025Team02DTO;

import java.sql.Date;
import java.sql.Time;
/**
 * 사용자가 참여 중인 스터디 정보를 담는 DTO 클래스입니다.
 */
public class MyStudyDTO {
    private int studyId;
    private String studyName;
    private String leaderName;
    private Date startDate;
    private int leaderId;     // 개설자 ID
    private String status;    // 스터디 상태 (진행중, 모집중, 종료 등)
    private Date nextCertDate;

    
    public MyStudyDTO(int studyId, String studyName, String leaderName, Date startDate, int leaderId, String status, Date nextCertDate) {
        this.studyId = studyId;
        this.studyName = studyName;
        this.leaderName = leaderName;
        this.startDate = startDate;
        this.leaderId = leaderId;
        this.status = status;
        this.nextCertDate = nextCertDate;
    }

    public Date getNextCertDate() {
        return nextCertDate;
    }
    public int getStudyId() { return studyId; }
    public String getStudyName() { return studyName; }
    public String getLeaderName() { return leaderName; }
    public Date getStartDate() { return startDate; }
    public int getLeaderId() { return leaderId; }
    public String getStatus() { return status; }
    public void setNextCertDate(Date nextCertDate) {
        this.nextCertDate = nextCertDate;
    }
}
