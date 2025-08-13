package org.scoula.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.account.dto.AccountRegisterDTO;
import org.scoula.account.mapper.AccountMapper;
import org.scoula.account.service.AccountService;
import org.scoula.member.domain.MemberVO;
import org.scoula.member.dto.*;
import org.scoula.member.exception.*;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;
    private PasswordEncoder passwordEncoder;
    private JwtProcessor jwtProcessor;
    private AccountService accountService;

    // 목업 userId
    private static final Set<Integer> MOCKUP_USER_IDS = Set.of(1, 2, 3, 4, 5, 6, 7, 8);

    // 생성자에서는 MemberMapper만 주입
    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    // Setter 주입으로 변경
    @Autowired
    @Lazy
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    @Lazy
    public void setJwtProcessor(JwtProcessor jwtProcessor) {
        this.jwtProcessor = jwtProcessor;
    }
    @Autowired
    @Lazy
    public void setAccountService(AccountService accountService) { this.accountService = accountService; }

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
                throw new DuplicateEmailException("이미 가입된 이메일입니다.");
            }

            // 2. 닉네임 중복 확인
            if(memberMapper.existsByNickname(memberDTO.getNickname())) {
                throw new DuplicateNicknameException("이미 사용중인 닉네임입니다.");
            }

            // 3. JWT 토큰 기반 이메일 인증
            boolean emailVerifiedStatus = false;
            log.info("JWT 토큰 기반 이메일 인증 방식 사용 - 초기 상태: false");

            // 4. MemberVO 생성 - 변수명 수정 필요
            MemberVO memberVO = MemberVO.builder()
                    .email(memberDTO.getEmail())
                    .password(passwordEncoder.encode(memberDTO.getPassword()))
                    .nickname(memberDTO.getNickname())
                    .name(memberDTO.getName())
                    .phoneNumber(memberDTO.getPhoneNumber())
                    .fcmToken(memberDTO.getFcmToken() != null ? memberDTO.getFcmToken() : "") // null 방지
                    .emailVerified(emailVerifiedStatus)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // 5. 회원 정보 저장
            memberMapper.insertMember(memberVO);
            log.info("✅ 사용자 정보 저장 완료 - userId: {}", memberVO.getUserId());

            // 6. 계좌 정보 저장
            if (memberDTO.getAccountNumber() != null && memberDTO.getBankName() != null) {
                AccountRegisterDTO accountDTO = new AccountRegisterDTO();
                accountDTO.setUserId(memberVO.getUserId());
                accountDTO.setAccountNumber(memberDTO.getAccountNumber());
                accountDTO.setBankName(memberDTO.getBankName());

                try {
                    accountService.registerAccount(accountDTO);
                    log.info("✅ 계좌 정보 저장 완료 - userId: {}, 계좌: {}, 은행: {}", memberVO.getUserId(), memberDTO.getAccountNumber(), memberDTO.getBankName());
                } catch (Exception e) {
                    log.error("❌ 계좌 정보 저장 실패 - userId: {}, 오류: {}", memberVO.getUserId(), e.getMessage());
                    throw new RuntimeException("계좌 정보 저장에 실패했습니다.");
                }
            }

            // 7. 목업 사용자 처리
            if (MOCKUP_USER_IDS.contains(memberVO.getUserId())) {
                memberVO.setEmailVerified(true);
                memberMapper.updateEmailVerified(memberDTO.getEmail(), true);
                log.info("🎯 목업 사용자 자동 이메일 인증 완료 - userId: {}", memberVO.getUserId());
            }

            log.info("🎉 회원가입 완료 - 회원 ID: {}, 이메일 인증: {}",
                    memberVO.getUserId(), memberVO.isEmailVerified());
            return new ApiResponse(true, "회원가입이 완료되었습니다.");

        } catch (DuplicateEmailException | DuplicateNicknameException e) {
            throw e;
        } catch (Exception e) {
            log.error("💥 회원가입 처리 중 오류 발생 : {}", e.getMessage(), e);
            throw new RuntimeException("회원가입 처리 중 오류가 발생했습니다: " + e.getMessage());
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

            // 3. 이메일 인증 상태 체크(목업 = true)
            if(!memberVO.isEmailVerified()) {
                log.warn("로그인 실패 - 이메일 미인증 : {}", loginRequestDTO.getEmail());
                throw new AuthenticationException("이메일 인증이 필요합니다. 이메일을 확인해주세요.");
            }

            // 4. 액세스 토큰 생성(24시간)
            String accessToken = jwtProcessor.generateAccessToken(
                    memberVO.getEmail(), memberVO.getUserId(), memberVO.getNickname()
            );

            // 5. 로그인 결과
            log.info("로그인 성공 - 회원 ID : {}, 이메일 : {}", memberVO.getUserId(), memberVO.getEmail());

            // 6. MemberResponseDTO 생성
            MemberResponseDTO memberResponse = new MemberResponseDTO();
            memberResponse.setUserId(memberVO.getUserId());
            memberResponse.setEmail(memberVO.getEmail());
            memberResponse.setNickname(memberVO.getNickname());
            memberResponse.setName(memberVO.getName());
            memberResponse.setEmailVerified(memberVO.isEmailVerified());
            memberResponse.setMaskedPhoneNumber(maskPhoneNumber(memberVO.getPhoneNumber()));
            memberResponse.setCreatedAt(memberVO.getCreatedAt());

            // 7. 토큰 만료 시간 계산
            Long expiresIn = 86400000L;
            return MemberLoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .fcmToken(memberVO.getFcmToken())
                    .member(memberResponse).build();
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
            responseDTO.setPhoneNumber(memberVO.getPhoneNumber());
            responseDTO.setEmailVerified(memberVO.isEmailVerified());
            responseDTO.setMaskedPhoneNumber(maskPhoneNumber(memberVO.getPhoneNumber()));
            responseDTO.setCreatedAt(memberVO.getCreatedAt());
            responseDTO.setUpdatedAt(memberVO.getUpdatedAt());
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

            // 6. password 업데이트 추가
            if (StringUtils.hasText(updateDTO.getPassword())) {
                String encodedPassword = passwordEncoder.encode(updateDTO.getPassword());
                memberMapper.updatePassword(userId, encodedPassword, LocalDateTime.now());
                MemberVO afterUpdate = memberMapper.findById(userId);
            }

            // 7. 수정된 정보 응답 DTO 생성
            MemberResponseDTO responseDTO = new MemberResponseDTO();
            responseDTO.setUserId(memberVO.getUserId());
            responseDTO.setEmail(memberVO.getEmail());
            responseDTO.setNickname(memberVO.getNickname());
            responseDTO.setName(memberVO.getName());
            responseDTO.setEmailVerified(memberVO.isEmailVerified());
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
        try {
            boolean exists = memberMapper.existsByNickname(nickname);
            log.info("닉네임 '{}' 중복 확인 결과: {}", nickname, exists);
            return exists;
        } catch (Exception e) {
            // 여기서 정확한 예외 스택 트레이스를 로그에 남깁니다.
            log.error("❌ 닉네임 중복 확인 중 오류 발생 - 닉네임: {}, 오류: {}", nickname, e.getMessage(), e);
            // 에러를 다시 던져서 상위 컨트롤러가 500 에러를 반환하게 합니다.
            throw new RuntimeException("닉네임 중복 확인 중 서버 오류가 발생했습니다.", e);
        }
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

    // 사용자 검색
    @Override
    @Transactional(readOnly = true)
    public List<MemberSearchResponseDTO> searchMembersByNickname(String nickname) {
        return memberMapper.searchUserByNickname(nickname).stream().map(MemberSearchResponseDTO::of).toList();
    }

    // 이메일로 userId 조회
    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO findByEmail(String email) throws UserNotFoundException {
        log.info("이메일로 사용자 조회 - email: {}", email);
        try {
            MemberVO member = memberMapper.findByEmail(email);
            if(member == null) {
                log.warn("사용자를 찾을 수 없음 - email: {}", email);
                throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
            }

            MemberResponseDTO responseDTO = new MemberResponseDTO();
            responseDTO.setUserId(member.getUserId());
            responseDTO.setEmail(member.getEmail());
            responseDTO.setNickname(member.getNickname());
            responseDTO.setName(member.getName());
            responseDTO.setPhoneNumber(member.getPhoneNumber());
            responseDTO.setEmailVerified(member.isEmailVerified());
            responseDTO.setMaskedPhoneNumber(maskPhoneNumber(member.getPhoneNumber()));
            responseDTO.setCreatedAt(member.getCreatedAt());
            responseDTO.setUpdatedAt(member.getUpdatedAt());

            log.info("이메일로 사용자 조회 성공 - email: {}, userId: {}", email, member.getUserId());
            return responseDTO;

        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("이메일로 사용자 조회 중 오류 발생 - email: {}, 오류: {}", email, e.getMessage(), e);
            throw new RuntimeException("사용자 조회 중 오류가 발생했습니다.");
        }
    }

    // 로그인 인증(이메일 인증 상태 포함)
    @Override
    @Transactional(readOnly = true)
    public MemberResponseDTO authenticate(String email, String password) throws UserNotFoundException, AuthenticationException {
        log.info("로그인 인증 처리 - email: {}", email);

        try {
            MemberVO member = memberMapper.findByEmail(email);
            if (member == null) {
                log.warn("로그인 실패 - 사용자 없음: {}", email);
                throw new UserNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다.");
            }

            if (!passwordEncoder.matches(password, member.getPassword())) {
                log.warn("로그인 실패 - 비밀번호 불일치: {}", email);
                throw new AuthenticationException("이메일 또는 비밀번호가 올바르지 않습니다.");
            }

            // MemberVO를 MemberResponseDTO로 변환
            MemberResponseDTO responseDTO = new MemberResponseDTO();
            responseDTO.setUserId(member.getUserId());
            responseDTO.setEmail(member.getEmail());
            responseDTO.setNickname(member.getNickname());
            responseDTO.setName(member.getName());
            responseDTO.setEmailVerified(member.isEmailVerified()); // 이메일 인증 상태
            responseDTO.setMaskedPhoneNumber(maskPhoneNumber(member.getPhoneNumber()));
            responseDTO.setCreatedAt(member.getCreatedAt());
            responseDTO.setUpdatedAt(member.getUpdatedAt());

            log.info("로그인 인증 성공 - email: {}, emailVerified: {}", email, member.isEmailVerified());
            return responseDTO;

        } catch (UserNotFoundException | AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("로그인 인증 중 오류 발생 - email: {}, 오류: {}", email, e.getMessage(), e);
            throw new RuntimeException("로그인 인증 중 오류가 발생했습니다.");
        }
    }

    // 이메일 인증 상태 업데이트
    @Override
    public void updateEmailVerified(String email, boolean verified) throws UserNotFoundException {
        log.info("이메일 인증 상태 업데이트 - email: {}, verified: {}", email, verified);

        try {
            // 사용자 존재 확인
            MemberVO member = memberMapper.findByEmail(email);
            if (member == null) {
                log.warn("이메일 인증 상태 업데이트 실패 - 사용자 없음: {}", email);
                throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
            }

            memberMapper.updateEmailVerified(email, verified);
            log.info("✅ 이메일 인증 상태 업데이트 완료 - email: {}, verified: {}", email, verified);

        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("이메일 인증 상태 업데이트 실패 - email: {}, 오류: {}", email, e.getMessage(), e);
            throw new RuntimeException("이메일 인증 상태 업데이트에 실패했습니다.");
        }
    }
}
