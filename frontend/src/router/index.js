import { createRouter, createWebHistory } from 'vue-router';
import SettlementSummaryView from '../views/settlement/SettlementSummaryView.vue';
import SettlementRequestView from '../views/settlement/SettlementRequestView.vue';
import Home from "@/views/home/Home.vue";
import settlementRoutes from "@/router/settlementRoutes.js";
import memberRoutes from "@/router/memberRoutes.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      // /settlement/1, /settlement/4 처럼 동적인 tripId를 받을 수 있도록 설정합니다.
      path: '/settlement/:tripId',
      name: 'settlementSummary',
      component: SettlementSummaryView,
    },
    {
      path: '/settlement/:tripId/request',
      name: 'settlementRequest',
      component: SettlementRequestView,
    },
    {path: '/', name: 'home', component: Home},
      ...settlementRoutes,
      ...memberRoutes,
  ],
});

export default router;
