package com.ssafy.server.exception.feed;

public class FeedNotFoundException extends RuntimeException {

    public FeedNotFoundException(String message) {
        super(message);
    }
}