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

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

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
                    .fcmToken(memberDTO.getFcmToken())
                    .role("USER")
                    .createAt(LocalDateTime.now())
                    .isActive(true)
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
            Optional<MemberVO> memberOptional = memberMapper.fingByEmail(loginRequestDTO.getEmail());
            MemberVO memberVO = memberOptional.orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));

            // 2. 계정 활성화 상태 확인
            if (!memberVO.getIsActive()) {
                log.warn("비활성화된 계정 로그인 시도 : {}", loginRequestDTO.getEmail());
                throw new AuthenticationException("비활성화된 계정입니다.");
            }

            // 3. 비밀번호 검증
            if(!passwordEncoder.matches(loginRequestDTO.getPassword(), memberVO.getPassword())) {
                log.warn("비밀번호 불일치 - 이메일 : {}", loginRequestDTO.getEmail());
                throw new AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다.");
            }

            // 4. 액세스 토큰 생성(24시간)
            String accessToken = jwtProcessor.generateToken(
                    memberVO.getUserId(), memberVO.getEmail(), memberVO.getRole(), 1440
            );

            // 5. MyBatis로 로그인 시간 업데이트
            memberMapper.updateLastLoginTime(memberVO.getUserId(), LocalDateTime.now());
            log.info("로그인 성공 - 회원 ID : {}, 이메일 : {}", memberVO.getUserId(), memberVO.getEmail());

            // 6. MemberResponseDTO 생성
            MemberResponseDTO memberResponse = new MemberResponseDTO();
            memberResponse.setUserId(memberVO.getUserId());
            memberResponse.setEmail(memberVO.getEmail());
            memberResponse.setNickname(memberVO.getNickname());
            memberResponse.setName(memberVO.getName());
            memberResponse.setCreatedAt(memberVO.getCreatedAt());

            return MemberLoginResponseDTO.builder()
                    .accessToken(accessToken).member(memberResponse).build();
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("회원 정보 조회 중 오류 발생 - 회원ID : {}, 오류 : {}", userId, e.getMessage(), e);
            throw new RuntimeException("회원 정보 조회 중 오류가 발생했습니다.");
        }
    }

    // 회원 정보 수정
    @Override
    public MemberResponseDTO updateMember(int userId, MemberUpdateDTO updateDTO) {
        log.info("회원 정보 수정 시작 - 회원ID : {}", userId);
        try {
            // 1. MyBatis로 회원 존재 확인
            Optional<MemberVO> memberOptional = memberMapper.findById(userId);
            MemberVO memberVO = memberOptional
                    .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));

            // 2. 닉네임 변경 시 중복 확인
            if (StringUtils.hasText(updateDTO.getNickname()) &&
                    !updateDTO.getNickname().equals(memberVO.getNickname())) {
                if (memberMapper.existsByNickname(updateDTO.getNickname())) {
                    throw new DuplicateNicknameException("이미 사용중인 닉네임입니다.");
                }
                memberVO.setNickname(updateDTO.getNickname());
            }

            // 3. 이름 업데이트
            if (StringUtils.hasText(updateDTO.getName())) {
                memberVO.setName(updateDTO.getName());
            }

            // 4. MyBatis로 데이터베이스 업데이트 (업데이트 시간 제외)
            memberMapper.updateMember(memberVO);
            log.info("회원 정보 수정 완료 - 회원ID : {}", userId);

            // 5. 수정된 정보 응답 DTO 생성 (프로필 이미지 제외)
            MemberResponseDTO responseDTO = new MemberResponseDTO();
            responseDTO.setUserId(memberVO.getUserId());
            responseDTO.setEmail(memberVO.getEmail());
            responseDTO.setNickname(memberVO.getNickname());
            responseDTO.setName(memberVO.getName());
            responseDTO.setCreatedAt(memberVO.getCreatedAt());
            return responseDTO;
        } catch (UserNotFoundException | DuplicateNicknameException e) {
            throw e;
        } catch (Exception e) {
            log.error("회원 정보 수정 중 오류 발생 - 회원ID : {}, 오류 : {}", userId, e.getMessage(), e);
            throw new RuntimeException("회원 정보 수정 중 오류가 발생했습니다.");
        }
    }

    // 비밀번호 변경
    @Override
    public void changePassword(int userId, MemberPasswordDTO passwordDTO) {
        log.info("비밀번호 변경 시작 - 회원ID: {}", userId);
        try {
            // 1. MyBatis로 회원 정보 조회
            Optional<MemberVO> memberOptional = memberMapper.findById(userId);
            MemberVO memberVO = memberOptional
                    .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다."));

            // 2. 현재 비밀번호 검증
            if (!passwordEncoder.matches(passwordDTO.getCurrentPassword(), memberVO.getPassword())) {
                log.warn("현재 비밀번호 불일치 - 회원ID: {}", userId);
                throw new PasswordMismatchException("현재 비밀번호가 올바르지 않습니다.");
            }

            // 3. 새 비밀번호 암호화 및 MyBatis로 업데이트
            String encodedNewPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
            memberMapper.updatePassword(userId, encodedNewPassword);
            log.info("비밀번호 변경 완료 - 회원ID : {}", userId);
        } catch (UserNotFoundException | PasswordMismatchException e) {
            throw e;
        } catch (Exception e) {
            log.error("비밀번호 변경 중 오류 발생 - 회원ID : {}, 오류 : {}", userId, e.getMessage(), e);
            throw new RuntimeException("비밀번호 변경 중 오류가 발생했습니다.");
        }
    }

    // FCM 토큰 갱신
    @Override
    public void updateFcmToken(int userId, MemberFcmTokenDTO fcmTokenDTO) {
        log.info("FCM 토큰 갱신 시작 - 회원ID : {}", userId);
        try {
            // 1. MyBatis로 회원 존재 확인
            if (!memberMapper.existsById(userId)) {
                throw new UserNotFoundException("존재하지 않는 회원입니다.");
            }

            // 2. MyBatis로 FCM 토큰 업데이트
            memberMapper.updateFcmToken(userId, fcmTokenDTO.getFcmToken());
            log.info("FCM 토큰 갱신 완료 - 회원ID: {}", userId);
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("FCM 토큰 갱신 중 오류 발생 - 회원ID: {}, 오류: {}", userId, e.getMessage(), e);
            throw new RuntimeException("FCM 토큰 갱신 중 오류가 발생했습니다.");
        }
    }

    // 닉네임 중복
    @Override
    @Transactional(readOnly = true)
    public boolean checkNicknameDuplicate(String nickname) {
        log.debug("닉네임 중복 확인 - 닉네임: {}", nickname);
        return memberMapper.existsByNickname(nickname);
    }

    // 닉네임으로 사용자 ID 조회 구현
    @Override
    @Transactional(readOnly = true)
    public int findUserIdByNickname(String nickname) {
        log.info("닉네임으로 사용자 ID 조회 - 닉네임: {}", nickname);
        try {
            int userId = memberMapper.findUserIdByNickname(nickname);
            if (userId == 0) {
                throw new UserNotFoundException("해당 닉네임의 사용자를 찾을 수 없습니다.");
            }
            log.info("사용자 ID 조회 성공 - 닉네임: {}, 사용자ID: {}", nickname, userId);
            return userId;
        } catch (Exception e) {
            log.error("사용자 ID 조회 중 오류 발생 - 닉네임: {}, 오류: {}", nickname, e.getMessage(), e);
            throw new UserNotFoundException("해당 닉네임의 사용자를 찾을 수 없습니다.");
        }
    }
}
