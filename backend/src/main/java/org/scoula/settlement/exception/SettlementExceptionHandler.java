package org.scoula.settlement.exception;

import lombok.Builder;
import lombok.Data;
import org.scoula.settlement.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class SettlementExceptionHandler {

    @ExceptionHandler(SettlementException.class)
    public ResponseEntity<ErrorResponseDto> handleSettlementException(SettlementException e) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .errorCode(e.getErrorCode().name())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(NoSuchElementException e) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .errorCode("NOT_FOUND")
                .message("요청한 자원을 찾을 수 없습니다")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalState(IllegalStateException e) {
        ErrorResponseDto error = ErrorResponseDto.builder()
                .errorCode("INTERNAL_ERROR")
                .message("내부 처리 오류가 발생했습니다")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(500).body(error);
    }
}

