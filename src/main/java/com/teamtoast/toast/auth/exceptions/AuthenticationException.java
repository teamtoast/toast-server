package com.teamtoast.toast.auth.exceptions;

public class AuthenticationException extends Exception {

    @Override
    public String getMessage() {
        return "Could not authenticate.";
    }
}
