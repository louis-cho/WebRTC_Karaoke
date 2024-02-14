<template>
  <div>
    <!-- 로그인이 되어있다면 -->
    <TabItem v-if="isLoggedIn" />
    <!-- 로그인이 안되어있다면 -->
    <SignIn v-else />
  </div>
</template>

<script setup>
import { ref } from "vue";
import TabItem from "@/layouts/TabItem.vue";
import SignIn from "@/components/SignIn.vue";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();

const isLoggedIn = ref(getCookie("isLoggedIn"));
</script>

<style scoped></style>
