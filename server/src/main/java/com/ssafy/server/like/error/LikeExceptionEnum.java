package com.ssafy.server.like.error;

import com.ssafy.server.common.error.ExceptionEnum;
import org.springframework.http.HttpStatus;

public enum LikeExceptionEnum implements ExceptionEnum {

    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "L00001", "좋아요를 찾을 수 없습니다"),
    LIKE_CREATION_FAILED(HttpStatus.BAD_REQUEST, "L00002", "좋아요를 생성할 수 없습니다"),
    LIKE_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "L00003", "좋아요를 업데이트할 수 없습니다"),
    LIKE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "L00004", "좋아요를 삭제할 수 없습니다"),
    LIKE_FETCH_ERROR(HttpStatus.NOT_FOUND, "L00005", "피드에 대한 좋아요를 찾을 수 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;

    LikeExceptionEnum(HttpStatus status, String code, String message) {
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
