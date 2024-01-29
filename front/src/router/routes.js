const routes = [
  {
    path: "/",
    component: () => import("@/pages/HomePage.vue"),
  },
  {
    path: "/karaoke",
    component: () => import("@/pages/KaraokePage.vue"),
    children: [
      {
        path: "/karaoke/:sessionId", // 동적 세션 ID
        name: "KaraokeSession",
        component: () => import("@/components/karaoke/KaraokeSession.vue"),
      },
    ],
  },
  {
    path: "/feed",
    component: () => import("@/pages/FeedPage.vue"),
  },
  {
    path: "/message",
    component: () => import("@/pages/MessagePage.vue"),
  },
  {
    path: "/my_profile",
    component: () => import("@/pages/MyProfilePage.vue"),
  },
  {
    path: "/:catchAll(.*)*",
    component: () => import("@/pages/ErrorNotFound.vue"),
  },
];

export default routes;
