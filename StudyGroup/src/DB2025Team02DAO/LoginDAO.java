package DB2025Team02DAO;

import java.sql.*;
import DB2025Team02main.AppMain;
import DB2025Team02util.PasswordHasher;
import DB2025Team02DTO.LoginResultDTO;
import DB2025Team02DTO.LoginResultDTO.LoginStatus;
import DB2025Team02DTO.UserDTO;

public class LoginDAO {
	
	
    public LoginResultDTO login(String loginId, String password) {
        String sql = "SELECT * FROM db2025team02Users WHERE login_id = ?";

        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setString(1, loginId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = PasswordHasher.hashPassword(password);

                    if (storedHash != null && storedHash.equals(inputHash)) {
                        // User 정보 조회 및 객체 생성
                        UserDTO user = new UserDTO(
                        		rs.getInt("user_id"),
                                rs.getString("login_id"),
                                rs.getString("user_name"),
                                rs.getInt("points")
                        );
                        return new LoginResultDTO(LoginStatus.SUCCESS, user);
                    } else {
                        return new LoginResultDTO(LoginStatus.INVALID_PASSWORD, null);
                    }
                } else {
                    return new LoginResultDTO(LoginStatus.ID_NOT_FOUND, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResultDTO(LoginStatus.ERROR, null);
        }
    }
}
