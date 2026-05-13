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

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new account with the provided username and password.
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
        if(accountRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        // Create and save new account
        Account account = new Account();
        account.username(request.username().trim());
        account.hashedPassword(passwordEncoder.encode(request.password()));

        Account saved = accountRepository.save(account);
        return new AuthResponse(saved.id(), saved.username(), "Account registration successful");
    }

    /**
     * Authenticates a login with the provided username and password.
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

        // Find account by username and validate password
        Account account = accountRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), account.hashedPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        return new AuthResponse(account.id(), account.username(), "Login successful");}
}
