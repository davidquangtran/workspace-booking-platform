package com.workspace.auth.entities.exception;

public class InvalidRefreshTokenException extends DomainException {

    public InvalidRefreshTokenException(String reason) {
        super("Invalid refresh token: " + reason);
    }
}