package com.ssafy.server.comment.error;

import com.ssafy.server.common.error.ExceptionEnum;
import org.springframework.http.HttpStatus;

public enum CommentExceptionEnum implements ExceptionEnum {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C00001", "댓글을 찾을 수 없습니다"),
    COMMENT_CREATION_FAILED(HttpStatus.BAD_REQUEST, "C00002", "댓글을 생성할 수 없습니다"),
    COMMENT_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "C00003", "댓글을 업데이트할 수 없습니다"),
    COMMENT_DELETE_FAILED(HttpStatus.BAD_REQUEST, "C00004", "댓글을 삭제할 수 없습니다"),
    COMMENT_FETCH_ERROR(HttpStatus.NOT_FOUND, "C00005", "피드에 대한 댓글을 찾을 수 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;

    CommentExceptionEnum(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
