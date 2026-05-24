package com.workspace.auth.domain.exception;

public class InvalidEmailException extends RuntimeException  {

    public InvalidEmailException(String message) {
        super(message);
    }
}