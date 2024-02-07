<template>
  <q-dialog v-model="store.toggleModals['input-selector']">
    <q-card>
      <q-card-section>
        <!-- 캠,오디오 선택 옵션 -->
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

function closeModal() {
  store.toggleModals["input-selector"] = false;
}
</script>

<style scoped></style>
