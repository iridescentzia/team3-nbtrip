import './assets/styles/main.css';

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

app.mount('#app');

// [1] 브라우저가 서비스워커 기능을 지원하는지 확인
if ('serviceWorker' in navigator) {
  // [2] 서비스워커 등록 시도
  navigator.serviceWorker.register('/firebase-messaging-sw.js')
    .then((registration) => {
      console.log('서비스워커 등록 성공:', registration);
    })
    .catch((error) => {
      console.error('서비스워커 등록 실패:', error);
    });
}