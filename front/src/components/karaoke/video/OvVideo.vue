<template>
  <video ref="videoEl" autoplay />
</template>

<script setup>
import { ref, onMounted, watch } from "vue";

const props = defineProps({
  streamManager: Object,
});

const videoEl = ref(null);

// mounted되면 videoEl의 값을 addVideoElement에 추가함.
onMounted(() => {
  props.streamManager.addVideoElement(videoEl.value);
});

watch(
  () => props.streamManager,
  (newStreamManager) => {
    newStreamManager.addVideoElement(videoEl.value);
  },
  { deep: true }
);
</script>
