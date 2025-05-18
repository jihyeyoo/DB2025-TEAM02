package DAO;

import java.sql.*;
import main.AppMain;
import util.PasswordHasher;
import DTO.LoginResult;
import model.LoginStatus;
import model.User;

public class LoginDAO {

    public LoginResult login(String loginId, String password) {
        String sql = "SELECT * FROM Users WHERE login_id = ?";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setString(1, loginId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = PasswordHasher.hashPassword(password);

                    if (storedHash != null && storedHash.equals(inputHash)) {
                        // User 정보 조회 및 객체 생성
                        User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("login_id"),
                            rs.getString("user_name"),
                            rs.getString("password_hash"),
                            rs.getInt("points")
                        );
                        return new LoginResult(LoginStatus.SUCCESS, user);
                    } else {
                        return new LoginResult(LoginStatus.INVALID_PASSWORD, null);
                    }
                } else {
                    return new LoginResult(LoginStatus.ID_NOT_FOUND, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(LoginStatus.ERROR, null);
        }
    }
}
