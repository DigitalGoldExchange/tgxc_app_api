package com.dgex.backend.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginException extends AuthenticationException {
    private Integer errorType;

    public LoginException(String message,Integer errorType){
        super(message);
        this.errorType = errorType;
    }

    public Integer getErrorType() {
        return errorType;
    }

    public void setErrorType(Integer errorType) {
        this.errorType = errorType;
    }
}
