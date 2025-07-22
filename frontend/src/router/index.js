import { createRouter, createWebHistory } from 'vue-router'

// 컴포넌트 import
import Home from '@/views/home/Home.vue'
import GroupList from '@/views/group/GroupList.vue'
import QRScanner from '@/views/payment/QRScanner.vue'
import MyPage from '@/views/member/MyPage.vue'

const routes = [
  {
    path: '/',
    redirect: '/home', // 기본 경로를 홈으로 리다이렉트
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
  },
  {
    path: '/group',
    name: 'GroupList',
    component: GroupList,
  },
  {
    path: '/payment',
    name: 'QRScanner',
    component: QRScanner,
  },
  {
    path: '/mypage',
    name: 'MyPage',
    component: MyPage,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
