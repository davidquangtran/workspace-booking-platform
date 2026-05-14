package com.workspace.auth.domain.exception;

public class InvalidRefreshTokenException extends DomainException {

    public InvalidRefreshTokenException(String reason) {
        super("Invalid refresh token: " + reason);
    }
}