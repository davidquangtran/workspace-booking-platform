package com.workspace.auth.adapters.gateways.security;

import com.workspace.auth.entities.HashedPassword;
import com.workspace.auth.usecases.ports.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder;

    public BCryptPasswordHasher() {
        // Strength 12: cân bằng giữa security và performance.
        // Default Spring là 10. Tăng lên 12 cho production-grade.
        this.encoder = new BCryptPasswordEncoder(12);
    }

    @Override
    public HashedPassword hash(String rawPassword) {
        return new HashedPassword(encoder.encode(rawPassword));
    }

    @Override
    public boolean matches(String rawPassword, HashedPassword hashed) {
        return encoder.matches(rawPassword, hashed.value());
    }
}