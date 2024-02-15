<template>
  <!-- class="bg-purple" -->
  <div id="back">
    <nav-bar />
    <q-carousel
      v-model="slide"
      vertical
      animated
      transition-prev="slide-down"
      transition-next="slide-up"
      swipeable
      navigation
      navigation-icon="radio_button_unchecked"
      padding
      arrows
      control-color="white"
      class="bg-transparent text-white rounded-borders"
    >
      <!-- shadow1 -->
      <q-carousel-slide name="style" class="column no-wrap flex-center">
        <!-- <q-icon name="style" size="56px" /> -->
        <div class="q-mt-md text-center">
          {{ lorem1 }}<br />
          {{ lorem2 }}
        </div>
      </q-carousel-slide>
      <q-carousel-slide name="tv" class="column no-wrap flex-center">
        <!-- <q-icon name="style" size="56px" /> -->
        <div class="q-mt-md text-center">
          {{ lorem3 }}<br />
          {{ lorem4 }}
        </div>
      </q-carousel-slide>
      <q-carousel-slide name="layers" class="column no-wrap flex-center">
        <!-- <q-icon name="style" size="56px" /> -->
        <div class="q-mt-md text-center">{{ lorem5 }}<br /></div>
      </q-carousel-slide>
    </q-carousel>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import StartTest from "@/components/StartTest.vue";
import NavBar from "@/layouts/NavBar.vue";
import SignIn from "@/components/SignIn.vue";

const slide = ref("style");
const lorem1 = "노래하고 싶어? 여기서 노래해방!";
const lorem2 = "어디서나 노래와 함께하는 새로운 자유, 노래해방( 解放 )";
const lorem3 = "너는 어때? 나는 어때?";
const lorem4 = "너도 나도 올려방";
const lorem5 = "스트레스 해소해방";

onMounted(() => {
  document.getElementById("back").classList.add("back");
});
</script>

<style scoped>
.carousel-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.q-carousel {
  position: relative;
}

.back {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url("@/assets/img/background.gif") center center / 100% 100%
    no-repeat;
  z-index: -1;
}

.transparent-div {
  opacity: 1; /* 0.5는 투명도를 나타내며 0에서 1 사이의 값을 사용할 수 있습니다. */
}
</style>
