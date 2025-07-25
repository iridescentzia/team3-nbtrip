package org.scoula.mypage.service;

import org.scoula.mypage.dto.MyPageDTO;
import org.scoula.mypage.dto.PasswordChangeRequestDTO;
import org.scoula.mypage.dto.UserUpdateRequestDTO;
import org.scoula.mypage.mapper.MyPageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class MyPageServiceImpl implements MyPageService {
    @Autowired
    private MyPageMapper myPageMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 사용자 정보 조회 - GET /api/users/{userId}
    @Override
    public MyPageDTO getUserInfo(Integer userId) throws Exception {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("사용자 ID가 유효하지 않습니다.");
        }
        MyPageDTO userInfo = myPageMapper.selectUserInfo(userId);
        if (userInfo == null) {
            throw new Exception("사용자 정보를 찾을 수 없습니다. (ID: " + userId + ")");
        }
        return userInfo;
    }

    // 사용자 정보 업데이트(email 중복 검사) - PUT /api/users/{userId}
    @Override
    public boolean updateUserInfo(Integer userId, UserUpdateRequestDTO requestDTO) throws Exception {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
        }
        if (requestDTO == null) {
            throw new IllegalArgumentException("업데이트할 정보가 없습니다.");
        }

        // 이메일 중복 검사
        if (requestDTO.getEmail() != null && !requestDTO.getEmail().trim().isEmpty()) {
            Map<String, Object> emailParams = new HashMap<>();
            emailParams.put("email", requestDTO.getEmail());
            emailParams.put("userId", userId);

            int emailCount = myPageMapper.checkEmailExists(emailParams);
            if (emailCount > 0) {
                throw new Exception("이미 사용 중인 이메일입니다.");
            }
        }

        // 닉네임 중복 검사
        if (requestDTO.getNickname() != null && !requestDTO.getNickname().trim().isEmpty()) {
            Map<String, Object> nicknameParams = new HashMap<>();
            nicknameParams.put("nickname", requestDTO.getNickname());
            nicknameParams.put("userId", userId);

            int nicknameCount = myPageMapper.checkNicknameExists(nicknameParams);
            if (nicknameCount > 0) {
                throw new Exception("이미 사용 중인 닉네임입니다.");
            }
        }

        // DTO 변환 및 업데이트
        MyPageDTO updateDTO = MyPageDTO.builder()
                .userId(userId)
                .email(requestDTO.getEmail())
                .nickname(requestDTO.getNickname())
                .name(requestDTO.getName())
                .phoneNumber(requestDTO.getPhoneNumber())
                .build();

        int updateCount = myPageMapper.updateUserInfo(updateDTO);
        return updateCount > 0;
    }

    // 현재 비밀번호 확인(암호화된 비밀번호-평문 비밀번호)
    @Override
    public boolean verifyCurrentPassword(Integer userId, String currentPassword) throws Exception {
        if (userId == null || userId <= 0 || currentPassword == null || currentPassword.trim().isEmpty()) {
            return false;
        }
        String encodedPassword = myPageMapper.selectUserPassword(userId);
        if (encodedPassword == null) { return false; }
        return passwordEncoder.matches(currentPassword, encodedPassword);
    }

    // 비밀번호 변경 - PUT /api/users/{userId}/password
    @Override
    public boolean changePassword(Integer userId, PasswordChangeRequestDTO requestDTO) throws Exception {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
        }
        if (requestDTO == null) {
            throw new IllegalArgumentException("비밀번호 변경 정보가 없습니다.");
        }

        // 현재 비밀번호 확인
        boolean isCurrentPasswordValid = verifyCurrentPassword(userId, requestDTO.getCurrentPassword());
        if (!isCurrentPasswordValid) {
            throw new Exception("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호와 확인 비밀번호 일치 검사
        if (!requestDTO.getNewPassword().equals(requestDTO.getNewPasswordConfirm())) {
            throw new Exception("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        // 비밀번호 정책 검사
        String newPassword = requestDTO.getNewPassword();
        if (newPassword.length() < 8) {
            throw new Exception("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        if (!newPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).+$")) {
            throw new Exception("비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.");
        }

        // 새 비밀번호 암호화 및 업데이트
        String newEncodedPassword = passwordEncoder.encode(newPassword);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("password", newEncodedPassword);
        int updateCount = myPageMapper.updateUserPassword(params);
        return updateCount > 0;
    }

    // FCM 토큰 갱신 구현 - PUT /api/users/fcm-token
    @Override
    public boolean updateFcmToken(Integer userId, String fcmToken) throws Exception {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("사용자 ID가 유효하지 않습니다.");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("fcmToken", fcmToken);
        int updateCount = myPageMapper.updateFcmToken(params);
        return updateCount > 0;
    }
}
