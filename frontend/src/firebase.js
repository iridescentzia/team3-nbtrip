// src/firebase.js
// Firebase 초기화 및 FCM 토큰 발급 로직.
import { initializeApp } from "firebase/app";
import { getMessaging, getToken, onMessage } from "firebase/messaging";
import router from "@/router";

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

// SW와 동일 규칙의 매핑 함수
function mapUrlFromData(data = {}) {
  const type = data.type;
  const tripId = data.tripId;
  const paymentId = data.paymentId;

  switch (type) {
    case 'TRANSACTION':
      if (paymentId) return `/trip/${tripId}`;
      return `/trip/${tripId}`;
    case 'SETTLEMENT':
      return `/settlement/${tripId}/detail`;
    case 'COMPLETED':
      return `/settlement/${tripId}/completed`;
    case 'REMINDER':
      return `/settlement/${tripId}/detail`;
    case 'INVITE':
      return `/trip/join/${tripId}`;
    case 'JOIN':
    case 'LEFT':
      return `/trip/${tripId}`;
    default:
      return '/';
  }
}

const canUseFCM =
    typeof window !== "undefined" &&
    window.isSecureContext &&
    "serviceWorker" in navigator &&
    "Notification" in window &&
    "PushManager" in window;

let messaging = null;
if (canUseFCM) {
  messaging = getMessaging(app);

  onMessage(messaging, (payload) => {
    console.log("푸시 알림 수신:", payload);
    const url = mapUrlFromData(payload.data || {});
    if (!url) return;

    if (Notification.permission === "granted" && payload.notification) {
      const { title, body } = payload.notification;
      const n = new Notification(title, { body });
      n.onclick = () => {
        window.focus();
        router.push(url);
        n.close();
      };
    }
  });
} else {
  console.info("[FCM] 비지원 환경 → FCM 건너뜀(앱은 정상 렌더)");
}

// 브라우저에서 FCM 토큰 발급
export const requestPermissionAndGetToken = async (registration) => {
  if (!canUseFCM || !messaging) {
    console.warn("[FCM] 지원되지 않는 환경");
    return null;
  }

  try {
    const permission = await Notification.requestPermission();
    if (permission !== "granted") {
      console.warn("알림 권한이 거부되었습니다.");
      return null;
    }

    return await getToken(messaging, {
      vapidKey: "BKLHna4RJKWaEv5iaGUoi-T9IExHNzhNdt7WLyy2cwArSy3U9ZLro1omUR6FwzpUduZM6A6_tIu-OLepc7uuvlc",
      serviceWorkerRegistration: registration,
    });
  } catch (error) {
    console.error("토큰 발급 오류:", error);
    return null;
  }
};

export default app;