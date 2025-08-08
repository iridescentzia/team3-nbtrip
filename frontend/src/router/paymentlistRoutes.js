export default [
  {
    path: '/paymentlist/:tripId',
    name: 'paymentlist',
    component: () => import('@/views/paymentlist/PaymentList.vue'),
  },
  {
    path: '/paymentlist/prepaidRegister/:tripId',
    name: 'payment-register-prepaid',
    component: () => import('@/views/paymentlist/PaymentPrepaidRegister.vue'),
  },
  {
    path: '/paymentlist/otherRegister/:tripId',
    name: 'payment-register-other',
    component: () => import('@/views/paymentlist/PaymentListOtherRegister.vue'),
  },
  {
    path: '/paymentlist/update/:paymentId',
    name: 'PaymentListUpdate',
    component: () => import('@/views/paymentlist/PaymentListUpdate.vue'),
  },
];
