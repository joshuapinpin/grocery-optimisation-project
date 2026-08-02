package com.BagnSave.backend.auth;

public interface AccountService {
    Account findOrCreateAccount(String email, String name, String oauthProviderId, AuthProvider oauthProvider);
}