<template>
  <div>
    <canvas ref="canvas" width="1000" height="200"></canvas>
    <button @click="play">Play</button>
    <button @click="stop">Stop</button>
    <button @click="playNextSong" :disabled="!isPlaying">다음 곡 재생</button>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from 'vue';
import { parseLyric, parseBundle, parseScore } from '@/js/karaoke/karaokeParser.js'

const canvas = ref(null);
const songLength = ref(0);  //  노래 재생 시간, 추후에 사용
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
const lyricPosX = 100;  // 가사 렌더링 시작 위치 x좌표, css에 따라 수정
const lyricPosY = 100;  // 가사 렌더링 시작 위치 y좌표, css에 따라 수정
const lyricInterval = 50; // 가사 윗묶음&아랫묶음 y좌표 간격
const eraserWidth = 300;  // 지우개 넓이, 가사 가사 길이에 따라 달라질 수도.
const eraserHeight = 34;  // 지우개 높이, 가사 font-size에 따라 달라짐
let moveX = 100;       // 가사가 채워질 때 이동하는 x좌표, 초기값은 lyricPosX와 동일
const fontsize = 24   // 가사가 채워질 때 이동하는 x좌표 간격. 모험을 통해 알아가야 함. 24pt Arial 기준 24
const blankSize = 6.7 // 띄어쓰기 가사가 채워질 때 이동하는 x좌표 간격. 모험을 통해 알아가야 함. 24pt Arial 기준 6.7
const prelude = ref(0)  // 전주 시간

const play = () => {
  lyrics.value = parseLyric(parseScore(sampleMML));
  bundles.value = parseBundle(lyrics.value)
  // 노래 재생 로직 추가
  drawLyrics()
}
const stop = () => {
  // 노래 정지 로직 추가
  const canvas = lyricsCanvas.value;
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, canvas.width, canvas.height);

}
/*
fontSize = 24pt면, 32~36px정도
fillText(text, x, y)는 xy 좌표 기준으로 1사분면에 렌더링
fillRect(x, y, width, height)는 xy좌표 기준 4사분면에 렌더링
*/
const drawLyrics = () => {
  const ctx = canvas.value.getContext('2d');

  ctx.fillStyle = 'black';
  ctx.fillRect(0, 0, canvas.value.width, canvas.value.height);

  lyricUpper.value = bundles.value[0].lyric
  lyricLower.value = bundles.value[1].lyric

  ctx.fillStyle = 'white';
  ctx.font = '24px Arial';
  ctx.fillText(lyricUpper.value, lyricPosX, lyricPosY);
  ctx.fillText(lyricLower.value, lyricPosX, lyricPosY+lyricInterval);  // 맨 처음 가사묶음 두개는 노래 시작과 동시에 렌더링
  bundleIndex.value = 2;  // index 0과 1은 미리 rendering하기 때문에 2부터 시작.

  startTimeRef.value = Date.now()   // 노래 시작 시간 저장.
  const renderFrame = (timestamp) => {
    if((Date.now() - startTimeRef.value) >= bundles.value[bundleIndex.value-1].start) { // 가사 묶음 렌더링 부분
      // 렌더링할 index-1이 시작되면 렌더링
      // 현재 bundleIndex가 가리키는 이전 묶음이 시작되면, 새로운 묶음 렌더링
      if(bundleFlag.value) { // 위에거 업데이트
        lyricUpper.value = bundles.value[bundleIndex.value].lyric
        ctx.fillStyle = 'black';
        ctx.fillRect(lyricPosX, 70, eraserWidth, eraserHeight); // 덮어씌우는 Rect의 시작 y좌표는 css하면서 수정
        ctx.fillStyle = 'white';
        ctx.font = '24px Arial';
        ctx.fillText(lyricUpper.value, lyricPosX, lyricPosY);
        bundleFlag.value = !bundleFlag.value
        bundleIndex.value++;
        // ---------------------------------------------------------------bundleIndex, OUT OF INDEX 처리 필요
      } else {
        lyricLower.value = bundles.value[bundleIndex.value].lyric
        ctx.fillStyle = 'black';
        ctx.fillRect(lyricPosX, 120, eraserWidth, eraserHeight);  // 덮어씌우는 Rect의 시작 y좌표는 css하면서 수정
        ctx.fillStyle = 'white';
        ctx.font = '24px Arial';
        ctx.fillText(lyricLower.value, lyricPosX, lyricPosY+lyricInterval);
        bundleFlag.value = !bundleFlag.value
        bundleIndex.value++;
      }
    }

    if((Date.now() - startTimeRef.value - prelude.value) >= lyrics.value[lyricBundleIndex.value][lyricIndex.value].start) { // 가사 채우는 렌더링 부분
      lyric.value = lyrics.value[lyricBundleIndex.value][lyricIndex.value].lyric
      if(lyric.value === ' ') { // 띄어쓰기
        lyricIndex.value++;
        moveX += blankSize;  // x축을 띄어쓰기 크기만큼 이동
      } else {
        // 채우기
        ctx.fillStyle = 'yellow';
        if(lyricFlag.value) {  // 위
          ctx.fillText(lyric.value, moveX, lyricPosY);
        } else {  // 아래
          ctx.fillText(lyric.value, moveX, lyricPosY+lyricInterval);
        }

        // 다음 글자로 인덱스 이동, 렌더링될 위치 이동.
        lyricIndex.value++;
        moveX += fontsize; // x축 옮기기.
      }

      if(lyricIndex.value >= lyrics.value[lyricBundleIndex.value].length) {  // 줄바꿈
        lyricBundleIndex.value++;
        lyricIndex.value = 0;
        moveX = lyricPosX;
        lyricFlag.value = !lyricFlag.value
      }
    }

    // 다음 프레임 요청
    requestAnimationFrame(renderFrame);
  }

  requestAnimationFrame(renderFrame);
}

const playNextSong = () => {
  // 다음 곡 재생 로직 구현
};

const sampleMML = `t68 o3 l4
  d'동'g.'해'f+8'물'e'과\t'g'백'd'두'c-'산'd'이\n' g'마'a8'르'b8'고\t'b+.'닳'b8'도' a2'록\n'.r
  >d.'하'c8'느'<b'님'a'이\t' g'보'f+8'우'e8d'하'c-'사\n' d'우'g'리'a8'나'a8'라\t'b'만' g2.'세\n'r
  f+.'무'g8a'궁'f+'화\t' b.'삼'>c8d'천'<b'리\n' a'화'g'려'f+'강'g a2.'산\n'r
  >d.'대'c8'한'<b'사'a'람\t' g'대'f+8'한'e8d'으'c-'로\n' d'길'g'이\t'a8'보'a8'전'b'하'g2.'세'r`;

onMounted(() => {

});

</script>

<style scoped>

</style>
