package DB2025Team02DTO;

import java.sql.Date;

public class StudyEditDTO {
    private int studyId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String certMethod;
    private int settlementCycle;
    private int leaderId;

    public StudyEditDTO(int studyId, String name, String description, Date startDate, Date endDate,
                    String certMethod, int settlementCycle, int leaderId) {
        this.studyId = studyId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certMethod = certMethod;
        this.settlementCycle = settlementCycle;
        this.leaderId = leaderId;
    }

    public StudyEditDTO() {}

    public int getStudyId() { return studyId; }
    public void setStudyId(int studyId) { this.studyId = studyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getCertMethod() { return certMethod; }
    public void setCertMethod(String certMethod) { this.certMethod = certMethod; }

    public int getSettlementCycle() { return settlementCycle; }
    public void setSettlementCycle(int settlementCycle) { this.settlementCycle = settlementCycle; }

}
