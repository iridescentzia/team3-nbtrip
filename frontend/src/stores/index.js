import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/home/Home.vue'
import PaymentList from '@/views/paymentlist/PaymentList.vue'
// 또는 '@/components/Home.vue'

const routes = [
    {
        path: '/',
        name: 'paymentlist',
        component: PaymentList
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
