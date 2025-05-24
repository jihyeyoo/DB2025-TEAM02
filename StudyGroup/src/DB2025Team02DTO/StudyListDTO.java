package DB2025Team02DTO;

/**
 * StudyList 화면에서 필요한 스터디 기본 정보를 담는 DTO 클래스
 */
public class StudyListDTO {
    private int studyId;
    private String name;
    private String startDate;
    private String endDate;
    private String certMethod;
    private int deposit;
    private String status;

    public StudyListDTO(int studyId, String name, String startDate,
                        String endDate, String certMethod, int deposit, String status) {
        this.studyId = studyId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certMethod = certMethod;
        this.deposit = deposit;
        this.status = status;
    }

    public int getStudyId() { return studyId; }
    public String getName() { return name; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getCertMethod() { return certMethod; }
    public int getDeposit() { return deposit; }
    public String getStatus() { return status; }
}
