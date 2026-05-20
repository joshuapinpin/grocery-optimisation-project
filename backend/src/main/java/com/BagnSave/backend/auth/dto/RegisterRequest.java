package com.BagnSave.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
        @NotBlank String username,
        @NotBlank String password) {
    public RegisterRequest{
        username = username != null ? username.trim() : null;
        password = password != null ? password.trim() : null;
    }
}
