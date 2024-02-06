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
          >{{ pref.app.kor.karaokePage.sessionId }} :
          {{ store.sessionName }}</q-toolbar-title
        >
        <div style="display: flex">
          <q-btn
            @click="openModal"
            color="positive"
            :label="pref.app.kor.karaokePage.updateSession"
          />
          <q-btn
            @click="leaveSession"
            color="negative"
            :label="pref.app.kor.karaokePage.leaveSession"
          />
        </div>
      </div>
    </q-header>

    <!-- 메인 컨텐츠 영역 -->
    <q-page-container>
      <!-- 메인 캠 -->
      <div
        id="main-video"
        style="display: flex; flex-direction: row; overflow-x: auto"
      >
        <UserVideo :stream-manager="mainStreamManagerComputed" />
        <normal-mode v-if="!songMode" />
        <perfect-score v-if="songMode" />
      </div>

      <!-- 모든 캠 -->
      <div id="video-container" class="responsive-container">
        <UserVideo
          :stream-manager="publisherComputed"
          class="user-video"
          @click="updateMainVideoStreamManager(store.publisher)"
        />
        <UserVideo
          v-for="sub in subscribersComputed"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          class="user-video"
          @click="updateMainVideoStreamManager(sub)"
        />
      </div>
    </q-page-container>

    <!-- 하단 메뉴바 -->
    <q-footer>
      <q-tabs align="justify" active-color="positive" indicator-color="primary">
        <q-tab
          name="audio-filter"
          label="Audio Filter"
          @click="toggleModal('audio-filter')"
        />

        <q-tab
          name="karaoke-chat"
          label="Karaoke Chat"
          @click="toggleModal('karaoke-chat')"
        />

        <q-tab
          name="input-controller"
          label="Input Controller"
          @click="toggleModal('input-controller')"
        />

        <q-tab
          name="input-selector"
          label="Input Selector"
          @click="toggleModal('input-selector')"
        />

        <q-tab
          name="recording-video"
          label="Recording Video"
          @click="toggleModal('recording-video')"
        />
      </q-tabs>
    </q-footer>
  </q-layout>

  <audio-filter
    v-if="modals['audio-filter']"
    @close="closeModal('audio-filter')"
  />
  <karaoke-chat
    v-if="modals['karaoke-chat']"
    @close="closeModal('karaoke-chat')"
  />
  <input-controller
    v-if="modals['input-controller']"
    @close="closeModal('input-controller')"
  />
  <input-selector
    v-if="modals['input-selector']"
    @close="closeModal('input-selector')"
  />
  <recording-video
    v-if="modals['recording-video']"
    @close="closeModal('recording-video')"
  />

  <update-modal />
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

import { useKaraokeStore } from "@/stores/karaokeStore.js";
import pref from "@/js/config/preference.js";

import UserVideo from "@/components/karaoke/UserVideo.vue";
import AudioFilter from "@/components/karaoke/AudioFilter.vue";
import KaraokeChat from "@/components/karaoke/KaraokeChat.vue";
import InputController from "@/components/karaoke/InputController.vue";
import InputSelector from "@/components/karaoke/InputSelector.vue";
import RecordingVideo from "src/components/karaoke/RecordingVideo.vue";
import NormalMode from "src/components/karaoke/NormalMode.vue";
import PerfectScore from "src/components/karaoke/PerfectScore.vue";
import UpdateModal from "src/components/karaoke/UpdateModal.vue";

axios.defaults.headers.post["Content-Type"] = "application/json";

// store 사용
const store = useKaraokeStore();
const router = useRouter();
const songMode = ref(true);

// 다시그려내기 위해 computed 작성
const mainStreamManagerComputed = computed(() => store.mainStreamManager);
const publisherComputed = computed(() => store.publisher);
const subscribersComputed = computed(() => store.subscribers);

const openModal = () => {
  store.updateModal = true;
};

async function leaveSession() {
  await store.leaveSession();
  router.push("/karaoke");
}

function updateMainVideoStreamManager(stream) {
  // 주요 비디오 스트림 매니저 업데이트
  if (store.mainStreamManager === stream) return;
  store.mainStreamManager = stream;
}

const modals = {
  "audio-filter": false,
  "karaoke-chat": false,
  "input-controller": false,
  "input-selector": false,
  "recording-video": false,
};

const toggleModal = (modalName) => {
  modals[modalName] = !modals[modalName];
};

const closeModal = (modalName) => {
  modals[modalName] = false;
};
</script>

<style scoped>
.custom-header {
  height: 50px;
}
.user-video {
  cursor: pointer;
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
