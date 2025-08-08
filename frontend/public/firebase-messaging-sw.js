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

function mapUrlFromData(data = {}) {
  const type = data.type;
  const tripId = data.tripId;
  const paymentId = data.paymentId;

  switch (type) {
    case 'TRANSACTION':
      if (paymentId) return `/paymentlist/${tripId}`;
      return `/paymentlist/${tripId}`;
    case 'SETTLEMENT':
      return `/settlement/${tripId}/detail`;
    case 'COMPLETED':
      return `/settlement/${tripId}/completed`;
    case 'REMINDER':
      return `/settlement/${tripId}/detail`;
    case 'INVITE':
      return `/trip/join/${tripId}`;
    default:
      return '/';
  }
}

// Background push notification
messaging.onBackgroundMessage(function (payload) {
  console.log('Background push notification:', payload);
  console.log('[APP] onMessage payload.data:', payload?.data);

  const url = mapUrlFromData(payload.data || {});
  const notificationTitle = payload.notification?.title || 'NbbangTrip';
  const notificationOptions = {
    body: payload.notification?.body || 'Push notification received.',
    data: {url}
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

// 알림 클릭 → 해당 URL로 이동
self.addEventListener('notificationclick', function (event) {
  event.notification.close();
  const targetUrl = event.notification?.data?.url || '/';

  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then((windowClients) => {
      for (const client of windowClients) {
        if (client.url.startsWith(self.location.origin)) {
          client.focus();
          client.navigate(targetUrl);
          return;
        }
      }
      return clients.openWindow(targetUrl);
    })
  );
});