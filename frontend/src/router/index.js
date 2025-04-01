import { createRouter, createWebHashHistory } from 'vue-router';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../components/pages/Index.vue'),
    },
    {
      path: '/orders/orders',
      component: () => import('../components/ui/OrderGrid.vue'),
    },
    {
      path: '//',
      component: () => import('../components/ui/Grid.vue'),
    },
    {
      path: '/inventories/inventories',
      component: () => import('../components/ui/InventoryGrid.vue'),
    },
    {
      path: '//',
      component: () => import('../components/ui/Grid.vue'),
    },
  ],
})

export default router;
