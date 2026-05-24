package com.workspace.auth.domain.exception;

public class WeakPasswordException extends DomainException {

    public WeakPasswordException(String reason) {
        super("Password is too weak: " + reason);
    }
}