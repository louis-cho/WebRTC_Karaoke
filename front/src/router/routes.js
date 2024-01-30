import { useKaraokeStore } from "@/stores/karaokeStore";

const routes = [
  {
    path: "/",
    name: "home",
    component: () => import("@/pages/HomePage.vue"),
  },
  {
    path: "/karaoke",
    component: () => import("@/pages/KaraokeListPage.vue"),
  },
  {
    path: "/karaoke/:sessionId", // 동적 세션 ID
    name: "KaraokeSession",
    component: () => import("@/pages/KaraokeSessionPage.vue"),
    beforeEnter: async (to, from, next) => {
      // useKaraokeStore 인스턴스 생성
      const karaokeStore = useKaraokeStore();

      // URL 파라미터에서 sessionId 추출
      const sessionId = to.params.sessionId;

      // joinSession에 sessionId를 전달하여 호출
      await karaokeStore.joinSession(sessionId);

      // 특정 페이지로 이동
      next();
    },
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
