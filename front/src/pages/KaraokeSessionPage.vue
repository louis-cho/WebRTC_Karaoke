<template>
  <q-layout view="lHh Lpr lFf" v-if="store.session">
    <!-- 상단 AppBar -->
    <q-header elevated class="custom-header">
      <div
        id="session-header"
        class="q-mb-md"
        style="
          display: flex;
          align-items: center;
          justify-content: space-between;
        "
      >
        <q-toolbar-title style="font-size: 40px"
          >{{ pref.app.kor.karaoke.list.sessionId }} :
          {{ decodeBase64(store.sessionName) }}</q-toolbar-title
        >
        <div style="display: flex">
          <q-btn
            @click="openInviteModal"
            color="black"
            :label="pref.app.kor.karaoke.session.invite"
          />
          <q-btn
            @click="openModal"
            color="positive"
            :label="pref.app.kor.karaoke.session.setting"
          />
          <q-btn
            @click="leaveSession"
            color="negative"
            :label="pref.app.kor.karaoke.session.leave"
          />
        </div>
      </div>
    </q-header>

    <!-- 메인 컨텐츠 영역 -->
    <q-page-container class="q-pa-md">
      <!-- 메인 캠 -->
      <div
        id="main-video"
        style="display: flex; flex-direction: row; overflow-x: auto"
      >
        <UserVideo :stream-manager="mainStreamManagerComputed" />

        <song-controller />
      </div>

      <!-- 모든 캠 -->
      <div id="video-container" class="responsive-container">
        <UserVideo :stream-manager="publisherComputed" class="user-video" />
        <UserVideo
          v-for="sub in subscribersComputed"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          class="user-video"
        />
      </div>
    </q-page-container>

    <!-- 하단 메뉴바 -->
    <q-footer>
      <q-tabs align="justify" active-color="positive" indicator-color="primary">
        <q-tab
          name="karaoke-chat"
          :label="pref.app.kor.karaoke.session.chatting"
          @click="toggleModal('karaoke-chat')"
        />

        <q-tab
          name="input-controller"
          :label="pref.app.kor.karaoke.session.input"
          @click="toggleModal('input-controller')"
        />

        <q-tab
          name="reserve-song"
          :label="pref.app.kor.karaoke.session.reserve"
          @click="toggleModal('reserve-song')"
        />

        <q-tab
          name="reserve-list"
          :label="pref.app.kor.karaoke.session.reserveList"
          @click="toggleModal('reserve-list')"
        />
      </q-tabs>
    </q-footer>
  </q-layout>

  <karaoke-chat />
  <input-controller />
  <recording-video />
  <reserve-modal />
  <reserve-list />

  <update-modal />
  <invite-modal :sessionName="store.sessionName" />
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import { useNotificationStore } from "@/stores/notificationStore.js";
import pref from "@/js/config/preference.js";

import UserVideo from "@/components/karaoke/video/UserVideo.vue";
import KaraokeChat from "@/components/karaoke/session/KaraokeChat.vue";
import InputController from "@/components/karaoke/session/InputController.vue";
import UpdateModal from "@/components/karaoke/session/UpdateModal.vue";
import InviteModal from "@/components/karaoke/session/InviteModal.vue";
import ReserveModal from "@/components/karaoke/song/ReserveModal.vue";
import ReserveList from "@/components/karaoke/song/ReserveList.vue";
import SongController from "@/components/karaoke/song/SongController.vue";

const store = useKaraokeStore();
const router = useRouter();

// 다시그려내기 위해 computed 작성
const mainStreamManagerComputed = computed(() => store.mainStreamManager);
const publisherComputed = computed(() => store.publisher);
const subscribersComputed = computed(() => store.subscribers);

//notificationStore 사용
const notificationStore = useNotificationStore();

const openModal = () => {
  store.updateModal = true;
};

const openInviteModal = () => {
  notificationStore.inviteModal = true;
};

async function leaveSession() {
  await store.leaveSession();
  router.push("/karaoke");
}

const toggleModal = (modalName) => {
  store.toggleModals[modalName] = true;
};

function decodeBase64(encodedString) {
  return decodeURIComponent(escape(atob(encodedString)));
}

// document.addEventListener("keydown", function (e) {
//   if ((e.ctrlKey && (e.key === "r" || e.key === "n")) || e.key === "F5") {
//     e.preventDefault();
//     alert("새로고침을 할 수 없습니다.");
//   }
// });

window.addEventListener("beforeunload", function (e) {
  e.preventDefault();
  e.returnValue = ""; // 표준 이벤트 속성, 오래된 브라우저에서도 동작합니다.
  alert("새로고침을 할 수 없습니다.");
});
</script>

<style scoped>
.custom-header {
  height: 50px;
}

#video-container {
  display: flex;
  flex-direction: row;
  overflow-x: auto;
}

/* 추가한 클래스로 반응형 스타일을 지정합니다. */
.responsive-container {
  flex-wrap: nowrap; /* 자식 요소들이 한 줄에 나오도록 설정 */
}

/* 미디어 쿼리를 사용하여 페이지 크기에 따라 스타일을 동적으로 조절합니다. */
@media (max-width: 768px) {
  .responsive-container {
    flex-wrap: wrap; /* 페이지 크기가 작을 때는 요소들이 여러 줄에 나오도록 설정 */
  }
}
</style>
