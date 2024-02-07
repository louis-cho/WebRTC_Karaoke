<template>
  <div>
    <!-- 로그인이 되어있다면 -->
    <div v-if="isLoggedIn">
      <TabItem />
    </div>
    <!-- 로그인이 안되어있다면 -->
    <div v-else>
      <!-- <TabItemStart/> -->
      <SignIn />
    </div>

  </div>
</template>

<script setup>
import { isLoggedIn } from "@/js/encrypt/authRequest.js";
import TabItemStart from "@/layouts/TabItemStart.vue";
import TabItem from "@/layouts/TabItem.vue";
import SignIn from "@/components/SignIn.vue";

console.log(isLoggedIn)
</script>

<style scoped>

</style>
