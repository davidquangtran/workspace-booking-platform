package com.workspace.auth.usecases.refresh;

public record RefreshAccessTokenInput(
        String refreshToken
) {
}