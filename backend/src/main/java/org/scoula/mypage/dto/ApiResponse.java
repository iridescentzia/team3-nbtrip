package org.scoula.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private long timestamp;

    // 성공 응답
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청이 성공적으로 처리되었습니다.", data, System.currentTimeMillis());
    }

    // 성공 응답(메시지 포함)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, System.currentTimeMillis());
    }

    // 성공 응답(메시지만)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, System.currentTimeMillis());
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, System.currentTimeMillis());
    }
}
