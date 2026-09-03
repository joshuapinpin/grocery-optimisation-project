package com.BagnSave.backend.shared.config;

import com.BagnSave.backend.auth.AccountService;
import com.BagnSave.backend.auth.AuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AccountService accountService;

    @Value("${app.frontend-url:http://localhost:5171}")
    private String frontendUrl;

    public OAuth2SuccessHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        
        accountService.findOrCreateAccount(email, name, providerId, AuthProvider.GOOGLE);

        setDefaultTargetUrl(frontendUrl + "/");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
