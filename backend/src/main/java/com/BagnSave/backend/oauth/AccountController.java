package com.BagnSave.backend.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public AccountDTO getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        
        Account account = accountService.findOrCreateAccount(email, name, providerId, "google");
        
        return new AccountDTO(account.getId(), account.getEmail(), account.getName());
    }
}
