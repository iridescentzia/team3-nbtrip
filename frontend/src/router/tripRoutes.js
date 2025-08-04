export default [
    {
        path: '/trip',
        name: 'tripList',
        component: () => import('../views/trip/TripList.vue'),
    },
    {
        path: '/trip/:tripId',
        name: 'tripDetail',
        component: () => import('../views/trip/TripDetail.vue'),
    },
    {
        path: '/trip/create',
        name: 'tripCreate',
        component: () => import('../views/trip/TripCreate.vue'),
    },
    {
        path: '/trip/invite',
        name: 'tripInvite',
        component: () => import('../views/trip/TripInvite.vue'),
    },
    {
        path: '/trip/join/:tripId',
        name: 'tripJoin',
        component: () => import('../views/trip/TripJoin.vue'),
    },
    {
        path: '/trip/join/:tripId/complete',
        name: 'tripJoinComplete',
        component: () => import('../views/trip/TripJoinComplete.vue'),
    },
];
