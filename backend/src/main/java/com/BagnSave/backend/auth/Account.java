package com.BagnSave.backend.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a user account in the system.
 * This entity is used to store user information obtained from OAuth providers.
 * It does not include sensitive information like passwords; auth is handled by the OAuth provider.
 */
@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // This field is optional and may not be used if the account is created via OAuth
    // However, it cannot be null if the auth provider is LOCAL
    @Column(nullable = true, length = 100)
    private String hashedPassword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider; // e.g., LOCAL, GOOGLE

    @Column(unique = true, nullable = true) // can be null if the account is created locally
    private String authProviderId; // e.g. Google's "sub"

    @Column(nullable = false, length = 100)
    private String name;
}
