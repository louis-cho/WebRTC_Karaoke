package com.ssafy.server.api;

import com.ssafy.server.common.error.ApiExceptionEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * api 요청 응답 클래스
 * @param <T>
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private ApiExceptionEntity exception;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", null, data, null);
    }

    public static ApiResponse<?> error(String message) {
        return new ApiResponse<>("error", message, null, null);
    }

    @Builder
    public ApiResponse(String status, String message, T data, ApiExceptionEntity exception) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.exception = exception;
    }
}
