package com.BagnSave.backend.auth;

import com.BagnSave.backend.auth.exception.AccountAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Account register(String email, String rawPassword, String displayName) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        String normalizedName = displayName == null ? null : displayName.trim();

        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (normalizedName == null || normalizedName.isBlank()) {
            throw new IllegalArgumentException("Display name is required");
        }

        // Ensure email is unique
        if(accountRepository.existsByEmail(normalizedEmail)) {
            throw new AccountAlreadyExistsException();
        }

        // Create and save a new account
        Account account = new Account();
        account.setEmail(normalizedEmail);
        account.setName(normalizedName);
        account.setHashedPassword(passwordEncoder.encode(rawPassword));
        account.setAuthProvider(AuthProvider.LOCAL);

        // Save and handle possible race-condition uniqueness violation
        try{
            return accountRepository.save(account);
        } catch(DataIntegrityViolationException e){
            throw new AccountAlreadyExistsException();
        }

    }
}
