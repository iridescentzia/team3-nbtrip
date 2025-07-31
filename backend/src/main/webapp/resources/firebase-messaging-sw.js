// firebase-messaging-sw.js
// 서비스 워커(Service Worker), 브라우저 백그라운드 푸시 알림 수신용.
importScripts('https://www.gstatic.com/firebasejs/9.6.11/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.6.11/firebase-messaging-compat.js');

// Firebase init
firebase.initializeApp({
  apiKey: "AIzaSyDdGlnwurgQGdbDfPRbx0Gh2ZZ2G8AUBag",
  authDomain: "nbtrip-push.firebaseapp.com",
  projectId: "nbtrip-push",
  storageBucket: "nbtrip-push.firebasestorage.app",
  messagingSenderId: "312093298222",
  appId: "1:312093298222:web:0a37dc9fdf32be875819bf",
  measurementId: "G-7DYPB4TSD1"
});

const messaging = firebase.messaging();

// Background push notification
messaging.onBackgroundMessage(function (payload) {
  console.log('Background push notification:', payload);

  const notificationTitle = payload.notification?.title || 'NbbangTrip';
  const notificationOptions = {
    body: payload.notification?.body || 'Push notification received.'
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

//테스트 용
self.addEventListener('push', (event) => {
  console.log('푸시 이벤트 수신 (테스트):', event);
  event.waitUntil(
    self.registration.showNotification('테스트 알림', {
      body: '서비스워커 수신 테스트',
    })
  );
});

