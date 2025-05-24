package DB2025Team02DTO;

public class StudyDetailDTO {
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private String certMethod;
    private int deposit;
    private RuleDTO rule;
    private String status;

    public StudyDetailDTO(String name, String description, String startDate,
                          String endDate, String certMethod, int deposit,   String status, RuleDTO rule) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certMethod = certMethod;
        this.deposit = deposit;
        this.rule = rule;
        this.status = status;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getCertMethod() { return certMethod; }
    public int getDeposit() { return deposit; }
    public String getStatus() { return status; }
}
