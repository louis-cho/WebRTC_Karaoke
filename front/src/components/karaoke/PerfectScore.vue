<template>
  <button @click="choose">노래 예약</button>
  <button @click="play">play</button>
  <button @click="stop">stop</button>
  <div id="app1"></div>
</template>

<script setup>
  // 아래처럼 ref를 사용하여 index.js를 가져오는 부분을 지연시킵니다.
  import { ref, onMounted } from "vue";

  const appInstance = ref(null);
  const audio = ref(null);

  onMounted(() => {
    // 이곳에서 index.js를 가져옵니다.
    import("@/js/perfectScore/index.js").then((module) => {
      appInstance.value = module.app;
    });
  });

  const song = {
      title: "애국가",
      singer: "",
      author: "nobody",
      score: `t68 o3 l4
  d'동'g.'해'f+8'물'e'과\t'g'백'd'두'c-'산'd'이\n' g'마'a8'르'b8'고'b+.'닳'b8'도' a2'록'.r
  >d.'하'c8'느'<b'님'a'이' g'보'f+8'우'e8d'하'c-'사' d'우'g'리'a8'나'a8'라'b'만' g2.'세'r
  f+.'무'g8a'궁'f+'화' b.'삼'>c8d'천'<b'리' a'화'g'려'f+'강'g a2.'산'r
  >d.'대'c8'한'<b'사'a'람' g'대'f+8'한'e8d'으'c-'로' d'길'g'이'a8'보'a8'전'b'하'g2.'세'r`,
    }

  //   `t68 o3 l4
  // d'동'g.'해'f+8'물'e'과 'g'백'd'두'c-'산'd'이' g'마'a8'르'b8'고'b+.'닳'b8'도' a2'록'.r
  // >d.'하'c8'느'<b'님'a'이' g'보'f+8'우'e8d'하'c-'사' d'우'g'리'a8'나'a8'라'b'만' g2.'세'r
  // f+.'무'g8a'궁'f+'화' b.'삼'>c8d'천'<b'리' a'화'g'려'f+'강'g a2.'산'r
  // >d.'대'c8'한'<b'사'a'람' g'대'f+8'한'e8d'으'c-'로' d'길'g'이'a8'보'a8'전'b'하'g2.'세'r`

  // `t54 l16 o3
  // g.a32b-.g32b-.>e-32d8r8..<g[화려한불빛으로그]
  // f#.g32a.f#32a.>e-32d4r8[뒷모습만보이며]
  // <a.b-32>c.<b-32>c.e-32d.c32r8<b-.>c32[안녕이란말도없이사라]
  // <b-8r8r16.b-32a8r4[진그대]
  // g.a32b-.g32b-.>e-32d8r..<g32[쉽게흘려진눈물눈]
  // f#.g32a.f#32a.>e-32d4r8[가에가득히고여]
  // <a.b-32>c.<b-32>c.e-32d.c32r16.c32<b-.>c32&[거리엔온통투명한유리알]
  // c.d32&d8..c32d8r8c.d32[속--그대]
  // e-.d32e-.d32e-.g32f8 r8 c.c32[따뜻한손이라도잡아]
  // d.c#32d.c#32d.<b-32g8r8a.b-32 [볼수만있었다면아직]
  // >c.<b-32>c.<b-32>c.e-32 dcr.c32<b-.>c32&[은그대의온기남아있겠지]
  // c.f32'만'd8e-32d32cd8r8 c.d32[비바]
  // e-.d32e-.d32e-.b-32&b-.>c32<a8a.g32[람이부는길가-에홀로]
  // f#.f#32f#.d32f#.a32&a.g32r8g.a32[애태우는이자리두뺨]
  // b-.g32b-.g32b-.g32b-r8>c.<b-32&[엔비바람만차게부는]
  // b-.a32&a4>d4.[데-]<
  // l8gab-b-a16g16g[사랑한단말-은]
  // f#ga&ar..a32[못해도안]
  // ab->c16.c32c<b-a&[녕이란말은해야]
  // a.b-16&b-a4r[지-]
  // gab-b-ag16f#16[아무말도없이-]
  // f#ga&ar..<a32[떠나간그]
  // >a16.a32&agf#ra16f#32g32&[대가정말미워-]
  // g16g16&g4[요] `



  const choose = () => {
    console.log("노래 예약")
    appInstance.value.songEditor.score = song.score // 퍼펙트스코어 데이터주입
    console.log(appInstance.value.songEditor.score)
    audio.value = new Audio(song.url);  // mp3 url 연결
  }

  const play = () => {
    appInstance.value.playSong(); // 퍼펙트스코어 drawer 재생
    audio.value.play(); // mp3 재생

  }

  const stop = () => {
    appInstance.value.stopSong();
    audio.value.pause();
    audio.value.currentTime = 0;
  }

</script>

<style scoped>
  /* 스타일링 추가 */
</style>
