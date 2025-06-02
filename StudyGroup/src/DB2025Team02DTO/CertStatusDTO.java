package DB2025Team02DTO;

/**
  스터디 인증 상태를 조회하는 화면에서 각 스터디의 인증 마감일과 제출 상태를 나타내는 DTO입니다.
 */
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

