<template>
  <div>
    <!-- 로그인이 되어있다면 -->
    <TabItem />
    <!-- 로그인이 안되어있다면 -->
    <TabItemStart/>
    <SignIn />
  </div>
</template>

<script setup>
import TabItemStart from "@/layouts/TabItemStart.vue";
import TabItem from "@/layouts/TabItem.vue";
import SignIn from "@/components/SignIn.vue";
</script>

<style scoped>

</style>
