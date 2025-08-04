export default [
  {
    path: '/report/:tripId',
    name: 'report',
    component: () => import('@/views/report/TravelReportPage.vue'),
  },
];
