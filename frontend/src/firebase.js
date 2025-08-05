// src/firebase.js
// Firebase 초기화 및 FCM 토큰 발급 로직.
import { initializeApp } from "firebase/app";
import { getMessaging, getToken, onMessage } from "firebase/messaging";

const firebaseConfig = {
  apiKey: "AIzaSyDdGlnwurgQGdbDfPRbx0Gh2ZZ2G8AUBag",
  authDomain: "nbtrip-push.firebaseapp.com",
  projectId: "nbtrip-push",
  storageBucket: "nbtrip-push.firebasestorage.app",
  messagingSenderId: "312093298222",
  appId: "1:312093298222:web:0a37dc9fdf32be875819bf",
  measurementId: "G-7DYPB4TSD1"
};

// Firebase 초기화
const app = initializeApp(firebaseConfig);

// FCM 메시징 객체
const messaging = getMessaging(app);


// 브라우저에서 FCM 토큰 발급
export const requestPermissionAndGetToken = async (registration) => {
  console.log(">>> FCM 권한 요청 시도"); // 확인용 로그
  try {
    // 1. 알림 권한 요청
    const permission = await Notification.requestPermission();
    console.log(">>> 브라우저 권한 상태:", permission);
    if (permission !== 'granted') {
      console.warn('알림 권한이 거부되었습니다.');
      return null;
    }

    // 2. 토큰 발급
    const token = await getToken(messaging, {
      vapidKey: "BKLHna4RJKWaEv5iaGUoi-T9IExHNzhNdt7WLyy2cwArSy3U9ZLro1omUR6FwzpUduZM6A6_tIu-OLepc7uuvlc",
      serviceWorkerRegistration: registration
    });

    if (token) {
      console.log("FCM Token:", token);
      return token;
    } else {
      console.warn("토큰을 받을 수 없습니다. 권한을 허용했는지 확인하세요.");
    }
  } catch (error) {
    console.error("토큰 발급 오류:", error);
  }
};


// Foreground 메시지 수신 리스너
onMessage(messaging, (payload) => {
  console.log("푸시 알림 수신:", payload);
});

export default app;
