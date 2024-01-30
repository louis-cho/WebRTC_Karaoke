package com.ssafy.server.exception.user;

public class InvalidCredentialException  extends RuntimeException {

    public InvalidCredentialException(String message) {
        super(message);
    }
}