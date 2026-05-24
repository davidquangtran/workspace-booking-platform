package com.workspace.auth.application.dto.response;

import com.workspace.auth.domain.entity.Role;

public record UserResponse(
        String email,
        Role role
) {
    public static UserResponse of(String email, Role role) {
        return new UserResponse(email, role);
    }
}
