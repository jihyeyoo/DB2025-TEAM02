package DB2025Team02DTO;

/**
 * 스터디에 참여한 회원의 정보를 담는 DTO 클래스입니다.
 */
public class StudyMemberDTO {
    private String userName;
    private int accumulatedFine;
    private int userId;

    public StudyMemberDTO(String userName, int accumulatedFine, int userId) {
        this.userName = userName;
        this.accumulatedFine = accumulatedFine;
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }

    public int getAccumulatedFine() {
        return accumulatedFine;
    }
    public int getUserId() {return userId;}
}
