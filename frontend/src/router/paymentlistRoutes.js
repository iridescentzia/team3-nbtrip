export default [
  {
    path: '/paymentlist/:tripId',
    name: 'paymentlist',
    component: () => import('@/views/paymentlist/PaymentList.vue'),
  },
  {
    path: '/paymentlist/register',
    name: 'PaymentListRegister',
    component: () => import('@/views/paymentlist/PaymentListRegister.vue'),
  },
  {
    path: '/paymentlist/register2/:tripId',
    name: 'payment-register',
    component: () => import('@/views/paymentlist/PaymentListRegister2.vue'),
  },
  {
    path: '/paymentlist/update/:paymentId',
    name: 'PaymentListUpdate',
    component: () => import('@/views/paymentlist/PaymentListUpdate.vue'),
  },
];
