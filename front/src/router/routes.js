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
    path: '/chat',
    component: () => import('@/pages/ChatPage.vue')
  },
  // path 추후 수정 필요
  {
    path: '/feed_detail',
    component: () => import('@/pages/FeedDetailPage.vue')
  },
  {
    path: '/:catchAll(.*)*',
    component: () => import('@/pages/ErrorNotFound.vue')
  },
  {
    path: '/perfect_score',
    component: () => import('@/components/karaoke/PerfectScore.vue')
  },
  {
    path: '/normal_mode',
    component: () => import('@/components/karaoke/NormalMode.vue')
  },
  {
    path: '/canvas_test',
    component: () => import('@/components/karaoke/CanvasTest.vue')
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
    }
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
    path: "/chat",
    component: () => import("@/pages/ChatPage.vue"),
  },
  {
    path: "/:catchAll(.*)*",
    component: () => import("@/pages/ErrorNotFound.vue"),
  },
];

export default routes;

