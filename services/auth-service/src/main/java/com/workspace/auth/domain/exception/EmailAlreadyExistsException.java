package com.workspace.auth.entities.exception;

import com.workspace.auth.entities.Email;

public class EmailAlreadyExistsException extends DomainException {

    private final Email email;

    public EmailAlreadyExistsException(Email email) {
        super("Email already exists: " + email.value());
        this.email = email;
    }

    public Email getEmail() {
        return email;
    }
}