<template>
  <!-- class="bg-purple" -->
  <div>

    <TabItem/>
    <TabItemStart/>
    <!-- <SignIn /> -->
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
      class="bg-purple text-white rounded-borders"
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

<script>
import { ref } from "vue";
import SignIn from "src/components/SignIn.vue";
import TabItemStart from "src/components/TabItemStart.vue"
import TabItem from "src/components/TabItem.vue";
import { defineComponent } from "vue";

export default defineComponent({
  components: {
    // SignIn,
    TabItem,
    TabItemStart
  },
  setup() {
    return {
      slide: ref("style"),
      lorem1: "노래하고 싶어?여기서 노래해방!",
      lorem2: "어디서나 노래와 함께하는 새로운 자유, 노래해방( 解放 )",
      lorem3: "너는 어때? 나는 어때?",
      lorem4: "너도 나도 올려방",
      lorem5: "스트레스 해소해방",
    };
  },
});
</script>

<style scoped>
.carousel_height {
  height:100%;
  /* position: fixed; */
  /* bottom: 0; */
  width: 100%;
}
</style>

