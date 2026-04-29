package com.workspace.auth.usecases.register;

import java.time.LocalDateTime;
import java.util.UUID;

public record RegisterUserOutput(
        UUID userId,
        String email,
        String role,
        String status,
        LocalDateTime createdAt
) {
}