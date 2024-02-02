package com.ssafy.server.exception.user;

public class InvalidPasswordException  extends RuntimeException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}