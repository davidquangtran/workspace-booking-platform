package com.workspace.auth.entities.exception;

public class WeakPasswordException extends DomainException {

    public WeakPasswordException(String reason) {
        super("Password is too weak: " + reason);
    }
}