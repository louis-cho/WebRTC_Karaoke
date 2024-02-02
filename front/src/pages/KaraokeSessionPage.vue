<template>
  <nav-bar />
  <div id="main-container">
    <!-- session이 true일때! 즉, 방에 들어갔을 때 -->
    <div id="session" v-if="store.session" class="q-pa-md">
      <div id="session-header" class="q-mb-md">
        <q-toolbar-title style="font-size: 40px">
          {{ pref.app.kor.karaokePage.sessionId }} : {{ store.mySessionId }}
        </q-toolbar-title>

        <q-btn
          @click="leaveSession"
          color="negative"
          :label="pref.app.kor.karaokePage.leaveSession"
        />
      </div>

      <!-- 음성 필터 -->
      <audio-filter />

      <!-- 내 캠 -->
      <div id="main-video">
        <UserVideo :stream-manager="mainStreamManagerComputed" />
      </div>
      <!-- 모든 캠 -->
      <div id="video-container">
        <UserVideo
          :stream-manager="publisherComputed"
          @click="updateMainVideoStreamManager(store.publisher)"
        />
        <UserVideo
          v-for="sub in subscribersComputed"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          @click="updateMainVideoStreamManager(sub)"
        />
      </div>

      <!-- 방에 들어갔을 때 같이 보이게 될 채팅창 -->
      <karaoke-chat />

      <!-- 캠활성화, 음소거 버튼 -->
      <input-controller />

      <!-- 캠,오디오 선택 옵션 -->
      <input-selector />

      <!-- 녹화 기능 -->
      <recording-video />
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

import { useKaraokeStore } from "@/stores/karaokeStore.js";
import pref from "@/js/config/preference.js";

import NavBar from "@/layouts/NavBar.vue";
import UserVideo from "@/components/karaoke/UserVideo.vue";
import AudioFilter from "@/components/karaoke/AudioFilter.vue";
import KaraokeChat from "@/components/karaoke/KaraokeChat.vue";
import InputController from "@/components/karaoke/InputController.vue";
import InputSelector from "@/components/karaoke/InputSelector.vue";
import RecordingVideo from "src/components/karaoke/RecordingVideo.vue";

axios.defaults.headers.post["Content-Type"] = "application/json";

// store 사용
const store = useKaraokeStore();
const router = useRouter();

// 다시그려내기 위해 computed 작성
const mainStreamManagerComputed = computed(() => store.mainStreamManager);
const publisherComputed = computed(() => store.publisher);
const subscribersComputed = computed(() => store.subscribers);

async function leaveSession() {
  await store.leaveSession();
  router.push("/karaoke");
}

function updateMainVideoStreamManager(stream) {
  // 주요 비디오 스트림 매니저 업데이트
  if (store.mainStreamManager === stream) return;
  store.mainStreamManager = stream;
}
</script>

<style scoped></style>
