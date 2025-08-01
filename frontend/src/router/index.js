import { createRouter, createWebHistory } from 'vue-router';

// 레이아웃 컴포넌트 import
import DefaultLayout from '@/components/layout/DefaultLayout.vue';

// 각 페이지 컴포넌트 import
import Home from '@/views/home/Home.vue';

// 분리된 라우트 설정 import
import settlementRoutes from '@/router/settlementRoutes.js';
import memberRoutes from '@/router/memberRoutes.js';
import paymentRoutes from './payment';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: DefaultLayout, // DefaultLayout을 부모로 설정
      children: [
        { path: '', name: 'home', component: Home }, // 예: http://localhost:5173/
        // --- 여기에 DefaultLayout을 사용할 페이지들을 추가 ---
        ...settlementRoutes,
        ...memberRoutes,
        ...paymentRoutes,
      ],
    },
    {
      // --- DefaultLayout을 사용하지 않을 페이지는 별도로 등록 ---
    },
  ],
});

export default router;
