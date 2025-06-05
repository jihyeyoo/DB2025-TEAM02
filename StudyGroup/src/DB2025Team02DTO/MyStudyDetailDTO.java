package DB2025Team02DTO;

/**
 * 마이 스터디 상세 화면에서 사용되는 DTO 클래스입니다.
 * 사용자가 참여 중인 스터디에 대해 스터디 이름, 총 인원 수, 누적 벌금 총합 등을 제공합니다.
 */
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
