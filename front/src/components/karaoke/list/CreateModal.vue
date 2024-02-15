<template>
  <!-- 모달 -->
  <q-dialog v-model="store.createModal">
    <q-card>
      <q-card-section>
        <q-form>
          <!-- 세션 ID 입력 -->
          <q-input
            v-model="sessionName"
            :label="pref.app.kor.karaoke.list.sessionId"
            @keydown.enter="createSession"
            outlined
            dense
            class="q-mb-md"
          />

          <!-- 인원수 설정 -->
          <q-input
            v-model="numberOfParticipants"
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
            v-model="isPrivate"
            :label="
              isPrivate
                ? pref.app.kor.karaoke.list.private
                : pref.app.kor.karaoke.list.public
            "
            color="primary"
            class="q-mb-md"
          />

          <!-- 비밀번호 설정 -->
          <q-input
            v-if="isPrivate"
            v-model="password"
            :label="pref.app.kor.karaoke.list.password"
            @keydown.enter="createSession"
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
            :label="pref.app.kor.karaoke.list.joinSession"
            class="q-mt-md"
            @click="createSession"
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
import { ref } from "vue";
import pref from "@/js/config/preference.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

const store = useKaraokeStore();

const { changeRoute } = defineProps(["changeRoute"]);
const router = changeRoute;

const sessionName = ref(undefined);
const numberOfParticipants = ref("6");
const isPrivate = ref(false);
const password = ref(undefined);
const base64EncodedText = ref(undefined);

function closeModal() {
  store.createModal = false;
}

async function createSession() {
  if (!sessionName.value) {
    alert("방 제목을 입력하세요");
    return;
  }
  if (isPrivate.value && !password.value) {
    alert("비밀번호를 입력하세요");
    return;
  }

  base64EncodedText.value = btoa(
    unescape(encodeURIComponent(sessionName.value))
  ).replace(/=+$/, "");

  await store.createSession(
    base64EncodedText.value,
    numberOfParticipants.value,
    isPrivate.value,
    password.value
  );

  await joinSession();
}

function joinSession() {
  closeModal();

  const url = `/karaoke/${base64EncodedText.value}`;
  router.push(url);
}

const handleInput = () => {
  // 사용자가 입력한 값을 범위 내에 제한
  if (numberOfParticipants.value < 1) {
    numberOfParticipants.value = 1;
  } else if (numberOfParticipants.value > 6) {
    numberOfParticipants.value = 6;
  }
};
</script>

<style scoped></style>
