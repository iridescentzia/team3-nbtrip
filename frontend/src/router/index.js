// import { createRouter, createWebHistory } from 'vue-router';
import { createRouter, createWebHashHistory } from 'vue-router';
import paymentRoutes from './payment';
import SettlementSummaryView from '../views/settlement/SettlementSummaryView.vue';
import SettlementRequestView from '../views/settlement/SettlementRequestView.vue';
import Home from "@/views/home/Home.vue";
import settlementRoutes from "@/router/settlementRoutes.js";
import memberRoutes from "@/router/memberRoutes.js";
import notificationRoutes from "@/router/notificationRoutes.js"

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  // history: createWebHashHistory(),
  routes: [
    {path: '/', name: 'home', component: Home},
      ...settlementRoutes,
      ...memberRoutes,
      ...paymentRoutes,
      ...notificationRoutes,
  ],
});

export default router;
