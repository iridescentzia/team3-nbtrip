package org.scoula.security.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.security.accounting.domain.CustomUser;
import org.scoula.security.accounting.domain.MemberVO;
import org.scoula.security.util.CryptoUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {
    private int userId;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String name;
    private String fcmToken;
    private String phoneNumber;  // 복호화된 휴대폰 번호
    private String maskedPhoneNumber;  // 마스킹된 휴대폰 번호
    private List<String> authorities;  // 권한 목록

    // MemberVO에서 UserInfoDTO 생성
    public static UserInfoDTO from(MemberVO memberVO) {
        // 권한 목록 변환
        List<String> authorities = memberVO.getAuthList() != null
                ? memberVO.getAuthList().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(java.util.stream.Collectors.toList())
                : java.util.Collections.emptyList();

        // 암호화된 휴대폰 번호 복호화
        String decryptedPhone = null;
        String maskedPhone = null;

        if (memberVO.getPhoneNumber() != null) {
            try {
                decryptedPhone = CryptoUtil.decrypt(memberVO.getPhoneNumber());
                maskedPhone = maskedPhoneNumber(decryptedPhone);
            } catch(Exception e) {
                System.err.println("휴대폰 번호 복호화 실패 : " + e.getMessage());
                maskedPhone = "010-****-****";
            }
        }

        return UserInfoDTO.builder()
                .userId(memberVO.getUserId())
                .email(memberVO.getEmail())
                .nickname(memberVO.getNickname())
                .name(memberVO.getName())
                .phoneNumber(decryptedPhone)  // 실제 휴대폰 번호
                .maskedPhoneNumber(maskedPhone)  // 마스킹된 휴대폰 번호
                .createdAt(memberVO.getCreatedAt())
                .updatedAt(memberVO.getUpdatedAt())
                .authorities(authorities)
                .build();
    }

    // 휴대폰 번호 마스킹 처리
    private static String maskedPhoneNumber(String phoneNumber) {
        if(phoneNumber == null || phoneNumber.length() != 11) {
            return "010-****-****";
        }
        String cleanNumber = phoneNumber.replaceAll("-", "");
        if(cleanNumber.length() == 11) {
            return cleanNumber.substring(0, 3) + "-****-" + cleanNumber.substring(cleanNumber.length() - 4);
        }
        return "010-****-****";
    }

    // 마스킹된 번호만 전송
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getPhoneNumber() {
        return null;
    }

    public String getDisplayPhoneNumber() {
        return maskedPhoneNumber;
    }
}
