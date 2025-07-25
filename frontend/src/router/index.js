import { createRouter, createWebHistory } from 'vue-router'
import Home from "@/views/home/Home.vue";
import settlementRoutes from "@/router/settlementRoutes.js";
import memberRoutes from "@/router/memberRoutes.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path: '/', name: 'home', component: Home},
      ...settlementRoutes,
      ...memberRoutes,
  ],
})

export default router