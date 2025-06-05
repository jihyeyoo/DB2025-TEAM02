package DB2025Team02DAO;

import java.sql.*;
import DB2025Team02main.AppMain;
import DB2025Team02util.PasswordHasher;
/**
 * SignUp 화면에서 사용되는 DAO 클래스입니다. JDBC를 사용한 기능을 제공합니다.
 */
public class SignUpDAO {
	

    /**회원가입 할 때 이미 DB에 동일한 id가 있는지 검사하고, 만약 동일한 id가 있는 경우는 회원가입을 막습니다*/
	public boolean isDuplicateLoginId(String loginId) {
        String sql = "SELECT 1 FROM db2025team02Users WHERE login_id = ?";

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

    /** loginId, userName, password 정보를 받아 회원가입시키는 메서드입니다. users 테이블에 정보가 insert됩니다.*/
    public boolean registerUser(String loginId, String userName, String password) {
        String sql = "INSERT INTO db2025team02Users (login_id, user_name, password_hash) VALUES (?, ?, ?)";
        
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
