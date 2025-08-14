package org.scoula.member.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final ExecutorService emailExecutor = Executors.newFixedThreadPool(3);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.smtp.username}")
    private String fromEmail;

    // 메모리에 인증번호 임시 저장
    private Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private Map<String, LocalDateTime> expirationTimes = new ConcurrentHashMap<>();
    private Map<String, Boolean> emailVerifiedStatus = new ConcurrentHashMap<>();

    // 목업 사용자 ID 허용(user_id 1~8)
    private static final Set<Integer> MOCKUP_USER_IDS = Set.of(1, 2, 3, 4, 5, 6, 7, 8);

    // 목업 사용자 여부 확인
    private boolean isMockupUser(Integer userId) {
        return userId != null && MOCKUP_USER_IDS.contains(userId);
    }

    // 이메일 인증번호 검증
    public boolean verifyCodeByEmail(String email, String code) {
        log.info("🔍 이메일 인증번호 검증 - 이메일: {}", email);

        String savedCode = verificationCodes.get(email);
        LocalDateTime expiration = expirationTimes.get(email);

        if (savedCode != null && expiration != null) {
            if (LocalDateTime.now().isBefore(expiration)) {
                if (savedCode.equals(code)) {
                    // 인증 성공 시 저장된 인증번호 삭제
                    verificationCodes.remove(email);
                    expirationTimes.remove(email);
                    log.info("✅ 이메일 인증 성공 - 이메일: {}", email);
                    return true;
                }
            } else {
                // 만료된 인증번호 삭제
                verificationCodes.remove(email);
                expirationTimes.remove(email);
                log.warn("⏰ 인증번호 만료 - 이메일: {}", email);
            }
        }
        log.warn("❌ 이메일 인증 실패 - 이메일: {}", email);
        return false;
    }

    // 이메일로 인증번호 재발송
    public void resendVerificationCodeByEmail(String email) {
        log.info("🔍 인증번호 재발송 - 이메일: {}", email);

        // 새로운 인증번호 생성
        String code = generateVerificationCode();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);  // 10분 유효

        // 기존 인증번호 덮어쓰기
        verificationCodes.put(email, code);
        expirationTimes.put(email, expiration);

        try {
            // 이메일 발송
            sendEmail(email, code);
            log.info("✅ 인증번호 재발송 완료 - 이메일: {}", email);
        } catch (Exception e) {
            // 발송 실패 시 저장된 인증번호 삭제
            verificationCodes.remove(email);
            expirationTimes.remove(email);
            log.error("❌ 인증번호 재발송 실패 - 이메일: {}", email, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * 이메일 인증 완료 상태를 메모리에 저장
     */
    public void markEmailAsVerified(String email) {
        emailVerifiedStatus.put(email, true);
        log.info("✅ 이메일 인증 상태 메모리 저장: {}", email);
    }

    /**
     * 메모리에서 이메일 인증 상태 확인
     */
    public boolean isEmailVerifiedInMemory(String email) {
        return emailVerifiedStatus.getOrDefault(email, false);
    }

    /**
     * 회원가입 완료 후 메모리 상태 정리
     */
    public void clearEmailVerificationStatus(String email) {
        emailVerifiedStatus.remove(email);
        log.info("🧹 이메일 인증 상태 메모리 정리: {}", email);
    }

    // 비동기 이메일 발송 - 즉시 응답 반환
    public void sendVerificationCodeAsync(String email) {
        String code = generateVerificationCode();
        verificationCodes.put(email, code);
        expirationTimes.put(email, LocalDateTime.now().plusMinutes(10));

        log.info("📤 인증번호 생성 완료, 비동기 발송 시작 - 이메일: {}", email);
        emailExecutor.submit(() -> {
            try {
                sendEmail(email, code);
                log.info("✅ 비동기 이메일 발송 성공: {}", email);
            } catch (Exception e) {
                log.error("❌ 비동기 이메일 발송 실패: {}", email, e);
                verificationCodes.remove(email);
                expirationTimes.remove(email);
            }
        });
    }

    // 인증번호 생성 및 이메일 발송
    public void sendVerificationCode(String email, Integer userId) {
        if (isMockupUser(userId)) {
            log.info("목업 사용자(userId: {})는 이메일 인증을 자동 완료합니다", userId);
            return;
        }

        // 6자리 랜덤 인증번호 생성
        String code = generateVerificationCode();
        verificationCodes.put(email, code);
        expirationTimes.put(email, LocalDateTime.now().plusMinutes(10));

        // 실제 이메일 발송
        sendEmail(email, code);
    }

    // 실제 이메일 발송
    private void sendEmail(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(fromEmail, "N빵 트립");
            helper.setSubject("[N빵 트립] 이메일 인증번호");

            String htmlContent =
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;'>" +
                            "<div style='text-align: center; margin-bottom: 30px;'>" +
                            "<h1 style='color: #4CAF50; margin: 0;'>N빵 트립</h1>" +
                            "<h2 style='color: #333; margin: 10px 0;'>이메일 인증번호</h2>" +
                            "</div>" +
                            "<div style='background-color: #f8f9fa; padding: 30px; border-radius: 10px; text-align: center;'>" +
                            "<p style='font-size: 16px; color: #666; margin-bottom: 20px;'>안녕하세요! 회원가입을 완료하려면 아래 인증번호를 입력해주세요:</p>" +
                            "<div style='background-color: white; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
                            "<span style='font-size: 36px; font-weight: bold; color: #4CAF50; letter-spacing: 8px;'>" +
                            code + "</span>" +
                            "</div>" +
                            "<div style='margin-top: 20px; padding: 15px; background-color: #fff3cd; border-radius: 5px;'>" +
                            "<p style='color: #856404; font-size: 14px; margin: 0;'>" +
                            "이 인증번호는 <strong>10분 후 만료</strong>됩니다.<br>" +
                            "</p>" +
                            "</div>" +
                            "</div>" +
                            "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("인증 이메일 발송 완료: {}***", email.substring(0, Math.min(email.length(), 3)));

        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다. Gmail SMTP 설정을 확인해주세요.", e);
        }
    }

    // 인증번호 확인
    public boolean verifyCode(String email, String code, Integer userId) {
        if (isMockupUser(userId)) {
            return true;
        }

        String savedCode = verificationCodes.get(email);
        LocalDateTime expiration = expirationTimes.get(email);

        if (savedCode != null && expiration != null && LocalDateTime.now().isBefore(expiration)) {
            if (savedCode.equals(code)) {
                verificationCodes.remove(email);
                expirationTimes.remove(email);
                log.info("이메일 인증 성공 : {}", email);
                return true;
            } else {
                log.warn("잘못된 인증번호 입력: {}", email);
            }
        } else {
            log.warn("만료되거나 존재하지 않는 인증번호: {}", email);
        }
        return false;
    }

    // 인증번호 재발송
    public void resendVerificationCode(String email, Integer userId) {
        verificationCodes.remove(email);
        expirationTimes.remove(email);
        sendVerificationCode(email, userId);
    }

    // 6자리 랜덤 인증번호 생성
    private String generateVerificationCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }

    // 종료 시 ExecutorService 정리(리소스 누수 방지)
    @PreDestroy
    public void cleanup() {
        log.info("EmailService ExecutorService 종료 시작");
        emailExecutor.shutdown();
        try {
            if (!emailExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                emailExecutor.shutdownNow();
                log.warn("EmailService ExecutorService 강제 종료");
            }
        } catch (InterruptedException e) {
            emailExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("EmailService ExecutorService 종료 완료");
    }
}
