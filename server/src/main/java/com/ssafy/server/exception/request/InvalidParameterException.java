package com.ssafy.server.exception.request;

public class InvalidParameterException  extends RuntimeException {

    public InvalidParameterException(String message) {
        super(message);
    }
}