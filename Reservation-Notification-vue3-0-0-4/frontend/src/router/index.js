import { createRouter, createWebHashHistory } from 'vue-router';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../components/pages/Index.vue'),
    },
    {
      path: '/reservations/reservations',
      component: () => import('../components/ui/ReservationGrid.vue'),
    },
    {
      path: '/notifications/notifications',
      component: () => import('../components/ui/NotificationGrid.vue'),
    },
  ],
})

export default router;
