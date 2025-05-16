package model;

import java.util.Date;

public class StudyGroup {
    private int studyId;
    private String name;
    private int leaderId;
    private String description;
    private Date startDate;
    private Date endDate;
    private String certMethod;
    private int deposit;

    public StudyGroup(int studyId, String name, int leaderId, String description,
                      Date startDate, Date endDate, String certMethod, int deposit) {
        this.studyId = studyId;
        this.name = name;
        this.leaderId = leaderId;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certMethod = certMethod;
        this.deposit = deposit;
    }
    
    public int getStudyId() { return studyId; }
    public String getName() { return name; }
    public int getLeaderId() { return leaderId; }
    public String getDescription() { return description; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public String getCertMethod() { return certMethod; }
    public int getDeposit() { return deposit; }
}

