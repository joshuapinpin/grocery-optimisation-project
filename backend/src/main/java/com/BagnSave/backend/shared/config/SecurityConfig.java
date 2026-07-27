package com.BagnSave.backend.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final OAuth2SuccessHandler oauth2SuccessHandler;

    public SecurityConfig(OAuth2SuccessHandler oauth2SuccessHandler) {
        this.oauth2SuccessHandler = oauth2SuccessHandler;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/login", "/oauth2/**").permitAll()
                    .requestMatchers("/api/user").authenticated()
                    .requestMatchers("/api/**").permitAll()
                    .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                    .successHandler(oauth2SuccessHandler)
            )
            .logout(logout -> logout
                    .logoutSuccessUrl("/")
            );
        return http.build();
    }
}
