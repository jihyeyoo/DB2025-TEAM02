package DTO;

public class StudyMemberDTO {
    private String userName;
    private int accumulatedFine;

    public StudyMemberDTO(String userName, int accumulatedFine) {
        this.userName = userName;
        this.accumulatedFine = accumulatedFine;
    }

    // Getter
    public String getUserName() {
        return userName;
    }

    public int getAccumulatedFine() {
        return accumulatedFine;
    }
}
