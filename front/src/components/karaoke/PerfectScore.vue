<template>
  <div id="app1"></div>
</template>

<script setup>
// 아래처럼 ref를 사용하여 index.js를 가져오는 부분을 지연시킵니다.
import { ref, onMounted, watch } from "vue";
import {
  parseLyric,
  parseBundle,
  parseScore,
} from "@/js/karaoke/karaokeParser.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import { initializeApp, getAppInstance } from "@/js/perfectScore/index.js";

const store = useKaraokeStore();

const appInstance = ref(null);
const audio = ref(null);

onMounted(async () => {
  const appContainer = document.getElementById("app1");
  initializeApp(appContainer);

  appInstance.value = getAppInstance();
});

const props = defineProps({
  songData: Object,
});

const song = ref(null);

const choose = () => {
  song.value = props.songData;
  appInstance.value.score = parseScore(song.value.mmlData); // 퍼펙트스코어 데이터주입
  appInstance.value.songLength = song.value.length;
  appInstance.value.prelude = song.value.prelude;
  appInstance.value.lyrics = parseBundle(
    parseLyric(parseScore(song.value.mmlData))
  ); // 가사 연결
  audio.value = new Audio(song.value.songUrl); // mp3 url 연결
};

const play = () => {
  appInstance.value.playSong(); // 퍼펙트스코어 drawer 재생
  audio.value.play(); // mp3 재생
};

const stop = () => {
  appInstance.value.stopSong();
  song.value = null;
  if(audio.value != null) {
    audio.value.currentTime = 0;
    audio.value.pause();
  }
};

defineExpose({
  play,
  stop,
  choose,
});
</script>

<style scoped>
#app1 {
  width: 1000px;
  height: 360px;
}
</style>
