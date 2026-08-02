package com.BagnSave.backend.auth;

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

        Account account = accountRepository.findByEmail(normalizedEmail)
                .orElseGet(Account::new);

        account.setEmail(normalizedEmail);
        account.setName(normalizedName);
        account.setHashedPassword(passwordEncoder.encode(rawPassword));
        if (account.getAuthProvider() == null) {
            account.setAuthProvider(AuthProvider.LOCAL);
        }

        return accountRepository.save(account);
    }
}
