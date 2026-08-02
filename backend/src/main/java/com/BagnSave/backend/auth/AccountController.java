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
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public AccountDTO getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Account account;
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            account = customUserDetails.getAccount();
        } else {
            OAuth2User oauth2User = (OAuth2User) principal;
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String providerId = oauth2User.getAttribute("sub");
            account = accountService.findOrCreateAccount(email, name, providerId, AuthProvider.GOOGLE);
        }

        return new AccountDTO(account.getId(), account.getEmail(), account.getName());
    }
}
