package DB2025Team02DTO;

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
