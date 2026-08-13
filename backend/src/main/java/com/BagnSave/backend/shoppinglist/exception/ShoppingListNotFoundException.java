package com.BagnSave.backend.shoppinglist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Shopping list not found")
public class ShoppingListNotFoundException extends RuntimeException {
    public ShoppingListNotFoundException() { super("Shopping list not found"); }
    public ShoppingListNotFoundException(String message) { super(message); }
}