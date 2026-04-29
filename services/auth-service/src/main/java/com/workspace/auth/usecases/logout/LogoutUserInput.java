package com.workspace.auth.usecases.logout;

public record LogoutUserInput(
        String refreshToken
) {
}