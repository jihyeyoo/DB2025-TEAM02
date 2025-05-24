package DB2025Team02DTO;

public class CreateStudyDTO {
    private String name;
    private int leaderId;
    private String description;
    private String startDate;
    private String endDate;
    private String certMethod;
    private int deposit;
    private RuleDTO rule;

    public CreateStudyDTO(String name, int leaderId, String description,
                          String startDate, String endDate, String certMethod, int deposit, RuleDTO rule) {
        this.name = name;
        this.leaderId = leaderId;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certMethod = certMethod;
        this.deposit = deposit;
        this.rule = rule;
    }

    public String getName() { return name; }
    public int getLeaderId() { return leaderId; }
    public String getDescription() { return description; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getCertMethod() { return certMethod; }
    public int getDeposit() { return deposit; }
    public RuleDTO getRule() { return rule; }
}
