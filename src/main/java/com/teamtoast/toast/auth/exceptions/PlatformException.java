package com.teamtoast.toast.auth.exceptions;

public class PlatformException extends Exception {

    @Override
    public String getMessage() {
        return "Could not connect to platform server.";
    }
}
