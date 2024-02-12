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
  // 이곳에서 index.js를 가져옵니다.
  import("@/js/perfectScore/index.js").then((module) => {
    appInstance.value = module.app;
  });
});

// const song = {
//   title: "애국가",
//   singer: "",
//   author: "nobody",
//   url: "https://a705.s3.ap-northeast-2.amazonaws.com/cbaf6b7b-3504-4ef4-ae79-3063301967d0",
//   score: `t69 o2 l4
//   d'동'g.'해'f+8'물'e'과\t'g'백'd'두'c-'산'd'이\n' g'마'a8'르'b8'고'b+.'닳'b8'도' a2'록'.r
//   >d.'하'c8'느'<b'님'a'이' g'보'f+8'우'e8d'하'c-'사' d'우'g'리'a8'나'a8'라'b'만' g2.'세'r
//   f+.'무'g8a'궁'f+'화' b.'삼'>c8d'천'<b'리' a'화'g'려'f+'강'g a2.'산'r
//   >d.'대'c8'한'<b'사'a'람' g'대'f+8'한'e8d'으'c-'로' d'길'g'이'a8'보'a8'전'b'하'g2.'세'r`,
//   length: 61,
// };

const song = {
      title: "응급실",
      singer: "Izi",
      author: "nobody",
      url: "https://a705.s3.ap-northeast-2.amazonaws.com/izif6b7b-3504-4ef4-ae79-3063301967d0.mp3",
      score: `t68.7 o3 l8
  <b-'후'>
  g4'회'r4 b-'하'a-16'고'g16'있'&gf'어'&
  fd'요\n'&d4r4. <g'우'>
  g'리'r4a-'다'g16'투'f16'던'&fe-'그'
  >cc'날\n'&c4r4.<b-'괜'
  >c4'한'r c'자'd'존'<b-'심'g'때'a-16'문'b-16'에\n'&
  b-4r>d-'끝'c'내'<g'자'f'고'g16'말'a-16'을\n'&
  a-4r4a-'해'b-16'버'>c16'린'&c<b-'거'&
  b-f'야\n'&f4r4.<b-'금'>

  g4'방'r4b-'볼'a-16'줄'g16'알'&gf'았'&
  fd'어\n'&d4r2
  g4'날'r4a-'찾'g16'길'f16'바'&fe-'랬'
  >cc'어\n'&c4r4.<b-'허'
  >c4'나'r c'며'd'칠'<b-'이'g'지'a-16'나'b-16'도\n'&
  b-4 r>d-'아'c'무'<g'소'f'식'g'조'
  b-'차'a-r g16'없'f16 f4'어\n' r4

  r e-'항'f'상'e-16'내'f16'게\n'&fe- r. <b-16'너'>
  a-'무'g'잘'f'해'e-'줘'&e-<b-'서\n'&b-4>
  r4 l16f'쉽'gf'게'f'생'& l8 f f'각'g'했'a-16'나'a-16'봐\n'&
  a-g&g4r2
  r e-'이'f'젠'e-16'알'f16'아\n'&fe- r. <b-16'내'>
  g'고'b-'집'b-'때'>c'문'&c<g'에\n'&g4
  b-'힘'a-'들'g'었'a-'던'&a-4 >g'너'g'를\n'&
  gf&f2r4

  <b-'이'>l16e-'바'e-'보'&e-8f'야\n'f'진'&f8g'짜'g'아'&g<b-b-'니'>c'야\n'&
  c<b-8.&b-4r2
  l8>c'아'e-'직'e-'도'f16'나'e-16'를\n'&e-4r a-'그'&
  a-g'렇'f16'게'e-16e-'몰'&e-<b-'라\n'&b-4

  >c'너'<b16'를'>c16'가'&ce-'진'f.'사'e-16e-4'랑\n'
  <b-'나'a16'밖'b-16'엔'&b->d'없'f.'는'e-16e-4'데\n'
  r2c'제'd16'발'e-16'나'&e-16f.'를\n'
  g.'떠'f16f4'나'e-.'가'd16d.'지'e-16e-2'마\n'r2

  r< e-'언'f'제'e-16'라'f16'도\n'&fe- r. <b-16'내'>
  a-'편'g'이'f'돼'e-'준'&e-<b-'너\n'&b-4>
  r4 l16f'고'gf'마'f'운'& l8 f f'준'g'모'a-16'르'a-16'고\n'&
  a-g&g4r2
  r e-'철'f'없'e-16'이'f16'나\n'&fe- r. r16
  g'멋'b-'대'b-'로'>c'한'&c<g'거\n'&g4
  b-'용'a-'서'g'할'a-'수'&a-4 >g'없'g'니\n'&
  gf&f2r4

  <b-'이'>l16e-'바'e-'보'&e-8f'야\n'f'진'&f8g'짜'g'아'&g<b-b-'니'>c'야\n'&
  c<b-8.&b-4r2
  l8>c'아'e-'직'e-'도'f16'나'e-16'를\n'&e-4r a-'그'&
  a-g'렇'f16'게'e-16e-'몰'&e-<b-'라\n'&b-4

  >c'너'<b-16'를'>c16'가'&ce-'진'f.'사'e-16e-4'랑\n'
  <b-'나'a-16'밖'b-16'엔'&b->d'없'f.'는'e-16e-4'데\n'
  r2c'제'd16'발'e-16'떠'&e-f'나'

  g4'가'a-'지'f'마\n'&f4r4
  <b-'너'>e-16'하'e-16'나'&e- f16'만\n'f16'사'&fg16'랑'g16'하'&g16<b-16b-16'는'>c16'데\n'&
  c16<b-.&b-4r2
  >c'이'e-'대'e-'로'f16'나'e-16'를\n'&e-4r a-'두'&
  l16a-gg'고'f f'가'e-e-'지'g'마\n'&gf+g8&g4
  l8 c'나'<b16'를'>c16'버'&cd'리'f.'지'e-16e-4'마\n'
  <b-'그'a16'냥'b-16'날'&b-16>b-'안'a-16 a-'아'g16g16'줘\n'&g4
  r2 c'다'd16'시'e-16'사'&e-16f.'랑'
  g.'하'f16 f4'게\n' e-.'돌'd16 d4'아'
  f'와\n'e-&e-4`,
  length: 220,
  };


const choose = () => {
  console.log("노래 예약");
  appInstance.value.score = parseScore(song.score); // 퍼펙트스코어 데이터주입
  appInstance.value.songLength = song.length;
  console.log((parseScore(song.score)));
  console.log(parseBundle(parseLyric(parseScore(song.score))));
  appInstance.value.lyrics = parseBundle(parseLyric(parseScore(song.score)));  // 가사 연결
  audio.value = new Audio(song.url); // mp3 url 연결
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

const sampleMML = `t69 o2 l4
  d'동'g.'해'f+8'물'e'과\t'g'백'd'두'c-'산'd'이\n' g'마'a8'르'b8'고\t'b+.'닳'b8'도' a2'록\n'.r
  >d.'하'c8'느'<b'님'a'이\t' g'보'f+8'우'e8d'하'c-'사\n' d'우'g'리'a8'나'a8'라\t'b'만' g2.'세\n'r
  f+.'무'g8a'궁'f+'화\t' b.'삼'>c8d'천'<b'리\n' a'화'g'려'f+'강'g a2.'산\n'r
  >d.'대'c8'한'<b'사'a'람\t' g'대'f+8'한'e8d'으'c-'로\n' d'길'g'이\t'a8'보'a8'전'b'하'g2.'세'r`;

defineExpose({
  play,
  stop,
  choose,
});

</script>

<style scoped>
/* 스타일링 추가 */
</style>
