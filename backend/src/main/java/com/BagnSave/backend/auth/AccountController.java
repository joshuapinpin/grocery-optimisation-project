package com.BagnSave.backend.auth;

import com.BagnSave.backend.auth.dto.AccountDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BagnSave.backend.auth.userdetailsservice.CustomUserDetails;

@RestController
@RequestMapping("/api/user")
public class AccountController {

    private final AuthenticatedAccountResolver authenticatedAccountResolver;

    public AccountController(AuthenticatedAccountResolver authenticatedAccountResolver) {
        this.authenticatedAccountResolver = authenticatedAccountResolver;
    }

    @GetMapping
    public AccountDTO getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Account account = authenticatedAccountResolver.resolve(authentication);
        return new AccountDTO(account.getId(), account.getEmail(), account.getName(), account.getAuthProvider());
    }
}
