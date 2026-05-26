package com.BagnSave.backend.auth;

import com.BagnSave.backend.auth.dto.AuthResponse;
import com.BagnSave.backend.auth.dto.LoginRequest;
import com.BagnSave.backend.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        System.out.println("REGISTER HIT: " + request.username());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        AuthResponse response = authService.login(request);

        var authentication = new UsernamePasswordAuthenticationToken(
                response.username(),
                null,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        session.setAttribute("userId", response.id());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) authService.logout(userId);
        SecurityContextHolder.clearContext();
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        authService.deleteAccount(userId);
        SecurityContextHolder.clearContext();
        session.invalidate();
        return ResponseEntity.noContent().build();
    }
    

    @PostMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("auth controller reachable");
    }
}
