package com.BagnSave.backend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Username already exists")
public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException() { super("Username already exists"); }
    public UsernameAlreadyExistsException(String message) { super(message); }
    public UsernameAlreadyExistsException(String message, Throwable cause) { super(message, cause); }
}
