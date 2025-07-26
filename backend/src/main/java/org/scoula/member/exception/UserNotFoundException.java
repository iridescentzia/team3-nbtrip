package org.scoula.member.exception;

import org.springframework.security.core.userdetails.User;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    // userId로 조회 실패 시
    public UserNotFoundException(Integer userId) {
        super("사용자를 찾을 수 없습니다. ID: " + userId);
    }

    // email로 조회 실패 시
    public UserNotFoundException(String email, boolean isEmail) {
        super("사용자를 찾을 수 없습니다. Email: " + email);
    }
}
