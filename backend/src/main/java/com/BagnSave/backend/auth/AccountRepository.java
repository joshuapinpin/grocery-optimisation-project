package com.BagnSave.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByAuthProviderId(String authProviderId);
    boolean existsByEmail(String email);
}
