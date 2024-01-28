
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
    path: '/feed',
    component: () => import('@/pages/FeedPage.vue')
  },
  {
    path: '/message',
    component: () => import('@/pages/MessagePage.vue')
  },
  {
    path: '/my_profile',
    component: () => import('@/pages/MyProfilePage.vue')
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('@/pages/ErrorNotFound.vue')
  },
]

export default routes
