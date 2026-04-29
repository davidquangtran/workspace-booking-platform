package com.workspace.auth.entities.exception;

public class InvalidEmailException extends DomainException {

    private final String invalidValue;

    public InvalidEmailException(String invalidValue) {
        super("Invalid email format: " + invalidValue);
        this.invalidValue = invalidValue;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}