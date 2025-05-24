package DB2025Team02DTO;

public class MyStudyDetailDTO {
    private int studyId;
    private String studyName;
    private int memberCount;
    private int totalFine;

    public MyStudyDetailDTO(int studyId, String studyName, int memberCount, int totalFine) {
        this.studyId = studyId;
        this.studyName = studyName;
        this.memberCount = memberCount;
        this.totalFine = totalFine;
    }


    public int getStudyId() {
        return studyId;
    }

    public String getStudyName() {
        return studyName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public int getTotalFine() {
        return totalFine;
    }
}
