package com.teamtoast.toast.auth.bodys;

import com.teamtoast.toast.auth.User;

public class SNSLoginRequest {

    private String token;
    private User.AccountType type;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User.AccountType getType() {
        return type;
    }

    public void setType(User.AccountType type) {
        this.type = type;
    }
}
