import { createRouter, createWebHistory } from 'vue-router';
// import HomeView from '../views/HomeView.vue'
import SettlementSummaryView from '../views/settlement/SettlementSummaryView.vue';
import SettlementRequestView from '../views/settlement/SettlementRequestView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // {
    //   path: '/',
    //   name: 'home',
    //   component: HomeView,
    // },
    // {
    //   path: '/about',
    //   name: 'about',
    //   // route level code-splitting
    //   // this generates a separate chunk (About.[hash].js) for this route
    //   // which is lazy-loaded when the route is visited.
    //   component: () => import('../views/AboutView.vue'),
    // },
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
  ],
});

export default router;
