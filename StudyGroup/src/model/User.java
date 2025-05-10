package model;

public class User {
    private int userId;
    private String loginId;
    private String userName;
    private String passwordHash;
    private int points;

    public User(int userId, String loginId, String userName, String passwordHash, int points) {
        this.userId = userId;
        this.loginId = loginId;
        this.userName = userName;
        this.passwordHash = passwordHash;
        this.points = points;
    }

    // Getter & Setter
    public int getUserId() { return userId; }
    public String getLoginId() { return loginId; }
    public String getUserName() { return userName; }
    public String getPasswordHash() { return passwordHash; }
    public int getPoints() { return points; }

    public void setPoints(int points) { this.points = points; }
}
