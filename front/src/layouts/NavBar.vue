<template>
  <div>
    <!-- 로그인이 되어있다면 -->
    <TabItem v-if="store.isLoggedIn" />
    <!-- 로그인이 안되어있다면 -->
    <SignIn v-else />
  </div>
</template>

<script setup>
import TabItem from "@/layouts/TabItem.vue";
import SignIn from "@/components/SignIn.vue";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

const store = useKaraokeStore();
</script>

<style scoped></style>
