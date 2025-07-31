package org.scoula.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.member.domain.MemberVO;
import org.scoula.member.dto.*;
import org.scoula.member.exception.*;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

    // 전화번호 마스킹 처리 메서드
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 7) return phoneNumber;
        return phoneNumber.substring(0, 3) + "-****-" + phoneNumber.substring(phoneNumber.length() - 4);
    }

    // 회원가입
    @Override
    public ApiResponse registerMember(MemberDTO memberDTO) {
        log.info("회원가입 시작 - 이메일 : {}", memberDTO.getEmail());
        try {
            // 1. 이메일 중복 확인
            if(memberMapper.existsByEmail(memberDTO.getEmail())) {
                log.warn("이미 가입된 이메일 : {}", memberDTO.getEmail());
                throw new DuplicateEmailException("이미 가입된 이메일입니다.");
            }

            // 2. 닉네임 중복 확인
            if(memberMapper.existsByNickname(memberDTO.getNickname())) {
                log.warn("이미 사용중인 닉네임 : {}", memberDTO.getNickname());
                throw new DuplicateNicknameException("이미 사용중인 닉네임입니다.");
            }

            // 3. MemberVO 생성 및 비밀번호 암호화
            MemberVO memberVO = MemberVO.builder()
                    .email(memberDTO.getEmail())
                    .password(passwordEncoder.encode(memberDTO.getPassword()))
                    .nickname(memberDTO.getNickname())
                    .name(memberDTO.getName())
                    .phoneNumber(memberDTO.getPhoneNumber())
                    .fcmToken(memberDTO.getFcmToken())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // 4. MyBatis로 회원 정보 저장
            memberMapper.insertMember(memberVO);
            log.info("회원가입 완료 - 회원 ID : {}, 이메일 : {}", memberVO.getUserId(), memberVO.getEmail());
            return new ApiResponse(true, "회원가입이 완료되었습니다.");
        } catch (DuplicateEmailException | DuplicateNicknameException e) {
            throw e;
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생 : {}", e.getMessage(), e);
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다.");
        }
    }

    // 로그인
    @Override
    public MemberLoginResponseDTO loginMember(MemberLoginRequestDTO loginRequestDTO) {
        log.info("로그인 시도 - 이메일 : {}", loginRequestDTO.getEmail());
        try {
            // 1. MyBatis로 회원정보 조회
            MemberVO memberVO = memberMapper.findByEmail(loginRequestDTO.getEmail());
            if (memberVO == null) { throw new UserNotFoundException("존재하지 않는 회원입니다."); }

            // 2. 비밀번호 검증
            if(!passwordEncoder.matches(loginRequestDTO.getPassword(), memberVO.getPassword())) {
                log.warn("비밀번호 불일치 - 이메일 : {}", loginRequestDTO.getEmail());
                throw new AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다.");
            }

            // 3. 액세스 토큰 생성(24시간)
            String accessToken = jwtProcessor.generateAccessToken(
                    memberVO.getEmail(), memberVO.getUserId(), memberVO.getNickname()
            );

            // 4. 로그인 결과
            log.info("로그인 성공 - 회원 ID : {}, 이메일 : {}", memberVO.getUserId(), memberVO.getEmail());

            // 5. MemberResponseDTO 생성
            MemberResponseDTO memberResponse = new MemberResponseDTO();
            memberResponse.setUserId(memberVO.getUserId());
            memberResponse.setEmail(memberVO.getEmail());
            memberResponse.setNickname(memberVO.getNickname());
            memberResponse.setName(memberVO.getName());
            memberResponse.setMaskedPhoneNumber(maskPhoneNumber(memberVO.getPhoneNumber()));
            memberResponse.setCreatedAt(memberVO.getCreatedAt());

            // 6. 토큰 만료 시간 계산
            Long expiresIn = 86400000L;
            return MemberLoginResponseDTO.builder()
                    .accessToken(accessToken).member(memberResponse).build();
        } catch (UserNotFoundException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생 - 회원 ID : {}, 오류 : {}", e.getMessage(), e);
            throw new RuntimeException("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    // 로그아웃
    @Override
    public void logoutMember(int userId) {
        log.info("로그아웃 처리 시작 - 회원 ID : {}", userId);
        try {
            // 1. MyBatis로 회원 존재 확인
            if(!memberMapper.existsById(userId)) {
                throw new UserNotFoundException("존재하지 않는 회원입니다.");
            }

            // 2. MyBatis로 FCM 토큰 삭제
            memberMapper.updateFcmToken(userId, null, LocalDateTime.now());
            log.info("로그아웃 완료 - 회원 ID : {}", userId);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생 - 회원 ID : {}, 오류 : {}", userId, e.getMessage(), e);
            throw new RuntimeException("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }

    // 회원 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO getMemberInfo(int userId) {
        log.info("회원 정보 조회 - 회원 ID : {}", userId);
        try {
            MemberVO memberVO = memberMapper.findById(userId);
            if (memberVO == null) { throw new UserNotFoundException("존재하지 않는 회원입니다."); }

            MemberResponseDTO responseDTO = new MemberResponseDTO();
            responseDTO.setUserId(memberVO.getUserId());
            responseDTO.setEmail(memberVO.getEmail());
            responseDTO.setNickname(memberVO.getNickname());
            responseDTO.setName(memberVO.getName());
            responseDTO.setMaskedPhoneNumber(maskPhoneNumber(memberVO.getPhoneNumber()));
            responseDTO.setCreatedAt(memberVO.getCreatedAt());
            return responseDTO;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("회원 정보 조회 중 오류 발생 - 회원 ID : {}, 오류 : {}", userId, e.getMessage(), e);
            throw new RuntimeException("회원 정보 조회 중 오류가 발생했습니다.");
        }
    }

    // 회원 정보 수정
    @Override
    public MemberResponseDTO updateMember(int userId, MemberUpdateDTO updateDTO) {
        log.info("회원 정보 수정 시작 - 회원ID : {}", userId);
        try {
            // 1. MyBatis로 회원 존재 확인
            MemberVO memberVO = memberMapper.findById(userId);
            if (memberVO == null) { throw new UserNotFoundException("존재하지 않는 회원입니다."); }

            // 2. nickname 변경 시 중복 확인
            if (StringUtils.hasText(updateDTO.getNickname()) && !updateDTO.getNickname().equals(memberVO.getNickname())) {
                if (memberMapper.existsByNickname(updateDTO.getNickname())) {
                    throw new DuplicateNicknameException("이미 사용중인 닉네임입니다.");
                }
                memberVO.setNickname(updateDTO.getNickname());
            }

            // 3. name 업데이트
            if (StringUtils.hasText(updateDTO.getName())) {
                memberVO.setName(updateDTO.getName());
            }

            // 4. phoneNumber 업데이트
            if (StringUtils.hasText(updateDTO.getPhoneNumber())) {
                memberVO.setPhoneNumber(updateDTO.getPhoneNumber());
            }

            // 5. MyBatis로 데이터베이스 업데이트 (업데이트 시간 제외)
            memberVO.setUpdatedAt(LocalDateTime.now());
            memberMapper.updateMember(memberVO);
            log.info("회원 정보 수정 완료 - 회원 ID : {}", userId);

            // 6. 수정된 정보 응답 DTO 생성
            MemberResponseDTO responseDTO = new MemberResponseDTO();
            responseDTO.setUserId(memberVO.getUserId());
            responseDTO.setEmail(memberVO.getEmail());
            responseDTO.setNickname(memberVO.getNickname());
            responseDTO.setName(memberVO.getName());
            responseDTO.setMaskedPhoneNumber(maskPhoneNumber(memberVO.getPhoneNumber()));
            responseDTO.setCreatedAt(memberVO.getCreatedAt());
            return responseDTO;
        } catch (UserNotFoundException | DuplicateNicknameException e) {
            throw e;
        } catch (Exception e) {
            log.error("회원 정보 수정 중 오류 발생 - 회원 ID : {}, 오류 : {}", userId, e.getMessage(), e);
            throw new RuntimeException("회원 정보 수정 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 검증 요청
    @Override
    public boolean verifyPassword(int userId, String password) {
        log.info("비밀번호 검증 요청 - 회원 ID: {}", userId);
        try {
            MemberVO member = memberMapper.findById(userId);
            if (member == null) {
                throw new UserNotFoundException("존재하지 않는 회원입니다.");
            }
            boolean match = passwordEncoder.matches(password, member.getPassword());
            if (!match) {
                throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
            }
            return true;
        } catch (UserNotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            log.error("비밀번호 검증 실패 - 회원 ID: {}, 오류: {}", userId, e.getMessage(), e);
            throw new RuntimeException("비밀번호 검증 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 변경
    @Override
    public void changePassword(int userId, MemberPasswordDTO passwordDTO) {
        log.info("비밀번호 변경 시작 - 회원 ID: {}", userId);
        try {
            // 1. MyBatis로 회원 정보 조회
            MemberVO memberVO = memberMapper.findById(userId);
            if (memberVO == null) { throw new UserNotFoundException("존재하지 않는 회원입니다."); }

            // 2. 현재 비밀번호 검증
            if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), memberVO.getPassword())) {
                log.warn("현재 비밀번호 불일치 - 회원 ID: {}", userId);
                throw new PasswordMismatchException("현재 비밀번호가 올바르지 않습니다.");
            }

            // 3. 새 비밀번호 암호화 및 MyBatis로 업데이트
            String encodedNewPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
            memberMapper.updatePassword(userId, encodedNewPassword, LocalDateTime.now());
            log.info("비밀번호 변경 완료 - 회원 ID : {}", userId);
        } catch (UserNotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생 - 회원 ID : {}, 오류 : {}", userId, e.getMessage(), e);
            throw new RuntimeException("비밀번호 변경 중 오류가 발생했습니다.");
        }
    }

    // FCM 토큰 갱신
    @Override
    public void updateFcmToken(int userId, MemberFcmTokenDTO fcmTokenDTO) {
        log.info("FCM 토큰 갱신 시작 - 회원 ID : {}", userId);
        try {
            // 1. MyBatis로 회원 존재 확인
            if (!memberMapper.existsById(userId)) {
                throw new UserNotFoundException("존재하지 않는 회원입니다.");
            }

            // 2. MyBatis로 FCM 토큰 업데이트
            memberMapper.updateFcmToken(userId, fcmTokenDTO.getFcmToken(), LocalDateTime.now());
            log.info("FCM 토큰 갱신 완료 - 회원 ID : {}", userId);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("FCM 토큰 갱신 중 오류 발생 - 회원 ID : {}, 오류 : {}", userId, e.getMessage(), e);
            throw new RuntimeException("FCM 토큰 갱신 중 오류가 발생했습니다.");
        }
    }

    // 닉네임 중복
    @Override
    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        log.debug("닉네임 중복 확인 - 닉네임 : {}", nickname);
        return memberMapper.existsByNickname(nickname);
    }

    // 닉네임으로 사용자 ID 조회 구현
    @Override
    @Transactional(readOnly = true)
    public int findUserIdByNickname(String nickname) {
        log.info("닉네임으로 사용자 ID 조회 - 닉네임 : {}", nickname);
        try {
            int userId = memberMapper.findUserIdByNickname(nickname);
            if (userId == 0) {
                throw new UserNotFoundException("해당 닉네임의 사용자를 찾을 수 없습니다.");
            }
            log.info("사용자 ID 조회 성공 - 닉네임 : {}, 사용자 ID : {}", nickname, userId);
            return userId;
        } catch (Exception e) {
            log.error("사용자 ID 조회 중 오류 발생 - 닉네임 : {}, 오류 : {}", nickname, e.getMessage(), e);
            throw new UserNotFoundException("해당 닉네임의 사용자를 찾을 수 없습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberSearchResponseDTO> searchMembersByNickname(String nickname) {
        return memberMapper.searchUserByNickname(nickname).stream().map(MemberSearchResponseDTO::of).toList();
    }
}
