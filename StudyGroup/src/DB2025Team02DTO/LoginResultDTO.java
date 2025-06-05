package DB2025Team02DTO;
/**
 * 로그인 상태를 나타내는 DTO클래스입니다. 로그인 결과를 SUCCESS, INVAILD_PASSWORD, ID_NOT_FOUND, ERROR 4가지 상태로 구분합니다.
 */
public class LoginResultDTO {

    public enum LoginStatus {
        SUCCESS,
        INVALID_PASSWORD,
        ID_NOT_FOUND,
        ERROR
    }

    private LoginStatus status;
    private UserDTO user;

    public LoginResultDTO(LoginStatus status, UserDTO user) {
        this.status = status;
        this.user = user;
    }

    public LoginStatus getStatus() {
        return status;
    }

    public UserDTO getUser() {
        return user;
    }
}

