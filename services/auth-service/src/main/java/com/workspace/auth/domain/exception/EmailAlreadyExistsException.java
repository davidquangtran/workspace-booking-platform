package com.workspace.auth.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException  {

    public EmailAlreadyExistsException(String email) {
        super("The user with email '" + email + "' already exists in the system.");
    }
}