package com.ssafy.server.user.error;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.ssafy.server.common.error.ExceptionEnum;
import org.springframework.http.HttpStatus;

public enum UserExceptionEnum implements ExceptionEnum {
        USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U00001", "유저를 찾을 수 없습니다"),
        USER_DELETE_FAIL(HttpStatus.BAD_REQUEST, "U00002", "유저를 삭제할 수 없습니다"),
        USER_UPDATE_FAIL(HttpStatus.BAD_REQUEST, "U00003", "유저를 업데이트할 수 없습니다");

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
