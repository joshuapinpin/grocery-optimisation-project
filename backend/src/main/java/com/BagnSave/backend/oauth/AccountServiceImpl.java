package com.BagnSave.backend.oauth;

import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account findOrCreateAccount(String email, String name, String oauthProviderId, String oauthProvider) {
        return accountRepository.findByOauthProviderId(oauthProviderId)
                .orElseGet(() -> {
                    Account newAccount = new Account();
                    newAccount.setEmail(email);
                    newAccount.setName(name);
                    newAccount.setOauthProviderId(oauthProviderId);
                    newAccount.setOauthProvider(oauthProvider);
                    return accountRepository.save(newAccount);
                });
    }
}
