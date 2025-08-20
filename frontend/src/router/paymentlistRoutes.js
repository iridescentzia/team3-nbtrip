export default [
  {
    path: '/paymentlist/:tripId',
    name: 'paymentlist',
    component: () => import('@/pages/paymentlist/PaymentListPage.vue'),
  },
  {
    path: '/paymentlist/prepaidRegister/:tripId',
    name: 'payment-register-prepaid',
    component: () =>
      import('@/pages/paymentlist/PaymentPrepaidRegisterPage.vue'),
  },
  {
    path: '/paymentlist/otherRegister/:tripId',
    name: 'payment-register-other',
    component: () =>
      import('@/pages/paymentlist/PaymentListOtherRegisterPage.vue'),
  },
  {
    path: '/paymentlist/update/:paymentId',
    name: 'PaymentListUpdate',
    component: () => import('@/pages/paymentlist/PaymentListUpdatePage.vue'),
  },
];
