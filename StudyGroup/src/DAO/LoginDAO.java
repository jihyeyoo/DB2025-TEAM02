package DAO;

import java.sql.*;

import main.AppMain;

public class LoginDAO {

    public boolean checkLogin(String loginId, String password) {
        String sql = "SELECT * FROM Users WHERE login_id = ? AND password_hash = ?";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setString(1, loginId);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // 있으면 로그인 성공

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // 예외 시 실패
    }
}
