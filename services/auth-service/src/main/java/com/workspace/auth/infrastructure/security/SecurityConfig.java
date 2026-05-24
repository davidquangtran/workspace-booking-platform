package com.workspace.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Tắt CSRF — REST API không cần
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless — không dùng session, dùng JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Phân quyền endpoint
                .authorizeHttpRequests(auth -> auth
                        // Cho phép không cần token
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/user/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()
                        // Tất cả endpoint khác phải có token
                        .anyRequest().authenticated()
                )
                .build();
    }
}