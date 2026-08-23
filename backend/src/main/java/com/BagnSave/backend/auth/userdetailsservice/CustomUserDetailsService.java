package com.BagnSave.backend.auth.userdetailsservice;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.auth.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository repository;

    public CustomUserDetailsService(AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        Account account = repository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (account.getHashedPassword() == null || account.getHashedPassword().isBlank()) {
            throw new UsernameNotFoundException("This user uses OAuth2 login. Please use the appropriate login method.");
        }
        return new CustomUserDetails(account);
    }
}
