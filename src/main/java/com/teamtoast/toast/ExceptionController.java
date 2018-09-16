package com.teamtoast.toast;

import com.teamtoast.toast.auth.exceptions.AuthenticationException;
import com.teamtoast.toast.auth.exceptions.ConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@ControllerAdvice
@RestController
public class ExceptionController {

    private static final String MSG_SERVER_ERROR = "Internal server error.";

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SQLException.class)
    public String handleSQLException(SQLException e) {
        System.out.println(e);
        return MSG_SERVER_ERROR;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = AuthenticationException.class)
    public String handleAuthenticationException(AuthenticationException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(value = ConflictException.class)
    public String handleConflicException(ConflictException e) {
        return e.getMessage();
    }

}
