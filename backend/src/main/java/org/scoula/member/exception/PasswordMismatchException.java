package org.scoula.member.exception;

public class PasswordMismatchException extends RuntimeException {
    // 기본 예외 메시지
    public PasswordMismatchException(String message) {
        super(message);
    }

    // 상세 예외 메시지
    public PasswordMismatchException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
