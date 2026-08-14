package com.BagnSave.backend.auth;

import com.BagnSave.backend.auth.userdetailsservice.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedAccountResolver {

    private final AccountService accountService;

    public AuthenticatedAccountResolver(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Resolve the Account for the currently authenticated principal
     * Handles both local (session/password) logins and Oauth2 (Google) logins
     * Creates an Account on first Oauth2 login if it doesn't exist yet
     * @param authentication the Spring Security Authentication object
     * @return the resolved Account
     */
    public Account resolve(Authentication authentication){
        Object principal = authentication.getPrincipal();

        // Check if the account is from manual authentication (CustomUserDetails)
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getAccount();
        }

        // If not, it must be from OAuth2 authentication (OAuth2User)
        OAuth2User oauth2User = (OAuth2User) principal;
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        return accountService.findOrCreateAccount(email, name, providerId, AuthProvider.GOOGLE);
    }
}
