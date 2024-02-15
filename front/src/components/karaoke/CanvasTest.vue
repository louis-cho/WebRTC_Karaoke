<template>
  <canvas ref="canvas" width="1000" height="200"></canvas>
</template>

<script setup>
import { ref, onMounted } from "vue";

const canvas = ref(null);

const lyrics = [
  { length: 882, start: 0, lyric: "동" },
  { length: 1323, start: 882, lyric: "해" },
  { length: 441, start: 2205, lyric: "물" },
  { length: 882, start: 2647, lyric: "과" },
  { length: 1, start: 2647, lyric: " " },
  { length: 882, start: 3529, lyric: "백" },
  { length: 882, start: 4411, lyric: "두" },
  { length: 882, start: 5294, lyric: "산" },
  { length: 882, start: 6176, lyric: "이" },
  { length: 1, start: 2647, lyric: " " },
  { length: 882, start: 7058, lyric: "마" },
  { length: 441, start: 7941, lyric: "르" },
  { length: 441, start: 8382, lyric: "고" },
  { length: 1, start: 2647, lyric: " " },
  { length: 1323, start: 8823, lyric: "닳" },
  { length: 441, start: 10147, lyric: "도" },
  { length: 3529, start: 10588, lyric: "록" },
  { length: 1, start: 2647, lyric: " " },
  { length: 1323, start: 14117, lyric: "하" },
  { length: 441, start: 15441, lyric: "느" },
  { length: 882, start: 15882, lyric: "님" },
  { length: 882, start: 16764, lyric: "이" },
  { length: 1, start: 2647, lyric: " " },
  { length: 882, start: 17647, lyric: "보" },
  { length: 882, start: 18529, lyric: "우" },
  { length: 882, start: 19411, lyric: "하" },
  { length: 882, start: 20294, lyric: "사" },
  { length: 1, start: 2647, lyric: " " },
  { length: 882, start: 21176, lyric: "우" },
  { length: 882, start: 22058, lyric: "리" },
  { length: 441, start: 22941, lyric: "나" },
  { length: 441, start: 23382, lyric: "라" },
  { length: 1, start: 2647, lyric: " " },
  { length: 882, start: 23823, lyric: "만" },
  { length: 3529, start: 24705, lyric: "세" },
  { length: 1764, start: 28235, lyric: "무" },
  { length: 882, start: 30000, lyric: "궁" },
  { length: 882, start: 30882, lyric: "화" },
  { length: 1764, start: 31764, lyric: "삼" },
  { length: 882, start: 33529, lyric: "천" },
  { length: 882, start: 34411, lyric: "리" },
  { length: 882, start: 35294, lyric: "화" },
  { length: 882, start: 36176, lyric: "려" },
  { length: 1764, start: 37058, lyric: "강" },
  { length: 3529, start: 38823, lyric: "산" },
  { length: 1323, start: 42352, lyric: "대" },
  { length: 441, start: 43676, lyric: "한" },
  { length: 882, start: 44117, lyric: "사" },
  { length: 882, start: 45000, lyric: "람" },
  { length: 882, start: 45882, lyric: "대" },
  { length: 882, start: 46764, lyric: "한" },
  { length: 882, start: 47647, lyric: "으" },
  { length: 882, start: 48529, lyric: "로" },
  { length: 882, start: 49411, lyric: "길" },
  { length: 882, start: 50294, lyric: "이" },
  { length: 441, start: 51176, lyric: "보" },
  { length: 441, start: 51617, lyric: "전" },
  { length: 882, start: 52058, lyric: "하" },
  { length: 2647, start: 52941, lyric: "세" },
];

const startTimeRef = ref(null);
const drawText = () => {
  const text =
    "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세";
  const ctx = canvas.value.getContext("2d");
  // 배경 그리기
  ctx.fillStyle = "black";
  ctx.fillRect(0, 0, canvas.value.width, canvas.value.height);

  // 텍스트 그리기
  ctx.fillStyle = "white";
  ctx.font = "24px Arial";
  ctx.fillText(text, 20, 30);
  startTimeRef.value = Date.now();
};

let posX = 20;
const drawAnimation = () => {
  if (!canvas.value) return;

  const ctx = canvas.value.getContext("2d");

  let currentIndex = 0;
  let lastRenderTime = 0;
  let prelude = ref(0); // 전주 시간
  const renderFrame = (timestamp) => {
    if (
      Date.now() - startTimeRef.value - prelude.value >=
      lyrics[currentIndex].start
    ) {
      // (현재 시간 - 노래 시작 시간)으로 가사별 index의 duration값 찾기. 전주가 있는 경우 그 시간만큼 빼주면 됨.
      const lyricData = lyrics[currentIndex];
      const { lyric } = lyricData;
      if (lyric === " ") {
        // 띄어쓰기
        currentIndex++;
        posX += 6.7; // font style과 크기에 따라 다름. 모험을 통해 알아가야 함.
      } else {
        // 렌더링
        ctx.fillStyle = "yellow";
        ctx.fillText(lyric, posX, 30);

        // 다음 글자로 인덱스 이동, 렌더링될 위치 이동.
        currentIndex++;
        posX += 24; // 글자 pt 따라감, 아마도.
      }

      // 모든 글자를 그렸다면 초기화
      if (currentIndex >= lyrics.length) {
        currentIndex = 0;
      }

      // 마지막 렌더링 시간 업데이트
      lastRenderTime = timestamp;
    }

    // 다음 프레임 요청
    requestAnimationFrame(renderFrame);
  };

  // 애니메이션 시작
  requestAnimationFrame(renderFrame);
};

onMounted(() => {
  drawText();
  drawAnimation();
});
</script>

<style scoped></style>
