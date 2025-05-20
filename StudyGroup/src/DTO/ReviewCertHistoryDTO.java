package DTO;

public class ReviewCertHistoryDTO {
    private String certDate;
    private String content;
    private boolean isApproved;

    public ReviewCertHistoryDTO(String certDate, String content, boolean isApproved) {
        this.certDate = certDate;
        this.content = content;
        this.isApproved = isApproved;
    }

    public String getCertDate() {
        return certDate;
    }

    public String getContent() {
        return content;
    }

    public boolean isApproved() {
        return isApproved;
    }
}
