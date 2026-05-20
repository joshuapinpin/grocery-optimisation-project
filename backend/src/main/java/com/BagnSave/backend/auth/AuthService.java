package com.BagnSave.backend.auth;

import com.BagnSave.backend.auth.dto.AuthResponse;
import com.BagnSave.backend.auth.dto.LoginRequest;
import com.BagnSave.backend.auth.dto.RegisterRequest;

public interface AuthService {
    /**
     * Registers a new account with the provided username and password.
     * Validates that the username and password are not null and that the username is unique.
     * @param request The registration request containing the username and password.
     * @return An AuthResponse containing the user's ID, username, and a success message.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a login with the provided username and password.
     *
     * @param request The login request containing the username and password.
     * @return An AuthResponse containing the user's ID, username, and a success message if authentication is successful.
     */
    AuthResponse login(LoginRequest request);
}
