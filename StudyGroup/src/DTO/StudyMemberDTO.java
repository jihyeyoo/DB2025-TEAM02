package DTO;

public class StudyMemberDTO {
    private String userName;
    private int accumulatedFine;
    private int userId;

    public StudyMemberDTO(String userName, int accumulatedFine, int userId) {
        this.userName = userName;
        this.accumulatedFine = accumulatedFine;
        this.userId = userId;
    }

    // Getter
    public String getUserName() {
        return userName;
    }

    public int getAccumulatedFine() {
        return accumulatedFine;
    }
    public int getUserId() {return userId;}
}
