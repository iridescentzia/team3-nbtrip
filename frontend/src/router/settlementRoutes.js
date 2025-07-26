export default [
    {
        path: '/settlement/pending',
        name: 'SettlementPendingNotice',
        component: () => import('@/views/settlement/SettlementPendingNotice.vue')
    },
    {
        path: '/settlement/completed',
        name: 'SettlementCompletedNotice',
        component: () => import('@/views/settlement/SettlementCompletedNotice.vue')
    }
]