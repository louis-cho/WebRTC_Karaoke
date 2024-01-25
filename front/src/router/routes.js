
const routes = [
  // {
  //   path: '/',
  //   component: () => import('layouts/MainLayout.vue'),
  //   children: [
  //     { path: '', component: () => import('pages/IndexPage.vue') }
  //   ]
  // },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/',
    component: () => import('../pages/HomePage.vue')
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('../pages/ErrorNotFound.vue')
  }
]

export default routes
