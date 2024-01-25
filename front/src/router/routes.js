
const routes = [
  {
    path: '/',
    component: () => import('@/pages/HomePage.vue')
  },
  {
    path: '/karaoke',
    component: () => import('@/pages/KaraokePage.vue')
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('@/pages/ErrorNotFound.vue')
  },
]

export default routes
