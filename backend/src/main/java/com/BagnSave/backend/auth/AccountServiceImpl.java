package com.BagnSave.backend.auth;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Account findOrCreateAccount(String email, String name, String oauthProviderId, AuthProvider authProvider) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        String normalizedName = name == null ? null : name.trim();

        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (oauthProviderId == null || oauthProviderId.isBlank()) {
            throw new IllegalArgumentException("OAuth provider id is required");
        }
        if (normalizedName == null || normalizedName.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        Account account = accountRepository.findByAuthProviderId(oauthProviderId)
                .orElseGet(() -> accountRepository.findByEmail(normalizedEmail).orElseGet(Account::new));

        // Update the account details with the provided information
        account.setEmail(normalizedEmail);
        account.setName(normalizedName);
        account.setAuthProviderId(oauthProviderId);
        if (account.getAuthProvider() == null || account.getHashedPassword() == null || account.getHashedPassword().isBlank()) {
            account.setAuthProvider(authProvider);
        }

        return accountRepository.save(account);
    }
}
