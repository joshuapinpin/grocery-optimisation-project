package com.BagnSave.backend.oauth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByOauthProviderId(String oauthProviderId);
}
