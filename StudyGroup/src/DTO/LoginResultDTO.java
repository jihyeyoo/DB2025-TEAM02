package DTO;

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

