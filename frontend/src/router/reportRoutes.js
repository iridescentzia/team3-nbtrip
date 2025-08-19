export default [
  {
    path: '/report/:tripId',
    name: 'report',
    component: () => import('@/pages/report/TravelReportPage.vue'),
  },
];
