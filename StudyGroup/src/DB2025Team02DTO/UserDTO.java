package DB2025Team02DTO;

/**
 * 사용자 정보를 담는 DTO 클래스입니다.
 * 로그인 후 세션 유지, 마이페이지 정보 표시, 포인트 처리 등에서 사용됩니다.
 */
public class UserDTO {
    private int userId;
    private String loginId;
    private String userName;
    private int points;

    public UserDTO(int userId, String loginId, String userName, int points) {
        this.userId = userId;
        this.loginId = loginId;
        this.userName = userName;
        this.points = points;
    }

    public int getUserId() { return userId; }
    public String getLoginId() { return loginId; }
    public String getUserName() { return userName; }
    public int getPoints() { return points; }
    
    public void setPoints(int points) {
        this.points = points;
    }

}
