import { createRouter, createWebHistory } from 'vue-router';

// 레이아웃 컴포넌트 import
import DefaultLayout from '@/components/layout/DefaultLayout.vue';

// 각 페이지 컴포넌트 import
import Home from '@/views/home/Home.vue';

// 분리된 라우트 설정 import
import settlementRoutes from '@/router/settlementRoutes.js';
import memberRoutes from '@/router/memberRoutes.js';
import paymentRoutes from './payment';
import SettlementSummaryView from '../views/settlement/SettlementSummaryView.vue';
import SettlementRequestView from '../views/settlement/SettlementRequestView.vue';
// import Home from '@/views/home/Home.vue';
// import settlementRoutes from '@/router/settlementRoutes.js';
// import memberRoutes from '@/router/memberRoutes.js';
import notificationRoutes from '@/router/notificationRoutes.js';
import paymentlistRoutes from '@/router/paymentlistRoutes.js';
import reportRoutes from '@/router/report.js';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: Home },
    ...settlementRoutes,
    ...memberRoutes,
    ...paymentRoutes,
    ...notificationRoutes,
    ...paymentlistRoutes,
    ...reportRoutes,
  ],
});

export default router;
