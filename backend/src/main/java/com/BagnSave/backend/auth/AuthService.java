package com.BagnSave.backend.auth;

import com.BagnSave.backend.auth.dto.AuthResponse;
import com.BagnSave.backend.auth.dto.LoginRequest;
import com.BagnSave.backend.auth.dto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the provided username and password.
     * Validates that the username and password are not null and that the username is unique.
     * @param request The registration request containing the username and password.
     * @return An AuthResponse containing the user's ID, username, and a success message.
     */
    public AuthResponse register(RegisterRequest request) {
        // Validate input is present
        if (request.username() == null || request.username().isEmpty()
                || request.password() == null || request.password().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        // Ensure username is unique
        if(userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // Create and save new user
        User user = new User();
        user.username(request.username().trim());
        user.hashedPassword(passwordEncoder.encode(request.password()));

        User saved = userRepository.save(user);
        return new AuthResponse(saved.id(), saved.username(), "User registration successful");
    }

    /**
     * Authenticates a user with the provided username and password.
     *
     * @param request The login request containing the username and password.
     * @return An AuthResponse containing the user's ID, username, and a success message if authentication is successful.
     */
    public AuthResponse login(LoginRequest request) {
        // Validate input is present
        if (request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        // Find user by username and validate password
        User user = userRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.hashedPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return new AuthResponse(user.id(), user.username(), "Login successful");}
}
