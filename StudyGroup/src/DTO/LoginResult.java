package DTO;

import model.LoginStatus;
import model.User;

public class LoginResult {
    private LoginStatus status;
    private User user;

    public LoginResult(LoginStatus status, User user) {
        this.status = status;
        this.user = user;
    }

    public LoginStatus getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }
}

