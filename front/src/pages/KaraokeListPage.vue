<template>
  <nav-bar />
  <div id="main-container" class="q-pa-md q-gutter-md">
    <div id="join-dialog" class="q-pa-md">
      <session-list :changeRoute="router" />
      <create-modal :changeRoute="router" />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import NavBar from "@/layouts/NavBar.vue";
import CreateModal from "@/components/karaoke/list/CreateModal.vue";
import SessionList from "@/components/karaoke/list/SessionList.vue";

const router = useRouter();
</script>

<style scoped></style>
