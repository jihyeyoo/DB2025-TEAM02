package DB2025Team02util;

import java.security.MessageDigest;

public class PasswordHasher {


    /*해시한 패스워드를 저장하기 위한 메서드입니다.*/
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
