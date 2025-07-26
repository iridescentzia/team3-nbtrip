package org.scoula.member.service;

import org.scoula.member.dto.*;
import org.scoula.member.exception.*;

public interface MemberService {
    // 회원가입
    ApiResponse registerMember(MemberDTO memberDTO) throws DuplicateEmailException, DuplicateNicknameException;

    // 로그인(JWT 토큰 발급)
    MemberLoginResponseDTO loginMember(MemberLoginRequestDTO loginRequestDTO) throws UserNotFoundException, AuthenticationException;

    // 로그아웃(FCM 토큰만 삭제)
    void logoutMember(int userId) throws UserNotFoundException;

    // 회원 정보 조회
    MemberResponseDTO getMemberInfo(int userId) throws UserNotFoundException;

    // 회원 정보 수정
    MemberResponseDTO updateMember(int userId, MemberUpdateDTO updateDTO) throws UserNotFoundException, AuthenticationException;

    // 비밀번호 변경
    void changePassword(int userId, MemberPasswordDTO passwordDTO) throws UserNotFoundException, PasswordMismatchException;

    // FCM Token 갱신
    void updateFcmToken(int userId, MemberFcmTokenDTO fcmTokenDTO) throws UserNotFoundException, InvalidTokenException;

    // 닉네임 중복 체크(true : 중복, false : 사용 가능)
    boolean checkNicknameDuplicate(String nickname);

    // 닉네임으로 userId 조회
    int findUserIdByNickname(String nickname) throws UserNotFoundException;
}
