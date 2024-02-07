package com.ssafy.server.common.error;

import org.springframework.http.HttpStatus;

public interface ExceptionEnum {

    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
