package com.ssafy.server.hit.error;

import com.ssafy.server.common.error.ExceptionEnum;
import org.springframework.http.HttpStatus;

public enum HitExceptionEnum implements ExceptionEnum {

    HIT_FETCH_ERROR(HttpStatus.NOT_FOUND, "H00001", "피드에 대한 조회수를 찾을 수 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;


    HitExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
