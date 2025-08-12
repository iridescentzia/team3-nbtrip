export default [
  {
    path: '/settlement/:tripId/pending',
    name: 'SettlementPendingNotice',
    component: () => import('@/views/settlement/SettlementPendingNotice.vue'),
  },
  {
    path: '/settlement/:tripId/completed',
    name: 'SettlementCompletedNotice',
    component: () => import('@/views/settlement/SettlementCompletedNotice.vue'),
  },
  // /settlement/1, /settlement/4 처럼 동적인 tripId를 받을 수 있도록 설정합니다.
  {
    path: '/settlement/:tripId',
    name: 'settlementSummary',
    component: () => import('@/views/settlement/SettlementSummaryView.vue'),
  },
  {
    path: '/settlement/:tripId/request',
    name: 'settlementRequest',
    component: () => import('@/views/settlement/SettlementRequestView.vue'),
  },
  {
    path: '/settlement/:tripId/detail',
    name: 'settlementDetail',
    component: () => import('@/views/settlement/SettlementDetailView.vue'),
  },
  {
    path: '/settlement/:tripId/failure',
    name: 'SettlementFailureNotice',
    component: () => import('@/views/settlement/SettlementFailureNotice.vue'),
  },
  {
    path: '/settlement/explain',
    name: 'SettlementGuide',
    component: () => import('@/views/settlement/SettlementGuide.vue'),
  },
];
