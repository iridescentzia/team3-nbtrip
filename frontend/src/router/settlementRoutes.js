export default [
  {
    path: '/settlement/pending',
    name: 'SettlementPendingNotice',
    component: () => import('@/views/settlement/SettlementPendingNotice.vue'),
  },
  {
    path: '/settlement/completed',
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
];
