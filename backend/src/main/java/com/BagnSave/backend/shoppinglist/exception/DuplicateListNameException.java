package com.BagnSave.backend.shoppinglist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "A list with this name already exists")
public class DuplicateListNameException extends RuntimeException {
    public DuplicateListNameException() { super("A list with this name already exists"); }
    public DuplicateListNameException(String message) { super(message); }
}