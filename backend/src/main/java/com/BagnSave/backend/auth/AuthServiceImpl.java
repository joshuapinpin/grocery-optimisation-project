package com.BagnSave.backend.auth;

import com.BagnSave.backend.auth.dto.AuthResponse;
import com.BagnSave.backend.auth.dto.LoginRequest;
import com.BagnSave.backend.auth.dto.RegisterRequest;
import com.BagnSave.backend.auth.exception.InvalidCredentialsException;
import com.BagnSave.backend.auth.exception.UsernameAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        String password = request.password();

        // Ensure username is unique
        if(accountRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Username '" + username + "' already exists");
        }

        // Create and save a new account
        Account account = new Account();
        account.username(username);
        account.hashedPassword(passwordEncoder.encode(password));

        // Save and handle possible race-condition uniqueness violation.
        try{
            Account saved = accountRepository.save(account);
            return new AuthResponse(saved.id(), saved.username(), "Account registration successful");
        } catch(DataIntegrityViolationException e) {
            throw new UsernameAlreadyExistsException("Username '" + request.username() + "' already exists");
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Validate input is present
        if (request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        // Find an account by username and validate password
        Account account = accountRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        // Validate username to password mapping
        if (!passwordEncoder.matches(request.password(), account.hashedPassword())) {
            throw new InvalidCredentialsException();
        }

        return new AuthResponse(account.id(), account.username(), "Login successful");}
}
