package com.workspace.auth.usecases.login;

public record LoginUserInput(
        String email,
        String rawPassword
) {
}