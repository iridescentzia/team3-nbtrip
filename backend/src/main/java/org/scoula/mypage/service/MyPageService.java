package org.scoula.mypage.service;

import org.scoula.mypage.dto.MyPageDTO;
import org.scoula.mypage.dto.PasswordChangeRequestDTO;
import org.scoula.mypage.dto.UserUpdateRequestDTO;

public interface MyPageService {
    // 사용자 정보 조회(비밀번호 제외, 휴대폰 번호 마스킹) - GET /api/users/{userId}
    MyPageDTO getUserInfo(Integer userId) throws Exception;

    // 사용자 정보 업데이트 - PUT /api/users/{userId}
    boolean updateUserInfo(Integer userId, UserUpdateRequestDTO requestDTO) throws Exception;

    // 현재 비밀번호 확인(암호화 비밀번호-평문 비밀번호 비교) - PUT /api/users/{userId}
    boolean verifyCurrentPassword(Integer userId, String currentPassword) throws Exception;

    // 비밀번호 변경 - PUT /api/users/{userId}/password
    boolean changePassword(Integer userId, PasswordChangeRequestDTO requestDTO) throws Exception;

    // FCM 토큰 갱신 - PUT /api/users/fcm-token
    boolean updateFcmToken(Integer userId, String fcmToken) throws Exception;
}
