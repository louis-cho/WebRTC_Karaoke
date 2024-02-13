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
const bundleFlag = ref(true)    // true면 위에거 업데이트, false면 아래거 업데이트
const lyricFlag = ref(true)    // true면 위에거 업데이트, false면 아래거 업데이트
const bundleIndex = ref(0)
const lyricBundleIndex = ref(0)
const lyricIndex = ref(0)
const lyricPosX = 45;  // 가사 렌더링 시작 위치 x좌표, css에 따라 수정
const lyricPosY = 70;  // 가사 렌더링 시작 위치 y좌표, css에 따라 수정
const lyricInterval = 50; // 가사 윗묶음&아랫묶음 y좌표 간격
const moveX = ref(0);       // 가사가 채워질 때 이동하는 x좌표, 초기값은 lyricPosX와 동일
const fontSize = "24px ";
const fontInterval = 24;    // 가사가 채워질 때 이동하는 x좌표 간격. 24px Arial 기준 24
const fontStyle = "Arial";
const fontColor = "black"
const filledFont = ref(""); // 부르고 있는 가사
const extraFont = ref("");  // 같은 lyricBundle 내에서 부르고 있는 가사 외 나머지 가사
const fontFillColor = "orange";
const blankSize = 6.7 // 띄어쓰기 가사가 채워질 때 이동하는 x좌표 간격. 24px Arial 기준 6.7
const blanckCount = ref(0);
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
};

const stop = () => {
  lyricUpper.value = "";
  lyricLower.value = "";
  filledFont.value = "";
  extraFont.value = "";
  hasNextLyrics.value = false;
  if(audio.value != null) {
    audio.value.currentTime = 0;
    audio.value.pause();
  }

  const ctx = canvas.value.getContext('2d');
  ctx.clearRect(0, 0, canvas.value.width, canvas.value.height);
}
/*
fontSize = 24pt면, 32~36px정도
fillText(text, x, y)는 xy 좌표 기준으로 1사분면에 렌더링
fillRect(x, y, width, height)는 xy좌표 기준 4사분면에 렌더링
*/
const drawLyrics = () => {

  startTimeRef.value = Date.now(); // 노래 시작 시간 저장.
  const renderFrame = (timestamp) => {
    const ctx = canvas.value.getContext("2d");
    ctx.clearRect(0, 0, canvas.value.width, canvas.value.height);   // 초기화

    if(hasNextLyrics.value) { // 렌더링할 가사가 있으면,

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
        ctx.fillStyle = fontColor;
        ctx.font = fontSize + fontStyle;
        ctx.fillText(countDown.value, lyricPosX-30, lyricPosY-40);
      }

      if(bundleIndex.value == 0 && (Math.abs((Date.now() - startTimeRef.value) >= (bundles.value[1].start + song.value.prelude)))) {
        // 전주 ~ 첫 마디를 벗어나면
        bundleIndex.value = 2;
      }

      if((Date.now() - startTimeRef.value) < bundles.value[1].start + song.value.prelude) {  // 첫 마디 다 부르기 전까지
        lyricUpper.value = bundles.value[bundleIndex.value].lyric;
        lyricLower.value = bundles.value[bundleIndex.value+1].lyric;
      } else if((Date.now() - startTimeRef.value) >= (bundles.value[bundleIndex.value-1].start + song.value.prelude)) {
        // 첫 줄 다 부르고 난 이후, bundles.value[2]부터 
        if(bundleFlag.value && (bundleIndex.value < bundles.value.length)) {
          lyricUpper.value = bundles.value[bundleIndex.value].lyric
          lyricLower.value = bundles.value[bundleIndex.value-1].lyric

          bundleFlag.value = !bundleFlag.value
          bundleIndex.value++;
          } else if(!bundleFlag.value && (bundleIndex.value < bundles.value.length)){
          lyricUpper.value = bundles.value[bundleIndex.value-1].lyric
          lyricLower.value = bundles.value[bundleIndex.value].lyric

          bundleFlag.value = !bundleFlag.value
          bundleIndex.value++;
        } else {  // bundleIndex.value > bundles.value.length
          console.log("checkPoint " + bundleIndex.value + " " + bundles.value.length + " " + bundleFlag.value)
          console.log(lyricUpper.value)
          console.log(lyricLower.value)
        }
      } 

      if((Date.now() - startTimeRef.value) >= lyrics.value[lyricBundleIndex.value][lyricIndex.value].start + song.value.prelude) {
        filledFont.value = "";
        extraFont.value = "";
        blanckCount.value = 0;  // 띄어쓰기 개수
        for(let i = 0; i < lyrics.value[lyricBundleIndex.value].length; i++) {
          if(i <= lyricIndex.value) {
            filledFont.value += lyrics.value[lyricBundleIndex.value][i].lyric;
            if(lyrics.value[lyricBundleIndex.value][i].lyric == ' ') blanckCount.value++;
          } else {
            extraFont.value += lyrics.value[lyricBundleIndex.value][i].lyric;
          }
        }
        
        lyricIndex.value++;

        if(lyricIndex.value >= lyrics.value[lyricBundleIndex.value].length) {  // 줄바꿈
          lyricBundleIndex.value++;
          lyricIndex.value = 0;
          lyricFlag.value = !lyricFlag.value
        }

        if(lyricBundleIndex.value == lyrics.value.length) {
          // 마지막 가사까지 다 저장했으면 업데이트 그만
          hasNextLyrics.value = false;
        }
      }
    }
    
    // 렌더링
    ctx.font = fontSize + fontStyle;
    if(bundleIndex.value == bundles.value.length) {  // 노래 마지막 부분
      if(bundleFlag.value) {  // lyricLower가 마지막 마디인 경우
        if((Date.now() - startTimeRef.value) < (bundles.value[bundles.value.length-1].start + song.value.prelude)) {  // 마지막 직전 마디 부르는 중 . . .
          ctx.fillStyle = fontFillColor;
          ctx.fillText(filledFont.value, lyricPosX, lyricPosY);
          ctx.fillStyle = fontColor;
          ctx.fillText(extraFont.value, (lyricPosX + (fontInterval * (filledFont.value.length - blanckCount.value)) + (blanckCount.value * blankSize)), lyricPosY);
          ctx.fillText(lyricLower.value, lyricPosX, lyricPosY + lyricInterval);
        } else {    // // 마지막 마디 부르는 중 . . .
          ctx.fillStyle = fontFillColor;
          ctx.fillText(lyricUpper.value, lyricPosX, lyricPosY);
          ctx.fillText(filledFont.value, lyricPosX, lyricPosY + lyricInterval);
          ctx.fillStyle = fontColor;
          ctx.fillText(extraFont.value, (lyricPosX + (fontInterval * (filledFont.value.length - blanckCount.value)) + (blanckCount.value * blankSize)), lyricPosY + lyricInterval);
        }
      } else {  // lyricUpper가 마지막 마디인 경우
        if((Date.now() - startTimeRef.value) < (bundles.value[bundles.value.length-1].start + song.value.prelude)) {
          ctx.fillStyle = fontFillColor;
          ctx.fillText(filledFont.value, lyricPosX, lyricPosY + lyricInterval);
          ctx.fillStyle = fontColor;
          ctx.fillText(extraFont.value, (lyricPosX + (fontInterval * (filledFont.value.length - blanckCount.value)) + (blanckCount.value * blankSize)), lyricPosY + lyricInterval);
          ctx.fillText(lyricUpper.value, lyricPosX, lyricPosY);
        } else {
          ctx.fillStyle = fontFillColor;
          ctx.fillText(lyricLower.value, lyricPosX, lyricPosY + lyricInterval);
          ctx.fillText(filledFont.value, lyricPosX, lyricPosY);
          ctx.fillStyle = fontColor;
          ctx.fillText(extraFont.value, (lyricPosX + (fontInterval * (filledFont.value.length - blanckCount.value)) + (blanckCount.value * blankSize)), lyricPosY);
        }
      }
    } else {  // 처음 ~ 마지막 부분 전
      if(bundleFlag.value) {
        if(filledFont.value == "" && extraFont.value == "") { // 간주 중 . . .
          ctx.fillStyle = fontColor;
          ctx.fillText(lyricUpper.value, lyricPosX, lyricPosY);
          ctx.fillText(lyricLower.value, lyricPosX, lyricPosY + lyricInterval);
        } else {  // 윗 가사 부르는 중
          ctx.fillStyle = fontFillColor;
          ctx.fillText(filledFont.value, lyricPosX, lyricPosY);
          ctx.fillStyle = fontColor;
          ctx.fillText(extraFont.value, (lyricPosX + (fontInterval * (filledFont.value.length - blanckCount.value)) + (blanckCount.value * blankSize)), lyricPosY);
          ctx.fillText(lyricLower.value, lyricPosX, lyricPosY + lyricInterval);
        }
      } else {  // 아랫 가사 부르는 중
        ctx.fillStyle = fontFillColor;
        ctx.fillText(filledFont.value, lyricPosX, lyricPosY + lyricInterval);
        ctx.fillStyle = fontColor;
        ctx.fillText(extraFont.value, (lyricPosX + (fontInterval * (filledFont.value.length - blanckCount.value)) + (blanckCount.value * blankSize)), lyricPosY + lyricInterval);
        ctx.fillText(lyricUpper.value, lyricPosX, lyricPosY);
      }
    }

    
    if((Date.now() - startTimeRef.value) >= (song.value.length*1000)) {
      // 노래 재생 시간 >= 노래 시간
      stop();
      return ;
    }

    requestAnimationFrame(renderFrame);
  };

  requestAnimationFrame(renderFrame);
};

onMounted(() => {
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
