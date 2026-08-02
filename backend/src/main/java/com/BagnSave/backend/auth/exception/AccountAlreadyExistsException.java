package com.BagnSave.backend.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Account already exists")
public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException() {super("Account already exists");}
    public AccountAlreadyExistsException(String message) {super(message);}
    public AccountAlreadyExistsException(String message, Throwable cause) {super(message, cause);}
}
