package model;

public class GroupMember {
    private int studyId;
    private int userId;
    private int accumulatedFine;
    private String status;

    public GroupMember(int studyId, int userId, int accumulatedFine, String status) {
        this.studyId = studyId;
        this.userId = userId;
        this.accumulatedFine = accumulatedFine;
        this.status = status;
    }

    // Getter & Setter
}
