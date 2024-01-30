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
        <audio-filter :publisher="publisher" />
      </div>

      <!-- 내 캠 -->
      <div id="main-video">
        <UserVideo :stream-manager="mainStreamManagerComputed" />
      </div>
      <!-- 모든 캠 -->
      <div id="video-container">
        <UserVideo
          :stream-manager="publisherComputed"
          @click="updateMainVideoStreamManager(publisher)"
        />
        <UserVideo
          v-for="sub in subscribersComputed"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          @click="updateMainVideoStreamManager(sub)"
        />
      </div>
      <!-- 방에 들어갔을 때 같이 보이게 될 채팅창 -->
      <div id="chat-container" class="outer-border q-pa-md">
        <q-card class="q-mb-md chat-window">
          <q-card-section class="q-pa-md dark-bg">
            <div class="chat-title q-mb-md">
              <h2 class="q-mb-none title-font-size">채팅</h2>
              <div class="section-divider"></div>
            </div>
            <ul class="chat-history q-mb-md">
              <li
                v-for="(message, index) in messages"
                :key="index"
                class="message-item q-py-md dark-item-bg"
              >
                <strong class="message-username dark-text"
                  >{{ message.username }}:</strong
                >
                {{ message.message }}
              </li>
            </ul>
          </q-card-section>
        </q-card>

        <form id="chat-write" class="q-mt-md">
          <q-input
            type="text"
            placeholder="전달할 내용을 입력하세요."
            v-model="inputMessage"
            outlined
            dense
            class="inline-input"
          />
          <q-btn @click="sendMessage" color="primary" label="전송" />
        </form>
      </div>

      <!-- 캠활성화, 음소거 버튼 -->
      <div class="q-mt-md">
        <q-btn
          id="camera-activate"
          @click="handleCameraBtn"
          color="primary"
          label="캠 비활성화"
        />
        <q-btn
          id="mute-activate"
          @click="handleMuteBtn"
          color="primary"
          label="음소거 활성화"
        />
      </div>

      <!-- 캠,오디오 선택 옵션 -->
      <div class="inline-selects">
        <q-select
          v-model="store.selectedCamera"
          @update:model-value="handleCameraChange"
          label="사용할 카메라를 선택하세요"
          outlined
          :options="store.cameras"
          emit-value
          map-options
          option-value="deviceId"
          option-label="label"
          class="inline-select"
        />
        <q-select
          v-model="store.selectedAudio"
          @update:model-value="handleAudioChange"
          label="사용할 마이크를 선택하세요"
          outlined
          :options="store.audios"
          emit-value
          map-options
          option-value="deviceId"
          option-label="label"
          class="inline-select"
        />
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { OpenVidu } from "openvidu-browser";
import pref from "@/js/config/preference.js";
import UserVideo from "@/components/karaoke/UserVideo.vue";
import NavBar from "@/layouts/NavBar.vue";
import AudioFilter from "src/components/karaoke/AudioFilter.vue";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

axios.defaults.headers.post["Content-Type"] = "application/json";

// store 사용
const store = useKaraokeStore();
const router = useRouter();

// 다시그려내기 위해 computed 작성
const mainStreamManagerComputed = computed(() => store.mainStreamManager);
const publisherComputed = computed(() => store.publisher);
const subscribersComputed = computed(() => store.subscribers);

function updateMainVideoStreamManager(stream) {
  // 주요 비디오 스트림 매니저 업데이트
  if (store.mainStreamManager === stream) return;
  store.mainStreamManager = stream;
}

// 채팅창 구현을 위한 함수 제작
function sendMessage(event) {
  event.preventDefault();
  if (store.inputMessage.trim()) {
    // 다른 참가원에게 메시지 전송하기
    store.session.signal({
      data: JSON.stringify({
        username: store.myUserName,
        message: store.inputMessage,
      }), // 메시지 데이터를 문자열로 변환해서 전송
      type: "chat", // 신호 타입을 'chat'으로 설정
    });
    store.inputMessage = "";
  }
}

// 음소거, 캠 활성화 버튼 작동
function handleCameraBtn() {
  if (!store.publisher) return;
  // 카메라 상태 토글
  store.camerOff = !store.camerOff;
  const cameraActivate = document.getElementById("camera-activate");
  if (store.camerOff) {
    //카메라 비활성화상태
    cameraActivate.innerText = "카메라 활성화";
  } else {
    //카메라 활성화상태
    cameraActivate.innerText = "카메라 비활성화";
  }

  // 카메라 작동 상태를 적용
  store.publisher.publishVideo(!store.camerOff);
}

function handleMuteBtn() {
  if (!store.publisher) return;

  // 음소거 상태 토글
  store.muted = !store.muted;
  const muteActivate = document.getElementById("mute-activate");
  if (store.muted) {
    //음소거 활성화상태
    muteActivate.innerText = "음소거 비활성화";
  } else {
    //음소거 비활성화상태
    muteActivate.innerText = "음소거 활성화";
  }
  // 음소거 설정을 적용
  store.publisher.publishAudio(!store.muted);
}

// select태그에서 사용할 기기를 선택했을때
async function handleCameraChange(event) {
  store.selectedCamera = event;
  await replaceCameraTrack(store.selectedCamera);
}

async function handleAudioChange(event) {
  store.selectedAudio = event;
  await replaceAudioTrack(store.selectedAudio);
}

async function replaceCameraTrack(deviceId) {
  if (!store.publisher) return;

  const newConstraints = {
    audio: false,
    video: {
      deviceId: { exact: deviceId },
    },
  };

  try {
    const newStream = await navigator.mediaDevices.getUserMedia(newConstraints);
    const newVideoTrack = newStream.getVideoTracks()[0];
    await store.publisher.replaceTrack(newVideoTrack);
  } catch (error) {
    console.error("Error replacing video track:", error);
  }
}

async function replaceAudioTrack(deviceId) {
  if (!store.publisher) return;

  const newConstraints = {
    audio: {
      deviceId: { exact: deviceId },
    },
    video: false,
  };

  try {
    const newStream = await navigator.mediaDevices.getUserMedia(newConstraints);
    const newAudioTrack = newStream.getAudioTracks()[0];
    await store.publisher.replaceTrack(newAudioTrack);
  } catch (error) {
    console.error("Error replacing audio track:", error);
  }
}
</script>

<style scoped>
.outer-border {
  border: 2px solid #3498db; /* 테두리 색상을 변경하세요 */
  border-radius: 10px; /* 라운드 코너를 적용하세요 */
  padding: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 그림자 효과를 추가하세요 */
}
.chat-window {
  background-color: #2c3e50; /* 어두운 배경색 */
  border-radius: 8px;
  color: #ecf0f1; /* 전체 텍스트 색상 */
}
/* 제목 스타일링을 위한 CSS */
.chat-title {
  text-align: left;
  margin-bottom: 20px;
}
/* 제목 폰트 크기 조절을 위한 CSS */
.title-font-size {
  font-size: 2rem; /* 원하는 크기로 조절하세요 */
}
/* 제목 밑에 줄을 추가하는 CSS */
.section-divider {
  border-bottom: 1px solid #ccc; /* 줄의 스타일 및 색상을 원하는대로 조절하세요 */
  margin-bottom: 10px; /* 원하는 간격으로 조절하세요 */
}
.dark-bg {
  background-color: #2c3e50 !important; /* !important로 우선순위 부여 */
}

.chat-history {
  list-style-type: none;
  padding: 0;
}

.message-item {
  background-color: #34495e; /* 어두운 메시지 배경색 */
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 12px;
  margin-bottom: 10px;
}

.dark-item-bg {
  background-color: #34495e !important; /* !important로 우선순위 부여 */
}

.message-username {
  font-weight: bold;
  color: #3498db; /* 사용자 이름 색상 */
}

.dark-text {
  color: #3498db !important; /* !important로 우선순위 부여 */
}
.inline-input {
  display: inline-block;
  width: calc(100% - 80px); /* 적절한 너비를 조절하세요 */
  margin-right: 10px; /* 필요한 간격을 조절하세요 */
}

.inline-selects {
  white-space: nowrap; /* 줄 바꿈 방지 */
}

.inline-select {
  display: inline-block;
  width: 33%; /* 적절한 너비를 조절하세요 */
  margin-right: 10px; /* 필요한 간격을 조절하세요 */
}
</style>
