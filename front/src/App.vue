<!--
<template>
  <div>
    <router-view />
    <div id="app1"></div>
  </div>
</template>

<script setup>
  import { app } from "./index.js";
</script>


<style scoped>
</style>
-->
<template>
  <div>
    <router-view />
    <div id="app1"></div>
  </div>
</template>

<script setup>
  // 아래처럼 ref를 사용하여 index.js를 가져오는 부분을 지연시킵니다.
  import { ref, onMounted } from "vue";

  const appInstance = ref(null);

  onMounted(() => {
    // 이곳에서 index.js를 가져옵니다.
    import("./index.js").then((module) => {
      appInstance.value = module.app;
    });
  });
</script>

<style scoped>
  /* 스타일링 추가 */
</style>
