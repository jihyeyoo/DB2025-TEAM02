package DB2025Team02DAO;

import java.sql.*;
import DB2025Team02main.AppMain;
import DB2025Team02util.PasswordHasher;
import DB2025Team02DTO.LoginResultDTO;
import DB2025Team02DTO.LoginResultDTO.LoginStatus;
import DB2025Team02DTO.UserDTO;

public class LoginDAO {
	
	/* 회원가입한 사용자가 로그인을 하는 메서드입니다. login_id에 따라 db2025team02Users 테이블에서 password_hash를 조회하고, LoginResultDTO에 지정된 LoginStatus로 결과를 반환합니다.
	결과에는 1. id에 따라 조회했을 때 db에 저장된 비밀번호 = 입력한 비밀번호 -> SUCCESS
	 2. id에 따라 조회했을 때 db에 저장된 비밀번호 != 입력한 비밀번호 -> INVAILD_PASSOWRD
	 3. db에 해당하는 loginId가 저장되어 있지 않은 경우 -> ID_NOT_FOUND
	  세 가지 경우가 있습니다. */
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
