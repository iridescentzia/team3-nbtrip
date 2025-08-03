package org.scoula.member.exception;

public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException(String nickname) {
        super("이미 사용 중인 닉네임입니다. Nickname: " + nickname);
    }
}
