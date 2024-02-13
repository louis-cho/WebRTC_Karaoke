<template>
  <div>
    <canvas ref="canvas" width="270" height="180"></canvas>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from "vue";
import {
  parseLyric,
  parseBundle,
  parseScore,
} from "@/js/karaoke/karaokeParser.js";

const props = defineProps({
  songData: Object,
});

const audio = ref(null);
const song = ref(null)
const hasNextLyrics = ref(false)

const canvas = ref(null);
const bundles = ref([]);
const lyrics = ref([]);
const startTimeRef = ref(0) // 노래 시작시간
const lyricUpper = ref("")  // 위에 띄울 가사
const lyricLower = ref("")  // 아래 띄울 가사
const lyric = ref("")
const bundleFlag = ref(true)    // true면 위에거 업데이트, false면 아래거 업데이트
const lyricFlag = ref(true)    // true면 위에거 업데이트, false면 아래거 업데이트
const bundleIndex = ref(0)
const lyricBundleIndex = ref(0)
const lyricIndex = ref(0)
const lyricPosX = 45;  // 가사 렌더링 시작 위치 x좌표, css에 따라 수정
const lyricPosY = 70;  // 가사 렌더링 시작 위치 y좌표, css에 따라 수정
const lyricInterval = 50; // 가사 윗묶음&아랫묶음 y좌표 간격
const eraserWidth = 210;  // 지우개 넓이, 가사 가사 길이에 따라 달라질 수도.
const eraserHeight = 34;  // 지우개 높이, 가사 font-size에 따라 달라짐
const moveX = ref(0);       // 가사가 채워질 때 이동하는 x좌표, 초기값은 lyricPosX와 동일
const fontSize = "24px ";
const fontInterval = 24;    // 가사가 채워질 때 이동하는 x좌표 간격. 모험을 통해 알아가야 함. 24pt Arial 기준 24
const fontStyle = "Arial";
const fontColor = "white"
const fontFillColor = "yellow";
const backgroundColor = "black";
const blankSize = 6.7 // 띄어쓰기 가사가 채워질 때 이동하는 x좌표 간격. 모험을 통해 알아가야 함. 24pt Arial 기준 6.7
const countDown = ref("");

const choose = () => {
  // props로 내려온 songData 주입
  console.log("노래 예약");
  song.value = props.songData;
  audio.value = new Audio(song.value.url); // mp3 url 연결
};

const play = () => {
  hasNextLyrics.value = true;
  lyrics.value = parseLyric(parseScore(song.value.score));
  bundles.value = parseBundle(lyrics.value);

  bundleIndex.value = 0;
  lyricBundleIndex.value = 0;
  lyricIndex.value = 0;
  moveX.value = lyricPosX;

  drawLyrics();
  audio.value.play(); // mp3 재생
  console.log(song.value.prelude);
  console.log(song.value.score);
};

const stop = () => {
  hasNextLyrics.value = false;
  if(audio.value != null) {
    audio.value.currentTime = 0;
    audio.value.pause();
  }

  const ctx = canvas.value.getContext('2d');
  ctx.fillStyle = backgroundColor;
  ctx.fillRect(0, 0, canvas.value.width, canvas.value.height);
}
/*
fontSize = 24pt면, 32~36px정도
fillText(text, x, y)는 xy 좌표 기준으로 1사분면에 렌더링
fillRect(x, y, width, height)는 xy좌표 기준 4사분면에 렌더링
*/
const drawLyrics = () => {
  const ctx = canvas.value.getContext("2d");

  ctx.fillStyle = backgroundColor;
  ctx.fillRect(0, 0, canvas.value.width, canvas.value.height);

  lyricUpper.value = bundles.value[0].lyric;
  lyricLower.value = bundles.value[1].lyric;

  ctx.fillStyle = fontColor;
  ctx.font = fontSize + fontStyle;
  ctx.fillText(lyricUpper.value, lyricPosX, lyricPosY);
  ctx.fillText(lyricLower.value, lyricPosX, lyricPosY + lyricInterval); // 맨 처음 가사묶음 두개는 노래 시작과 동시에 렌더링
  bundleIndex.value = 2; // index 0과 1은 미리 rendering하기 때문에 2부터 시작.

  startTimeRef.value = Date.now(); // 노래 시작 시간 저장.
  const renderFrame = (timestamp) => {
    if(hasNextLyrics.value) {
      // 카운트다운
      const beforeStart = (bundles.value[0].start + song.value.prelude) - (Date.now() - startTimeRef.value);
      if(beforeStart <= 3000 && beforeStart > 2000) {
        countDown.value = "3";
      } else if(beforeStart <= 2000 && beforeStart > 1000) {
        countDown.value = "2";
      } else if(beforeStart <= 1000 && beforeStart > 0) {
        countDown.value = "1";
      } else {
        countDown.value = "";
      }

      if(countDown.value != "") {
        ctx.fillStyle = backgroundColor;
        ctx.fillRect(lyricPosX-30, lyricPosY-65, eraserHeight, eraserHeight);

        ctx.fillStyle = fontColor;
        ctx.font = fontSize + fontStyle;
        ctx.fillText(countDown.value, lyricPosX-30, lyricPosY-40);
      } else {
        ctx.fillStyle = backgroundColor;
        ctx.fillRect(lyricPosX-30, lyricPosY-70, eraserHeight, eraserHeight);
      }

      if((Date.now() - startTimeRef.value) >= bundles.value[bundleIndex.value-1].start + song.value.prelude) { // 가사 묶음 렌더링 부분
        // 렌더링할 index-1이 시작되면 렌더링
        // 현재 bundleIndex가 가리키는 이전 묶음이 시작되면, 새로운 묶음 렌더링

        if(bundleFlag.value && bundleIndex.value < bundles.value.length) { // 위에거 업데이트
          lyricUpper.value = bundles.value[bundleIndex.value].lyric
          ctx.fillStyle = backgroundColor;
          ctx.fillRect(lyricPosX, lyricPosY-30, eraserWidth, eraserHeight); // 덮어씌우는 Rect의 시작 y좌표는 css하면서 수정
          ctx.fillStyle = fontColor;
          ctx.font = fontSize + fontStyle;
          ctx.fillText(lyricUpper.value, lyricPosX, lyricPosY);
          bundleFlag.value = !bundleFlag.value
          bundleIndex.value++;
        } else if(!bundleFlag.value && bundleIndex.value < bundles.value.length){
          lyricLower.value = bundles.value[bundleIndex.value].lyric
          ctx.fillStyle = backgroundColor;
          ctx.fillRect(lyricPosX, lyricPosY-30+lyricInterval, eraserWidth, eraserHeight);  // 덮어씌우는 Rect의 시작 y좌표는 css하면서 수정
          ctx.fillStyle = fontColor;
          ctx.font = fontSize + fontStyle;
          ctx.fillText(lyricLower.value, lyricPosX, lyricPosY+lyricInterval);
          bundleFlag.value = !bundleFlag.value
          bundleIndex.value++;
        }
      }

      if((Date.now() - startTimeRef.value) >= lyrics.value[lyricBundleIndex.value][lyricIndex.value].start + song.value.prelude) { // 가사 채우는 렌더링 부분
        lyric.value = lyrics.value[lyricBundleIndex.value][lyricIndex.value].lyric
        if(lyric.value === ' ') { // 띄어쓰기
          lyricIndex.value++;
          moveX.value += blankSize;  // x축을 띄어쓰기 크기만큼 이동
        } else {
          // 채우기
          ctx.fillStyle = fontFillColor;
          if(lyricFlag.value) {  // 위
            ctx.fillText(lyric.value, moveX.value, lyricPosY);
          } else {  // 아래
            ctx.fillText(lyric.value, moveX.value, lyricPosY+lyricInterval);
          }

          // 다음 글자로 인덱스 이동, 렌더링될 위치 이동.
          lyricIndex.value++;
          moveX.value += fontInterval; // x축 옮기기.
        }

        if(lyricIndex.value >= lyrics.value[lyricBundleIndex.value].length) {  // 줄바꿈
          lyricBundleIndex.value++;
          lyricIndex.value = 0;
          moveX.value = lyricPosX;
          lyricFlag.value = !lyricFlag.value
        }

        if(lyricBundleIndex.value == lyrics.value.length) {
          hasNextLyrics.value = false;
        }
      }
    }

    if((Date.now() - startTimeRef.value) >= (song.value.length*1000)) {
      stop();
    }

    requestAnimationFrame(renderFrame);
  };

  requestAnimationFrame(renderFrame);
};

onMounted(() => {
  const ctx = canvas.value.getContext('2d');
  ctx.fillStyle = backgroundColor;
  ctx.fillRect(0, 0, canvas.value.width, canvas.value.height);
});

defineExpose({
  play,
  stop,
  choose,
});
</script>

<style scoped>
canvas {
  width: 100%;
}
</style>
