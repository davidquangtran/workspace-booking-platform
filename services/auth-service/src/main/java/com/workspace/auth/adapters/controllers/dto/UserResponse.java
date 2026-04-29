package com.workspace.auth.adapters.controllers.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String role,
        String status,
        LocalDateTime createdAt
) {
}