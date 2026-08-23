package com.BagnSave.backend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Invalid credentials")
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {super("Invalid credentials");}
    public InvalidCredentialsException(String message) {super(message);}
    public InvalidCredentialsException(String message, Throwable cause) {super(message, cause);}
}
