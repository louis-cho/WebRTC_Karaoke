<template>
  <button @click="test">노래 예약</button>
  <div id="app1"></div>
</template>

<script setup>
  // 아래처럼 ref를 사용하여 index.js를 가져오는 부분을 지연시킵니다.
  import { ref, onMounted } from "vue";

  const appInstance = ref(null);

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
      score: `t60 o3 l4
  d'동'g.'해'f+8'물'e'과' g'백'd'두'c-'산'd'이' g'마'a8'르'b8'고'b+.'닳'b8'도' a2'록'.r
  >d.'하'c8'느'<b'님'a'이' g'보'f+8'우'e8d'하'c-'사' d'우'g'리'a8'나'a8'라'b'만' g2.'세'r
  f+.'무'g8a'궁'f+'화' b.'삼'>c8d'천'<b'리' a'화'g'려'f+'강'g a2.'산'r
  >d.'대'c8'한'<b'사'a'람' g'대'f+8'한'e8d'으'c-'로' d'길'g'이'a8'보'a8'전'b'하'g2.'세'r`
    }

  const test = () => {
    console.log("chk")
    appInstance.value.songEditor.score = song.score
    console.log(appInstance.value.songEditor.score)
  }

</script>

<style scoped>
  /* 스타일링 추가 */
</style>
