export default [
  {
    path: '/paymentlist/:tripId',
    name: 'paymentlist',
    component: () => import('@/views/paymentlist/PaymentList.vue'),
  },
];
