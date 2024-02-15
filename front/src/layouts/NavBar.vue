<template>
  <div>
    <!-- 로그인이 되어있다면 -->
    <TabItem v-if="isLoggedIn" />
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
const notificationStore = useNotificationStore();
const { setCookie, getCookie, removeCookie } = useCookie();

const isLoggedIn = ref(false);

onMounted(() => {
  if (getCookie("Authorization")) {
    isLoggedIn.value = true;
    notificationStore.setSse();
  }
});
</script>

<style scoped></style>
