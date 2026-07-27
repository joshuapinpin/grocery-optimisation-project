package com.BagnSave.backend.oauth;

public interface AccountService {
    Account findOrCreateAccount(String email, String name, String oauthProviderId, String oauthProvider);
}