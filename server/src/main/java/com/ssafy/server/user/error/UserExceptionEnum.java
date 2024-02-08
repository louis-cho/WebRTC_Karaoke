package com.ssafy.server.user.error;

import com.ssafy.server.common.error.ExceptionEnum;
import org.springframework.http.HttpStatus;

public enum UserExceptionEnum implements ExceptionEnum {
        USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U00001", "유저를 찾을 수 없습니다");

        private final HttpStatus status;
        private final String code;
        private final String message;

    UserExceptionEnum(HttpStatus status, String code, String message) {
            this.status = status;
            this.code = code;
            this.message = message;
        }


    @Override
    public HttpStatus getStatus() {
        return null;
    }

    @Override
    public String getCode() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }
}
