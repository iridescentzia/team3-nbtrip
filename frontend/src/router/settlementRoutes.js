export default [
  {
    path: '/settlement/:tripId/pending',
    name: 'SettlementPendingNotice',
    component: () =>
      import('@/pages/settlement/SettlementPendingNoticePage.vue'),
  },
  {
    path: '/settlement/:tripId/completed',
    name: 'SettlementCompletedNotice',
    component: () =>
      import('@/pages/settlement/SettlementCompletedNoticePage.vue'),
  },
  // /settlement/1, /settlement/4 처럼 동적인 tripId를 받을 수 있도록 설정합니다.
  {
    path: '/settlement/:tripId',
    name: 'settlementSummary',
    component: () => import('@/pages/settlement/SettlementSummaryPage.vue'),
  },
  {
    path: '/settlement/:tripId/request',
    name: 'settlementRequest',
    component: () => import('@/pages/settlement/SettlementRequestPage.vue'),
  },
  {
    path: '/settlement/:tripId/detail',
    name: 'settlementDetail',
    component: () => import('@/pages/settlement/SettlementDetailPage.vue'),
  },
  {
    path: '/settlement/:tripId/failure',
    name: 'SettlementFailureNotice',
    component: () =>
      import('@/pages/settlement/SettlementFailureNoticePage.vue'),
  },
  {
    path: '/settlement/explain',
    name: 'SettlementGuide',
    component: () => import('@/pages/settlement/SettlementGuidePage.vue'),
  },
];
