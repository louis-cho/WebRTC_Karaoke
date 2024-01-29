<template>
  <div>
      <div>
    <h3 id="mainPage_title"></h3>
    <h6 id="mainPage_content"></h6>
  </div>

    <TabItem/>

    <!-- 로그인 전에 시작하기 뜨는 navbar -->
    <TabItemStart/>
  </div>

</template>

<script setup>
import TabItemStart from "@/layouts/TabItemStart.vue";
import TabItem from "@/layouts/TabItem.vue";

import { ref, onMounted } from "vue";
import * as pref from "@/js/config/preference.js";

function init(lang) {

    document.getElementById("mainPage_title").innerHTML = pref.default.app[lang].mainPage.title;
    document.getElementById("mainPage_content").innerHTML = pref.default.app[lang].mainPage.content;
}

onMounted(() => {
  init("kor");
});

</script>

<style scoped>
</style>
