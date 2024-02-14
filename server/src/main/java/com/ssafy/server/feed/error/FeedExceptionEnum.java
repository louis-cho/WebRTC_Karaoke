package com.ssafy.server.feed.error;

import com.ssafy.server.common.error.ExceptionEnum;
import org.springframework.http.HttpStatus;

/**
 * 피드 예외를 정의한 enum 클래스
 */
public enum FeedExceptionEnum implements ExceptionEnum {
    FEED_NOT_FOUND(HttpStatus.NOT_FOUND, "F00001", "피드를 찾을 수 없습니다."),
    FEED_CREATION_FAILED(HttpStatus.BAD_REQUEST, "F00002", "피드를 생성할 수 없습니다."),
    FEED_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "F00003", "피드를 업데이트할 수 없습니다."),
    FEED_DELETE_FAILED(HttpStatus.BAD_REQUEST, "F00004", "피드를 삭제할 수 없습니다."),
    FEED_SORT_ERROR(HttpStatus.NOT_FOUND, "F00005", "피드를 정렬하던 중 문제가 발생하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    FeedExceptionEnum(HttpStatus status, String code, String message) {
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
