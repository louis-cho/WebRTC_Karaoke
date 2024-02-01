<template>
  <div>
    <canvas ref="canvas" width="1000" height="200"></canvas>
    <div>
      <div>{{ currentTimeFormatted }}</div>
      <input type="range" :value="currentPercentage" @input="seekTo" />
      <div>{{ totalTimeFormatted }}</div>
    </div>
    <button @click="play">Play</button>
    <button @click="stop">Stop</button>
    <button @click="playNextSong" :disabled="!isPlaying">다음 곡 재생</button>
  </div>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from 'vue';

const canvas = ref(null);
const currentTime = ref(0);
const totalTime = ref(0);
const isPlaying = ref(false);
const startTimeRef = ref(0) // 노래 시작시간
const lyricUpper = ref(null)  // 위에 띄울 가사
const lyricLower = ref(null)  // 아래 띄울 가사
const flag = ref(true)    // true면 위 렌더링, false면 아래 렌더링
const bundleIndex = ref(0)
const lyricIndex = ref(0)
const posX = 100;       // 가사가 렌더링될 x축. flat
const prelude = ref(0)  // 전주 시간, 일단 추가해놓음

const play = () => {
  // 노래 재생 로직 추가
  drawLyrics()
}
const stop = () => {
  // 노래 정지 로직 추가
  const canvas = lyricsCanvas.value;
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, canvas.width, canvas.height);

}

const drawLyrics = () => {
  const text = "test";
  const ctx = canvas.value.getContext('2d');

  ctx.fillStyle = 'black';
  ctx.fillRect(0, 0, canvas.value.width, canvas.value.height);

  lyricUpper.value = bundles[0].lyric
  lyricLower.value = bundles[1].lyric

  ctx.fillStyle = 'white';
  ctx.font = '24px Arial';
  ctx.fillText(lyricUpper.value, posX, 100);
  ctx.fillText(lyricLower.value, posX, 150);
  startTimeRef.value = Date.now()

  const renderFrame = (timestamp) => {
    if((Date.now() - startTimeRef.value - prelude.value) >= lyrics[bundleIndex.value][lyricIndex.value].start) {
      console.log(Date.now() - startTimeRef.value)
      const lyricData = lyrics[bundleIndex.value][lyricIndex.value];
      const { lyric } = lyricData;
      if(lyric === ' ') { // 띄어쓰기
        console.log("띄어쓰기")
        currentIndex++;
        posX += 6.7;  // font style과 크기에 따라 다름. 모험을 통해 알아가야 함.
      } else {
        // 채우기
        ctx.fillStyle = 'yellow';
        if(flag.value) {  // 위
          ctx.fillText(lyric, posX, 100);
        } else {  // 아래
          ctx.fillText(lyric, posX, 150);
        }

        // 다음 글자로 인덱스 이동, 렌더링될 위치 이동.
        lyricIndex.value++;
        posX += 24; // x축 옮기기. 글자 크기 따라감, 아마도.
      }

      // 한 bundle 다 그렸다면 초기화
      if (lyricIndex.value >= lyrics[bundleIndex.value].length) {
        lyricIndex.value = 0;
        bundleIndex.value++;
        // ---------------------------------------------------- OUT OF INDEX
        if(flag.value) {  // true면 위에거 새로 할당하고 그리기
          lyricUpper.value = bundles[bundleIndex.value+1].lyric
          ctx.fillRect(x, y, width, height);  // 먼저 덮어씌우기
          ctx.fillText(lyricUpper.value, posX, 100);
        } else {  // 반대
          lyricLower.value = bundles[bundleIndex.value+1].lyric
          ctx.fillText(lyricLower.value, posX, 100);
        }
        flag.value = !flag.value;
      }
    }

    // 다음 프레임 요청
    requestAnimationFrame(renderFrame);
  }

  requestAnimationFrame(renderFrame);
}
// 가사 갱신을 감시하고 Canvas에 가사 다시 렌더링
// watch(lyrics, renderLyrics);

// 다음 곡 재생 버튼 클릭 핸들러
const playNextSong = () => {
  // 다음 곡 재생 로직 구현
};

// 시간 이동을 처리하는 함수
const seekTo = (event) => {
  const seekPosition = event.target.value;
  // 해당 시간으로 이동하는 로직 구현
};

// 렌더링 된 가사 및 시간 업데이트
const updateLyricsAndTime = () => {
  // 가사 및 시간 업데이트 로직 구현
};

const songLength = 61;

const bundles = [  // 띄워줄 가사
  { "length": 7058, "start": 0, "lyric": "동해물과 백두산이" },
  { "length": 7058, "start": 7058, "lyric": "마르고 닳도록" },
  { "length": 7058, "start": 14116, "lyric": "하느님이 보우하사" },
  { "length": 7058, "start": 21174, "lyric": "우리나라 만세" },
  { "length": 7058, "start": 28232, "lyric": "무궁화 삼천리" },
  { "length": 7058, "start": 35290, "lyric": "화려강산" },
  { "length": 7058, "start": 42348, "lyric": "대한사람 대한으로" },
  { "length": 7058, "start": 49406, "lyric": "길이 보전하세" }
]
const lyrics = [
  [
    { length: 882, start: 0, lyric: '동' },
    { length: 1323, start: 882, lyric: '해' },
    { length: 441, start: 2205, lyric: '물' },
    { length: 882, start: 2647, lyric: '과' },
    { length: 1, start: 2647, lyric: ' ' },
    { length: 882, start: 3529, lyric: '백' },
    { length: 882, start: 4411, lyric: '두' },
    { length: 882, start: 5294, lyric: '산' },
    { length: 882, start: 6176, lyric: '이' },
  ],
  [
    { length: 882, start: 7058, lyric: '마' },
    { length: 441, start: 7941, lyric: '르' },
    { length: 441, start: 8382, lyric: '고' },
    { length: 1, start: 2647, lyric: ' ' },
    { length: 1323, start: 8823, lyric: '닳' },
    { length: 441, start: 10147, lyric: '도' },
    { length: 3529, start: 10588, lyric: '록' },
  ],
  [
    { length: 1323, start: 14117, lyric: '하' },
    { length: 441, start: 15441, lyric: '느' },
    { length: 882, start: 15882, lyric: '님' },
    { length: 882, start: 16764, lyric: '이' },
    { length: 1, start: 2647, lyric: ' ' },
    { length: 882, start: 17647, lyric: '보' },
    { length: 882, start: 18529, lyric: '우' },
    { length: 882, start: 19411, lyric: '하' },
    { length: 882, start: 20294, lyric: '사' },
  ],
  [
    { length: 882, start: 21176, lyric: '우' },
    { length: 882, start: 22058, lyric: '리' },
    { length: 441, start: 22941, lyric: '나' },
    { length: 441, start: 23382, lyric: '라' },
    { length: 1, start: 2647, lyric: ' ' },
    { length: 882, start: 23823, lyric: '만' },
    { length: 3529, start: 24705, lyric: '세' },
  ],
  [
    { length: 1764, start: 28235, lyric: '무' },
    { length: 882, start: 30000, lyric: '궁' },
    { length: 882, start: 30882, lyric: '화' },
    { length: 1, start: 2647, lyric: ' ' },
    { length: 1764, start: 31764, lyric: '삼' },
    { length: 882, start: 33529, lyric: '천' },
    { length: 882, start: 34411, lyric: '리' },
    { length: 1, start: 2647, lyric: ' ' },
  ],
  [
    { length: 882, start: 35294, lyric: '화' },
    { length: 882, start: 36176, lyric: '려' },
    { length: 1764, start: 37058, lyric: '강' },
    { length: 3529, start: 38823, lyric: '산' },
  ],
  [
    { length: 1323, start: 42352, lyric: '대' },
    { length: 441, start: 43676, lyric: '한' },
    { length: 882, start: 44117, lyric: '사' },
    { length: 882, start: 45000, lyric: '람' },
    { length: 1, start: 2647, lyric: ' ' },
    { length: 882, start: 45882, lyric: '대' },
    { length: 882, start: 46764, lyric: '한' },
    { length: 882, start: 47647, lyric: '으' },
    { length: 882, start: 48529, lyric: '로' },
  ],
  [
    { length: 882, start: 49411, lyric: '길' },
    { length: 882, start: 50294, lyric: '이' },
    { length: 1, start: 2647, lyric: ' ' },
    { length: 441, start: 51176, lyric: '보' },
    { length: 441, start: 51617, lyric: '전' },
    { length: 882, start: 52058, lyric: '하' },
    { length: 2647, start: 52941, lyric: '세' }
  ],
]

onMounted(() => {

});

</script>

<style scoped>
/* 필요한 CSS 스타일링 작성 */
</style>
