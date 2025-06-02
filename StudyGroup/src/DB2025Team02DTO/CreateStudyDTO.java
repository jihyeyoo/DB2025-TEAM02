package DB2025Team02DTO;
/**
 * 스터디 생성 시 입력되는 데이터를 담는 DTO 클래스입니다.
 * 스터디 기본 정보와 해당 스터디의 규칙 정보(RuleDTO)를 포함합니다.
 * 스터디 생성 화면에서 사용자의 입력값을 전달하는 데 사용됩니다.
 */
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
