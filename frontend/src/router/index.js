import { createRouter, createWebHistory } from 'vue-router';

// 레이아웃 컴포넌트 import
import DefaultLayout from '@/components/layout/DefaultLayout.vue';

// 각 페이지 컴포넌트 import
import Home from '@/views/home/Home.vue';

// 분리된 라우트 설정 import
import paymentRoutes from './payment';
import settlementRoutes from '@/router/settlementRoutes.js';
import memberRoutes from '@/router/memberRoutes.js';

import notificationRoutes from '@/router/notificationRoutes.js';
import paymentlistRoutes from '@/router/paymentlistRoutes.js';
import reportRoutes from '@/router/reportRoutes.js';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: DefaultLayout,
      children: [
        { path: '', name: 'home', component: Home },
        ...settlementRoutes,
        ...memberRoutes,
        ...paymentRoutes,
        ...notificationRoutes,
        ...paymentlistRoutes,
        ...reportRoutes,
      ],
    },
  ],
});
export default router;

