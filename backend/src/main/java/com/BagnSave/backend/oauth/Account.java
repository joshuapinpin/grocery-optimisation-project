package com.BagnSave.backend.oauth;

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

    @Column(unique = true)
    private String email;

    private String name;

    @Column(unique = true)
    private String oauthProviderId; // e.g. Google's "sub"

    private String oauthProvider; // e.g. "google", "facebook", etc.
}
