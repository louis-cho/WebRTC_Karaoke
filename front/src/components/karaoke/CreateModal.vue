<template>
  <!-- 모달 -->
  <q-dialog v-model="store.isModalOpen">
    <q-card>
      <q-card-section>
        <q-form>
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
        </q-form>

        <div style="display: flex; justify-content: space-between">
          <q-btn
            type="submit"
            color="primary"
            :label="pref.app.kor.karaokePage.joinSession"
            class="q-mt-md"
            @click="joinSession"
          />
          <q-btn
            color="negative"
            label="닫기"
            class="q-mt-md"
            @click="closeModal"
          />
        </div>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { useRouter } from "vue-router";
import pref from "@/js/config/preference.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

// store 사용
const store = useKaraokeStore();

const { changeRoute } = defineProps(["changeRoute"]);
const router = changeRoute;

function closeModal() {
  store.isModalOpen = false;
}

function joinSession() {
  closeModal();

  // 이동할 URL 구성
  const url = `/karaoke/${store.mySessionId}`;

  // Vue Router를 사용하여 페이지 이동
  router.push(url);
}
</script>

<style scoped></style>
