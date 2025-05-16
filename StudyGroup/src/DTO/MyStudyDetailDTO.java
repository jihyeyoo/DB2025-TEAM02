package DTO;
import java.util.Date;

public class MyStudyDetailDTO {
    private String studyName;
    private String leaderName;
    private int memberCount;
    private int totalFine;
    private Date lastModified;

    public MyStudyDetailDTO(String studyName, String leaderName, int memberCount, int totalFine, Date lastModified) {
        this.studyName = studyName;
        this.leaderName = leaderName;
        this.memberCount = memberCount;
        this.totalFine = totalFine;
        this.lastModified = lastModified;
    }

    // Getter methods
    public String getStudyName() { return studyName; }
    public String getLeaderName() { return leaderName; }
    public int getMemberCount() { return memberCount; }
    public int getTotalFine() { return totalFine; }
    public Date getLastModified() { return lastModified; }
}
