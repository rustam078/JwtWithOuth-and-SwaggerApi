package com.abc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401 Status
public class InvalidJwtTokenException extends InvalidCredentialsException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
