package org.scoula.member.exception;

public class CurrentPasswordInvalidException extends RuntimeException {
    public CurrentPasswordInvalidException() {
        super("현재 비밀번호가 올바르지 않습니다.");
    }
}
