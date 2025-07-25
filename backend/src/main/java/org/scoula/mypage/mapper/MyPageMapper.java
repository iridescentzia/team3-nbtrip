package org.scoula.mypage.mapper;

import org.scoula.mypage.dto.MyPageDTO;

import java.util.Map;

public interface MyPageMapper {
    // 사용자 정보 조회(userId) - GET /api/users/{userId}
    MyPageDTO selectUserInfo(Integer userId);

    // 사용자 정보 업데이트(1 : 성공, 0 : 실패) - PUT /api/users/{userId}
    int updateUserInfo(MyPageDTO myPageDTO);

    // 사용자 password 조회(암호화)
    String selectUserPassword(Integer userId);

    // 사용자 password 업데이트 - PUT /api/users/{userId}/password
    int updateUserPassword(Map<String, Object> params);

    // email 중복 검사(본인 제외)(0 : 사용 가능, 1이상 : 중복)
    int checkEmailExists(Map<String, Object> params);

    // nickname 중복 검사(0 : 사용 가능, 1이상 : 중복)
    int checkNicknameExists(Map<String, Object> params);

    // FCM 토큰 업데이트(1 : 성공, 0 : 실패)
    int updateFcmToken(Map<String, Object> params);
}
