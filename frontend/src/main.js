import './assets/styles/main.css';
import './firebase.js'
import { requestPermissionAndGetToken } from './firebase.js';
import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';

import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.component('VueDatePicker', VueDatePicker);

// 서비스 워커 등록
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('/firebase-messaging-sw.js')
    .then((registration) => {
      console.log('서비스 워커 등록 성공:', registration);
      requestPermissionAndGetToken(registration);
    })
    .catch((err) => {
      console.log('서비스 워커 등록 실패:', err);
    });
}

app.mount('#app');

