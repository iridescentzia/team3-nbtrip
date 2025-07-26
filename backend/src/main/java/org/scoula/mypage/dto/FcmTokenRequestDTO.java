package org.scoula.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

// PUT /api/users/fcm-token
public class FcmTokenRequestDTO {
    private String fcmToken;
}
