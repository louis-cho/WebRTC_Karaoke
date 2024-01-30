<template>
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
</template>

<script setup>
import { useKaraokeStore } from "@/stores/karaokeStore.js";

const store = useKaraokeStore();

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
.inline-selects {
  white-space: nowrap; /* 줄 바꿈 방지 */
}

.inline-select {
  display: inline-block;
  width: 33%; /* 적절한 너비를 조절하세요 */
  margin-right: 10px; /* 필요한 간격을 조절하세요 */
}
</style>
