<template>
  <!-- 모달 -->
  <q-dialog v-model="store.updateModal">
    <q-card>
      <q-card-section>
        <q-form>
          <!-- 세션 ID 입력 -->
          <q-input
            v-model="decodeBase64"
            :label="pref.app.kor.karaoke.list.sessionId"
            outlined
            dense
            class="q-mb-md"
            disable
          />

          <!-- 인원수 설정 -->
          <q-input
            v-model="store.numberOfParticipants"
            :label="
              pref.app.kor.karaoke.list.numberOfParticipants +
              pref.app.kor.karaoke.list.maxNumber
            "
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
            :label="
              store.isPrivate
                ? pref.app.kor.karaoke.list.private
                : pref.app.kor.karaoke.list.public
            "
            color="primary"
            class="q-mb-md"
          />

          <!-- 비밀번호 설정 -->
          <q-input
            v-if="store.isPrivate"
            v-model="store.password"
            :label="pref.app.kor.karaoke.list.password"
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
            :label="pref.app.kor.karaoke.session.update"
            class="q-mt-md"
            @click="updateSession"
          />
          <!-- 닫기 버튼 -->
          <q-btn
            color="negative"
            :label="pref.app.kor.karaoke.list.close"
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
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
const store = useKaraokeStore();
const decodeBase64 = decodeURIComponent(escape(atob(store.sessionName)));

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
        userName: store.userName,
        numberOfParticipants: store.numberOfParticipants,
        isPrivate: store.isPrivate,
        password: store.password,
      },
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    )
    .then((response) => {
      alert(response.data);
    })
    .catch((error) => {
      alert(error.response.data);
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
