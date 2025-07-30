export default [
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
];
