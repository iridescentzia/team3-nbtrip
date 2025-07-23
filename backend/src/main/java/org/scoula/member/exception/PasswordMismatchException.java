package org.scoula.member.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
    public PasswordMismatchException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
