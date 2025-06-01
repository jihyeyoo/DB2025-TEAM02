package DB2025Team02DTO;

public class CertStatusDTO {
    private String deadlineStr;
    private String submitStatus;

    public CertStatusDTO(String deadlineStr, String submitStatus) {
        this.deadlineStr = deadlineStr;
        this.submitStatus = submitStatus;
    }

    public String getDeadlineStr() {
        return deadlineStr;
    }

    public String getSubmitStatus() {
        return submitStatus;
    }
}

