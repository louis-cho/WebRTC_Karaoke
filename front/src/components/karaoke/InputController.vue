<template>
  <!-- 캠활성화, 음소거 버튼 -->
  <div class="q-mt-md">
    <q-btn
      id="camera-activate"
      @click="handleCameraBtn"
      :color="store.camerOff ? 'negative' : 'primary'"
      label="카메라 비활성화"
    />
    <q-btn
      id="mute-activate"
      @click="handleMuteBtn"
      :color="store.muted ? 'negative' : 'primary'"
      label="음소거 활성화"
    />
  </div>
</template>

<script setup>
import { useKaraokeStore } from "@/stores/karaokeStore.js";

const store = useKaraokeStore();

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
</style>
