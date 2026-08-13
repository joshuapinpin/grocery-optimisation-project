package com.BagnSave.backend.shoppinglist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Item not found")
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() { super("Item not found"); }
    public ItemNotFoundException(String message) { super(message); }
}