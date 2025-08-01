package org.scoula.notification.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;

@Service
public class FCMService {

    // Firebase 프로젝트 ID (Firebase 콘솔 → 프로젝트 설정 → 프로젝트 ID)
    private static final String PROJECT_ID = "nbtrip-push";
    // FCM API URL (HTTP v1)
    private static final String FCM_API_URL =
            "https://fcm.googleapis.com/v1/projects/" + PROJECT_ID + "/messages:send";

    // 1) Access Token 발급
    private String getAccessToken() throws IOException {
        System.out.println(">>> AccessToken 발급 시도");

        // JSON 키 파일 확인
        if (FCMService.class.getClassLoader()
                .getResourceAsStream("firebase/nbtrip-push-adminsdk.json") == null) {
            throw new IOException("### JSON 키 파일을 찾을 수 없습니다. 경로를 확인하세요.");
        }

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(FCMService.class.getClassLoader()
                        .getResourceAsStream("firebase/nbtrip-push-adminsdk.json"))
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshIfExpired();

        String token = googleCredentials.getAccessToken().getTokenValue();
        System.out.println(">>> AccessToken 발급 성공: " + token.substring(0, 10) + "...");
        return token;
    }

    // 2) 푸시알림 보내기
    public void sendPushNotification(String targetToken, String title, String body) throws IOException {
        JsonObject message = new JsonObject();
        JsonObject notification = new JsonObject();
        notification.addProperty("title", title);
        notification.addProperty("body", body);

        JsonObject messageObj = new JsonObject();
        messageObj.add("notification", notification);
        messageObj.addProperty("token", targetToken); // 대상 디바이스 토큰

        message.add("message", messageObj);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken()); // OAuth2 토큰으로 인증

        HttpEntity<String> request = new HttpEntity<>(message.toString(), headers);
        ResponseEntity<String> response = restTemplate.postForEntity(FCM_API_URL, request, String.class);

        System.out.println("FCM Response: " + response.getBody());
    }
    public void debugKeyFile() {
        if (FCMService.class.getClassLoader()
                .getResourceAsStream("firebase/nbtrip-push-adminsdk.json") == null) {
            System.out.println("### JSON 파일을 찾지 못했습니다! 경로를 확인하세요.");
        } else {
            System.out.println("### JSON 파일을 성공적으로 찾았습니다.");
        }
    }

}