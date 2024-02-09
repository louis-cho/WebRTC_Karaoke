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
          {{ store.sessionName }}</q-toolbar-title
        >
        <div style="display: flex">
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
        <div style="display: flex; flex-direction: column">
          <q-btn
            @click="startSong()"
            color="positive"
            :label="pref.app.kor.karaoke.session.start"
          />
          <q-btn
            @click="stopSong()"
            color="negative"
            :label="pref.app.kor.karaoke.session.stop"
          />
          <q-btn @click="finishSong()" color="primary" label="종료" />
        </div>
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
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import pref from "@/js/config/preference.js";
import axios from "axios";

import UserVideo from "@/components/karaoke/video/UserVideo.vue";
import KaraokeChat from "@/components/karaoke/session/KaraokeChat.vue";
import InputController from "@/components/karaoke/session/InputController.vue";
import UpdateModal from "@/components/karaoke/session/UpdateModal.vue";
import ReserveModal from "@/components/karaoke/song/ReserveModal.vue";
import ReserveList from "@/components/karaoke/song/ReserveList.vue";
import NormalMode from "@/components/karaoke/NormalMode.vue";
import PerfectScore from "@/components/karaoke/PerfectScore.vue";

// store 사용
const store = useKaraokeStore();
const router = useRouter();
const songMode = ref(true);

const fileUrl = ref(undefined);
const recordingId = ref(undefined);

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

const toggleModal = (modalName) => {
  store.toggleModals[modalName] = true;
};

function startSong() {
  removeReserve()
    .then(() => {
      startRecording();
    })
    .catch((error) => {
      console.log("removeReserve 실패", error);
    });
}

function stopSong() {
  stopRecording()
    .then(() => {
      removeRecording();
    })
    .catch((error) => {
      console.log("stopRecording 실패", error);
    });
}

function finishSong() {
  stopRecording()
    .then(() => {
      uploadRecording();
    })
    .catch((error) => {
      console.log("stopRecording 실패", error);
    });
}

function removeReserve() {
  return axios
    .post(
      store.APPLICATION_SERVER_URL + "/song/start",
      {
        sessionName: store.sessionName,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then((res) => {
      console.log(res.data);
    });
}

function startRecording() {
  axios
    .post(
      store.APPLICATION_SERVER_URL + "/karaoke/recording/start",
      {
        sessionName: store.sessionName,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then((res) => {
      console.log(res.data);
      recordingId.value = res.data.id;
    });
}

function stopRecording() {
  return axios
    .post(
      store.APPLICATION_SERVER_URL + "/karaoke/recording/stop",
      {
        recordingId: recordingId.value,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then((res) => {
      console.log(res.data);
      fileUrl.value = res.data.url;
    });
}

function removeRecording() {
  axios
    .post(
      store.APPLICATION_SERVER_URL + "/karaoke/recording/delete",
      {
        recordingId: recordingId.value,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then(() => {
      fileUrl.value = undefined;
      recordingId.value = undefined;
    });
}

function uploadRecording() {
  console.log(fileUrl.value);
  axios
    .post(store.APPLICATION_SERVER_URL + "/karaoke/file/upload", {
      fileUrl: fileUrl.value,
    })
    .then((res) => {
      console.log(res.data);
      removeRecording();
    })
    .catch((error) => {
      console.error("uploadRecording 실패", error);
    });
}
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
