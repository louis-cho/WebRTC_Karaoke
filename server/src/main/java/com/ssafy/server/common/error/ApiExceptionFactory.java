package com.ssafy.server.common.error;

import com.ssafy.server.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiExceptionFactory {

    public static ApiException fromExceptionEnum(ExceptionEnum exceptionEnum) {
        return new ApiException(
                exceptionEnum.getStatus(),
                exceptionEnum.getCode(),
                exceptionEnum.getMessage()
        );
    }

}