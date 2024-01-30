<template>
  <nav-bar />
  <div id="main-container" class="q-pa-md q-gutter-md">
    <div id="img-div" class="q-mb-md">
      <q-img
        src="src/assets/icon/logo1-removebg-preview.png"
        alt="로고 이미지"
        class="q-ma-md"
        :style="{ width: '40%' }"
      />
    </div>
    <div id="join-dialog" class="q-pa-md">
      <h1 class="q-mb-md text-h6">
        {{ pref.app.kor.karaokePage.title }}
      </h1>
      <div>
        <q-form @submit="joinSession">
          <q-input
            v-model="store.mySessionId"
            :label="pref.app.kor.karaokePage.sessionId"
            outlined
            dense
            class="q-mb-md"
          />
          <q-input
            v-model="store.myUserName"
            :label="pref.app.kor.karaokePage.userName"
            outlined
            dense
            class="q-mb-md"
          />
          <q-btn
            type="submit"
            color="primary"
            :label="pref.app.kor.karaokePage.joinSession"
            class="q-mt-md"
          />
        </q-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
import pref from "@/js/config/preference.js";
import NavBar from "@/layouts/NavBar.vue";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

// store 사용
const store = useKaraokeStore();
const router = useRouter();

// joinSession 함수 정의
function joinSession() {
  // joinSession 함수의 내용 추가

  // 이동할 URL 구성
  const url = `/karaoke/${store.mySessionId}`;

  // Vue Router를 사용하여 페이지 이동
  router.push(url);
}
</script>

<style scoped></style>
