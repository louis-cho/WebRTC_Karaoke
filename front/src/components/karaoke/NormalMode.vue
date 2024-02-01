<template>
  <div>
    <canvas ref="lyricsCanvas" width="910" height="174"></canvas>
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

const lyricsCanvas = ref(null);
const currentTime = ref(0);
const totalTime = ref(0);
const isPlaying = ref(false);
const lyrics = reactive([
  // 가사 데이터 예시: { time: 0, verse: '첫 번째 가사' }
]);
const testData = [
    { "length": 7058, "start": 0, "lyric": "동해물과 백두산이" },
    { "length": 7058, "start": 7058, "lyric": "마르고 닳도록" },
    { "length": 7058, "start": 14116, "lyric": "하느님이 보우하사" },
    { "length": 7058, "start": 21174, "lyric": "우리나라 만세" },
    { "length": 7058, "start": 28232, "lyric": "무궁화 삼천리" },
    { "length": 7058, "start": 35290, "lyric": "화려강산" },
    { "length": 7058, "start": 42348, "lyric": "대한사람 대한으로" },
    { "length": 7058, "start": 49406, "lyric": "길이 보전하세" }
]
const songLength = 61;

const play = () => {
  // 노래 재생 로직 추가
  renderLyrics(testData)
}
const stop = () => {
  // 노래 정지 로직 추가
  const canvas = lyricsCanvas.value;
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, canvas.width, canvas.height);
}

const flag = ref(true)

// Canvas를 통해 가사 렌더링
const renderLyrics = (testData) => {
  const canvas = lyricsCanvas.value;
  const ctx = canvas.getContext('2d');
  const startTimeRef = ref(Date.now()); // 노래를 시작한 시간
  // ctx.clearRect(0, 0, canvas.width, canvas.height); // clear
  ctx.fillStyle = 'black';
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  // 가사의 각 줄을 Canvas에 렌더링합니다.
  ctx.fillStyle = 'white'; // 가사 색상을 지정합니다.
  ctx.font = '16px Jua'; // 가사의 폰트와 크기를 지정합니다.

  let currentIndex = 0; // 현재 가사 인덱스
  let currentLength = 30; // 현재까지 표시된 가사 길이의 합
  const lineHeight = 30; // 각 가사의 높이
  let delay = 0; // 가사가 전환되는 간격 (1초)

  function displayNextLyrics() {
    ctx.fillStyle = 'black';
    ctx.fillRect(0, 0, canvas.width, canvas.height);
    if (currentIndex >= testData.length) {
      // 모든 가사를 표시한 경우 종료
      return;
    }

    // 현재 인덱스로부터 2개의 가사를 가져옴
    const lyricsToDisplay = testData.slice(currentIndex, currentIndex + 2);
    console.log(lyricsToDisplay.length)
    // 현재 가사를 캔버스에 표시
    lyricsToDisplay.forEach((lyric, index) => {
    ctx.fillStyle = 'white';
    const deltaTime = (Date.now() - startTimeRef.value) / 1000;
    console.log(deltaTime)
    if (lyric.start <= deltaTime && deltaTime <= lyric.start + lyric.length) {
      // 현재 가사의 시간 범위에 해당하는 부분을 하이라이트로 표시합니다.
      ctx.fillStyle = 'red';
    }
    ctx.fillText(lyric.lyric, 10, currentLength + index * lineHeight);
    });

    // 다음 가사로 인덱스와 길이 갱신
    currentIndex += 2;
    delay = lyricsToDisplay[0].length + lyricsToDisplay[1].length;
    // 일정 시간이 지난 후 다음 가사 표시
    setTimeout(displayNextLyrics, delay);
  }

  const currentPercentage = computed(() => (currentTime.value / totalTime.value) * 100);

  // 현재 시간을 분:초 형식으로 포맷팅
  const currentTimeFormatted = computed(() => {
    const minutes = Math.floor(currentTime.value / 60);
    const seconds = Math.floor(currentTime.value % 60);
    const formattedMinutes = String(minutes).padStart(2, '0');
    const formattedSeconds = String(seconds).padStart(2, '0');
    return `${formattedMinutes}:${formattedSeconds}`;
  });

      // 총 재생 시간을 분:초 형식으로 포맷팅
  const totalTimeFormatted = computed(() => {
    const minutes = Math.floor(totalTime.value / 60);
    const seconds = Math.floor(totalTime.value % 60);
    const formattedMinutes = String(minutes).padStart(2, '0');
    const formattedSeconds = String(seconds).padStart(2, '0');
    return `${formattedMinutes}:${formattedSeconds}`;
  });

      // 시작
    displayNextLyrics();
  };

    // 가사 갱신을 감시하고 Canvas에 가사 다시 렌더링
    watch(lyrics, renderLyrics);

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

onMounted(() => {

});
</script>

<style scoped>
/* 필요한 CSS 스타일링 작성 */
</style>
