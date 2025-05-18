package DAO;

import java.sql.*;
import main.AppMain;
import util.PasswordHasher;

public class SignUpDAO {
	

	public boolean isDuplicateLoginId(String loginId) {
        String sql = "SELECT 1 FROM Users WHERE login_id = ?";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setString(1, loginId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean registerUser(String loginId, String userName, String password) {
        String sql = "INSERT INTO Users (login_id, user_name, password_hash) VALUES (?, ?, ?)";
        
        String hashedPassword = PasswordHasher.hashPassword(password); // 해시 처리

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setString(1, loginId);
            stmt.setString(2, userName);
            stmt.setString(3, hashedPassword);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
