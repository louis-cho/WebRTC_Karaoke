package com.ssafy.server.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final String message;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
        this.message = message;
    }


    public ApiException(ApiException e) {
        this.status = e.getStatus();
        this.code = e.getCode();
        this.message = e.getMessage();
    }
}