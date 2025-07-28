package org.scoula.member.exception;

import lombok.extern.slf4j.Slf4j;
import org.scoula.member.dto.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = {"org.scoula.member"})
@Slf4j
public class MemberExceptionHandler {
    // 사용자 없음 예외 처리(HTTP 404 Not Found)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUserNotFound(UserNotFoundException e) {
        log.warn("사용자가 없습니다. : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, e.getMessage()));
    }

    // email 중복 예외 처리(HTTP 409 Conflict)
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEmail(DuplicateEmailException e) {
        log.warn("중복된 이메일입니다. : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage()));
    }

    // nickname 중복 예외 처리
    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ApiResponse> handleDuplicateNickname(DuplicateNicknameException e) {
        log.warn("중복된 닉네임입니다. : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(false, e.getMessage()));
    }

    // 비밀번호 불일치 예외 처리(회원가입/비밀번호 변경 시)
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ApiResponse> handlePasswordMismatch(PasswordMismatchException e) {
        log.warn("비밀번호가 일치하지 않습니다. : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage()));
    }

    // 현재 비밀번호 오류 예외 처리(비밀번호 변경 시)
    @ExceptionHandler(CurrentPasswordInvalidException.class)
    public ResponseEntity<ApiResponse> handleCurrentPasswordInvalid(CurrentPasswordInvalidException e) {
        log.warn("현재 비밀번호가 옳지 않습니다. : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, e.getMessage()));
    }

    // 인증 실패 예외 처리(HTTP 401 Unauthorized)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthentication(AuthenticationException e) {
        log.warn("인증에 실패하였습니다. : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, e.getMessage()));
    }

    // JWT 토큰 관련 예외 처리(페이지 이탈 시 자동 로그아웃)
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse> handleInvalidToken(InvalidTokenException e) {
        log.warn("잘못된 토큰입니다. : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "인증이 필요합니다."));
    }

    // 입력값 유효성 검사 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(","));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(false, errorMessage));
    }
}
