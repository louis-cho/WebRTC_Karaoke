<template>
  <!-- 모달 -->
  <q-dialog v-model="store.updateModal">
    <q-card>
      <q-card-section>
        <q-form>
          <!-- 세션 ID 입력 -->
          <q-input
            v-model="store.sessionName"
            :label="pref.app.kor.karaokePage.sessionId"
            outlined
            dense
            class="q-mb-md"
            disable
          />

          <!-- 인원수 설정 -->
          <q-input
            v-model="store.numberOfParticipants"
            :label="pref.app.kor.karaokePage.numberOfParticipants"
            outlined
            dense
            type="number"
            class="q-mb-md"
            min="1"
            max="6"
            @input="handleInput"
          />

          <!-- 공개 여부 설정 -->
          <q-toggle
            v-model="store.isPrivate"
            :label="pref.app.kor.karaokePage.public"
            color="primary"
            class="q-mb-md"
          />

          <!-- 비밀번호 설정 -->
          <q-input
            v-if="store.isPrivate"
            v-model="store.password"
            :label="pref.app.kor.karaokePage.password"
            outlined
            dense
            type="password"
            class="q-mb-md"
          />
        </q-form>

        <div style="display: flex; justify-content: space-between">
          <!-- 제출 버튼 -->
          <q-btn
            type="submit"
            color="primary"
            :label="pref.app.kor.karaokePage.updateSession"
            class="q-mt-md"
            @click="updateSession"
          />
          <!-- 닫기 버튼 -->
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
import pref from "@/js/config/preference.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import axios from "axios";

const store = useKaraokeStore();

function closeModal() {
  store.updateModal = false;
}

function updateSession() {
  closeModal();

  axios
    .post(
      store.APPLICATION_SERVER_URL + "/karaoke/sessions/updateSession",
      {
        sessionName: store.sessionName,
        numberOfParticipants: store.numberOfParticipants,
        isPrivate: store.isPrivate,
        password: store.password,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then((response) => {
      alert(response.data);
    });
}

const handleInput = () => {
  // 사용자가 입력한 값을 범위 내에 제한
  if (store.numberOfParticipants < 1) {
    store.numberOfParticipants = 1;
  } else if (store.numberOfParticipants > 6) {
    store.numberOfParticipants = 6;
  }
};
</script>

<style scoped></style>
