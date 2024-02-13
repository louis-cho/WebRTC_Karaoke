<template>
  <div id="app1"></div>
</template>

<script setup>
// 아래처럼 ref를 사용하여 index.js를 가져오는 부분을 지연시킵니다.
import { ref, onMounted } from "vue";
import { parseLyric, parseBundle, parseScore } from '@/js/karaoke/karaokeParser.js'

const appInstance = ref(null);
const audio = ref(null);

onMounted(() => {
  import("@/js/perfectScore/index.js").then((module) => {
    appInstance.value = module.app;
  });
});

const props = defineProps({
  songData: Object
});

const song = ref(null)

const choose = () => {
  song.value = props.songData;
  appInstance.value.score = parseScore(song.value.score); // 퍼펙트스코어 데이터주입
  appInstance.value.songLength = song.value.length;
  appInstance.value.prelude = song.value.prelude;
  console.log((parseScore(song.value.score)));
  appInstance.value.lyrics = parseBundle(parseLyric(parseScore(song.value.score)));  // 가사 연결
  audio.value = new Audio(song.value.url); // mp3 url 연결
};

const play = () => {
  appInstance.value.playSong(); // 퍼펙트스코어 drawer 재생
  audio.value.play(); // mp3 재생
};

const stop = () => {
  appInstance.value.stopSong();
  audio.value.currentTime = 0;
  audio.value.pause();
};

defineExpose({
  play,
  stop,
  choose,
});

</script>

<style scoped>
/* 스타일링 추가 */
</style>
