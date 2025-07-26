export default [
  {
    path: '/payment',
    name: 'payment',
    component: () => import('../views/payment/QRScanner.vue'),
  },
];
