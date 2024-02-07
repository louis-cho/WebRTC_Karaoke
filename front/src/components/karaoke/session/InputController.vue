<template>
  <q-dialog v-model="store.toggleModals['input-controller']">
    <q-card>
      <q-card-section>
        <!-- 캠활성화, 음소거 버튼 -->
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
      </q-card-section>

      <q-card-section class="q-mt-sm q-mb-sm float-right">
        <q-btn @click="closeModal" color="negative" label="닫기" />
      </q-card-section>
    </q-card>
  </q-dialog>
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

function closeModal() {
  store.toggleModals["input-controller"] = false;
}
</script>

<style scoped></style>
