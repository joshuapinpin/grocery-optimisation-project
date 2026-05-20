package com.BagnSave.backend.auth.dto;

public record RegisterRequest(String username, String password) {
    public RegisterRequest{
        username = username != null ? username.trim() : null;
        password = password != null ? password.trim() : null;
    }
}
