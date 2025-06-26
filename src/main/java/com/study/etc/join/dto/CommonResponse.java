package com.study.etc.join.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommonResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> CommonResponse<T> fail(String message) {
        return CommonResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
