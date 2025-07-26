import { createRouter, createWebHistory } from 'vue-router';
import paymentRoutes from './payment';
import SettlementSummaryView from '../views/settlement/SettlementSummaryView.vue';
import SettlementRequestView from '../views/settlement/SettlementRequestView.vue';
import Home from "@/views/home/Home.vue";
import settlementRoutes from "@/router/settlementRoutes.js";
import memberRoutes from "@/router/memberRoutes.js";


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path: '/', name: 'home', component: Home},
      ...settlementRoutes,
      ...memberRoutes,
      ...paymentRoutes,
    {
      path: '/notification',
      name: 'notification',
      component: NotificationList,
    }
  ],
});

export default router;
