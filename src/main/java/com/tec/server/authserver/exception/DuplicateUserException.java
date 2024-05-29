package com.tec.server.authserver.exception;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }
}
