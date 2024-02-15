<template>
  <div>
    <!-- 로그인이 되어있다면 -->
    <TabItem v-if="loginStore.isLoggedIn" />
    <!-- 로그인이 안되어있다면 -->
    <SignIn v-else />
  </div>
</template>

<script setup>
import TabItem from "@/layouts/TabItem.vue";
import SignIn from "@/components/SignIn.vue";
import { onMounted, ref } from "vue";
import useCookie from "@/js/cookie.js";
import { useNotificationStore } from "@/stores/notificationStore.js";
import {useLoginStore} from "@/stores/loginStore.js"
const notificationStore = useNotificationStore();
const loginStore = useLoginStore();
const { setCookie, getCookie, removeCookie } = useCookie();

// const isLoggedIn = ref(false);

onMounted(() => {
  console.log("로ㅓ그인 상태 : ",loginStore.isLoggedIn);
  if (getCookie("Authorization")) {
    // isLoggedIn.value = true;
    loginStore.isLoggedIn = true;
    // const LocalNotifiaction = localStorage.getItem('notification');
    // notificationStore.sse = LocalNotifiaction.sse;
    // notificationStore.setListener();
    // console.log("피니아에값 다시 할당 되나? 제발")
  }
});
</script>

<style scoped></style>
