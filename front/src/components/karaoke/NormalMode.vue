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
import { paserLyric, parseBundle } from '@/js/karaoke/karaokeParser.js'

const canvas = ref(null);
const currentTime = ref(0);
const totalTime = ref(0);
const isPlaying = ref(false);
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
  lyrics.value = paserLyric(parsedMML);
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
  const text = "test";
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

const bundles = ref([]);

const lyrics = ref([]);

const parsedMML = [ // MML에서 ScoreParser.js로 최초로 parse된 data
{note: 2, octav: 3, length: 882.3529411764706, start: 0, lyric: '동'},
{note: 7, octav: 3, length: 1323.5294117647059, start: 882.3529411764706, lyric: '해'},
{note: 6, octav: 3, length: 441.1764705882353, start: 2205.8823529411766, lyric: '물'},
{note: 4, octav: 3, length: 882.3529411764706, start: 2647.0588235294117, lyric: '과\t'},
{note: 7, octav: 3, length: 882.3529411764706, start: 3529.4117647058824, lyric: '백'},
{note: 2, octav: 3, length: 882.3529411764706, start: 4411.764705882353, lyric: '두'},
{note: 11, octav: 2, length: 882.3529411764706, start: 5294.117647058823, lyric: '산'},
{note: 2, octav: 3, length: 882.3529411764706, start: 6176.470588235294, lyric: '이\n'},
{note: 7, octav: 3, length: 882.3529411764706, start: 7058.823529411764, lyric: '마'},
{note: 9, octav: 3, length: 441.1764705882353, start: 7941.176470588234, lyric: '르'},
{note: 11, octav: 3, length: 441.1764705882353, start: 8382.35294117647, lyric: '고\t'},
{note: 0, octav: 4, length: 1323.5294117647059, start: 8823.529411764706, lyric: '닳'},
{note: 11, octav: 3, length: 441.1764705882353, start: 10147.058823529413, lyric: '도'},
{note: 9, octav: 3, length: 2647.0588235294117, start: 10588.235294117649, lyric: '록\n'},
{note: -1, octav: 3, length: 882.3529411764706, start: 13235.29411764706},
{note: 2, octav: 4, length: 1323.5294117647059, start: 14117.64705882353, lyric: '하'},
{note: 0, octav: 4, length: 441.1764705882353, start: 15441.176470588236, lyric: '느'},
{note: 11, octav: 3, length: 882.3529411764706, start: 15882.352941176472, lyric: '님'},
{note: 9, octav: 3, length: 882.3529411764706, start: 16764.705882352944, lyric: '이\t'},
{note: 7, octav: 3, length: 882.3529411764706, start: 17647.058823529416, lyric: '보'},
{note: 6, octav: 3, length: 441.1764705882353, start: 18529.41176470589, lyric: '우'},
{note: 4, octav: 3, length: 441.1764705882353, start: 18970.588235294123},
{note: 2, octav: 3, length: 882.3529411764706, start: 19411.764705882357, lyric: '하'},
{note: 11, octav: 2, length: 882.3529411764706, start: 20294.11764705883, lyric: '사\n'},
{note: 2, octav: 3, length: 882.3529411764706, start: 21176.4705882353, lyric: '우'},
{note: 7, octav: 3, length: 882.3529411764706, start: 22058.823529411773, lyric: '리'},
{note: 9, octav: 3, length: 441.1764705882353, start: 22941.176470588245, lyric: '나'},
{note: 9, octav: 3, length: 441.1764705882353, start: 23382.35294117648, lyric: '라\t'},
{note: 11, octav: 3, length: 882.3529411764706, start: 23823.529411764714, lyric: '만'},
{note: 7, octav: 3, length: 2647.0588235294117, start: 24705.882352941186, lyric: '세\n'},
{note: -1, octav: 3, length: 882.3529411764706, start: 27352.9411764706},
{note: 6, octav: 3, length: 1323.5294117647059, start: 28235.29411764707, lyric: '무'},
{note: 7, octav: 3, length: 441.1764705882353, start: 29558.823529411777},
{note: 9, octav: 3, length: 882.3529411764706, start: 30000.00000000001, lyric: '궁'},
{note: 6, octav: 3, length: 882.3529411764706, start: 30882.352941176483, lyric: '화\t'},
{note: 11, octav: 3, length: 1323.5294117647059, start: 31764.705882352955, lyric: '삼'},
{note: 0, octav: 4, length: 441.1764705882353, start: 33088.23529411766},
{note: 2, octav: 4, length: 882.3529411764706, start: 33529.411764705896, lyric: '천'},
{note: 11, octav: 3, length: 882.3529411764706, start: 34411.764705882364, lyric: '리\n'},
{note: 9, octav: 3, length: 882.3529411764706, start: 35294.11764705883, lyric: '화'},
{note: 7, octav: 3, length: 882.3529411764706, start: 36176.4705882353, lyric: '려'},
{note: 6, octav: 3, length: 882.3529411764706, start: 37058.82352941177, lyric: '강'},
{note: 7, octav: 3, length: 882.3529411764706, start: 37941.17647058824},
{note: 9, octav: 3, length: 2647.0588235294117, start: 38823.529411764706, lyric: '산\n'},
{note: -1, octav: 3, length: 882.3529411764706, start: 41470.58823529412},
{note: 2, octav: 4, length: 1323.5294117647059, start: 42352.94117647059, lyric: '대'},
{note: 0, octav: 4, length: 441.1764705882353, start: 43676.470588235294, lyric: '한'},
{note: 11, octav: 3, length: 882.3529411764706, start: 44117.64705882353, lyric: '사'},
{note: 9, octav: 3, length: 882.3529411764706, start: 45000, lyric: '람\t'},
{note: 7, octav: 3, length: 882.3529411764706, start: 45882.35294117647, lyric: '대'},
{note: 6, octav: 3, length: 441.1764705882353, start: 46764.70588235294, lyric: '한'},
{note: 4, octav: 3, length: 441.1764705882353, start: 47205.882352941175},
{note: 2, octav: 3, length: 882.3529411764706, start: 47647.05882352941, lyric: '으'},
{note: 11, octav: 2, length: 882.3529411764706, start: 48529.41176470588, lyric: '로\n'},
{note: 2, octav: 3, length: 882.3529411764706, start: 49411.76470588235, lyric: '길'},
{note: 7, octav: 3, length: 882.3529411764706, start: 50294.11764705882, lyric: '이\t'},
{note: 9, octav: 3, length: 441.1764705882353, start: 51176.47058823529, lyric: '보'},
{note: 9, octav: 3, length: 441.1764705882353, start: 51617.647058823524, lyric: '전'},
{note: 11, octav: 3, length: 882.3529411764706, start: 52058.82352941176, lyric: '하'},
{note: 7, octav: 3, length: 2647.0588235294117, start: 52941.17647058823, lyric: '세'},
{note: -1, octav: 3, length: 882.3529411764706, start: 55588.23529411764},
]

onMounted(() => {

});

</script>

<style scoped>
/* 필요한 CSS 스타일링 작성 */
</style>
