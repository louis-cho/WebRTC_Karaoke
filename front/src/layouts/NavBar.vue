<template>
  <div>
    <!-- 로그인이 되어있다면 -->
    <!-- <div v-if="isLoggedIn"> -->
      <TabItem />
    <!-- </div> -->
    <!-- 로그인이 안되어있다면 -->
    <!-- <div v-else> -->
      <TabItemStart/>
    <!-- </div> -->

    <!-- (주석처리) -->
    <SignIn />
  </div>
</template>

<script setup>
import { getPublicKey, register, login, isLoggedIn } from "@/js/encrypt/authRequest.js";
import TabItemStart from "@/layouts/TabItemStart.vue";
import TabItem from "@/layouts/TabItem.vue";
import SignIn from "@/components/SignIn.vue";
</script>

<style scoped>

</style>
