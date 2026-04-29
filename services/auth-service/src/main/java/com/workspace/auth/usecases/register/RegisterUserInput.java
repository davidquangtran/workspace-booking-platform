package com.workspace.auth.usecases.register;

public record RegisterUserInput(
        String email,
        String rawPassword
) {
}