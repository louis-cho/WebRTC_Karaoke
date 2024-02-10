<template>
  <nav-bar />
  <div id="main-container" class="q-pa-md q-gutter-md">
    <div id="join-dialog" class="q-pa-md">
      <session-list :pages="pages" :changeRoute="router" />
      <create-modal :changeRoute="router" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import NavBar from "@/layouts/NavBar.vue";
import CreateModal from "@/components/karaoke/list/CreateModal.vue";
import SessionList from "@/components/karaoke/list/SessionList.vue";

// store 사용
const store = useKaraokeStore();
const router = useRouter();

const pages = ref([]);

// 페이지가 마운트된 후에 실행되는 코드
onMounted(() => {
  axios
    .get(store.APPLICATION_SERVER_URL + "/karaoke/sessions/sessionList", {})
    .then((response) => {
      pages.value = response.data;
      console.log(pages.value);
    })
    .catch((error) => {
      console.error(error);
    });
});
</script>

<style scoped></style>
