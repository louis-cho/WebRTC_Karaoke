import { useKaraokeStore } from "@/stores/karaokeStore";

const routes = [
  {
    path: "/",
    name: "home",
    component: () => import("@/pages/HomePage.vue"),
  },
  {
    path: "/notification",
    name: "notification",
    component: () => import("@/pages/NotificationTest.vue"),
  },
  {
    path: "/karaoke",
    component: () => import("@/pages/KaraokeListPage.vue"),
  },
  {
    path: "/point",
    component: () => import("@/pages/PointPage.vue"),
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
    path: "/feed/:userUUID",
    name: "feed",
    component: () => import("@/pages/UserFeedPage.vue"),
  },
  {
    path: "/chat/:roomPk",
    component: () => import("@/pages/ChatPage.vue"),
  },
  {
    path: "/feed_detail/:feedId",
    name: "feed_detail",
    component: () => import("@/pages/FeedDetailPage.vue"),
  },
  {
    path: "/info_edit",
    component: () => import("@/pages/InformationEditPage.vue"),
  },
  // {
  //   임시 추가
  //   path: "/item1",
  //   component: () => import("@/components/SearchUser.vue"),
  // },
  {
    path: "/friend_list",
    component: () => import("@/pages/MyFriendList.vue"),
  },
  {
    path: "/:catchAll(.*)*",
    component: () => import("@/pages/ErrorNotFound.vue"),
  },
  {
    path: "/perfect_score",
    component: () => import("@/components/karaoke/PerfectScore.vue"),
  },
  {
    path: "/normal_mode",
    component: () => import("@/components/karaoke/NormalMode.vue"),
  },
  {
    path: "/canvas_test",
    component: () => import("@/components/karaoke/CanvasTest.vue"),
  },
  {
    path: "/karaoke/:sessionId", // 동적 세션 ID
    name: "KaraokeSession",
    component: () => import("@/pages/KaraokeSessionPage.vue"),
    beforeEnter: async (to, from, next) => {
      const karaokeStore = useKaraokeStore();

      const sessionId = to.params.sessionId;
      const result = await karaokeStore.getToken(sessionId);

      if (result) {
        next();
      }
    },
  },
  {
    path: "/:catchAll(.*)*",
    component: () => import("@/pages/ErrorNotFound.vue"),
  },
];

export default routes;
