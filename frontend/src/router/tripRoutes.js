export default [
  {
    path: '/trip',
    name: 'tripList',
    component: () => import('../pages/trip/TripListPage.vue'),
  },
  {
    path: '/trip/:tripId',
    name: 'tripDetail',
    component: () => import('../pages/trip/TripDetailPage.vue'),
  },
  {
    path: '/trip/:tripId/invite',
    name: 'tripEditInvite',
    component: () => import('../pages/trip/TripEditInvitePage.vue'),
  },
  {
    path: '/trip/create',
    name: 'tripCreate',
    component: () => import('../pages/trip/TripCreatePage.vue'),
  },
  {
    path: '/trip/invite',
    name: 'tripInvite',
    component: () => import('../pages/trip/TripInvitePage.vue'),
  },
  {
    path: '/trip/join/:tripId',
    name: 'tripJoin',
    component: () => import('../pages/trip/TripJoinPage.vue'),
  },
  {
    path: '/trip/join/:tripId/complete',
    name: 'tripJoinComplete',
    component: () => import('../pages/trip/TripJoinCompletePage.vue'),
  },
];
