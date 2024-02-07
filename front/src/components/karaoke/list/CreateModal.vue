<template>
  <!-- 모달 -->
  <q-dialog v-model="store.createModal">
    <q-card>
      <q-card-section>
        <q-form>
          <!-- 세션 ID 입력 -->
          <q-input
            v-model="sessionName"
            :label="pref.app.kor.karaokePage.sessionId"
            outlined
            dense
            class="q-mb-md"
          />

          <!-- 사용자 이름 입력 -->
          <q-input
            v-model="store.userName"
            :label="pref.app.kor.karaokePage.userName"
            outlined
            dense
            class="q-mb-md"
          />

          <!-- 인원수 설정 -->
          <q-input
            v-model="numberOfParticipants"
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
            v-model="isPrivate"
            :label="pref.app.kor.karaokePage.public"
            color="primary"
            class="q-mb-md"
          />

          <!-- 비밀번호 설정 -->
          <q-input
            v-if="isPrivate"
            v-model="password"
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
            :label="pref.app.kor.karaokePage.joinSession"
            class="q-mt-md"
            @click="createSession"
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
import { ref } from "vue";
import pref from "@/js/config/preference.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

const store = useKaraokeStore();

const { changeRoute } = defineProps(["changeRoute"]);
const router = changeRoute;

const sessionName = ref(undefined);
const numberOfParticipants = ref("1");
const isPrivate = ref(false);
const password = ref(undefined);

function closeModal() {
  store.createModal = false;
}

async function createSession() {
  await store.createSession(
    sessionName.value,
    numberOfParticipants.value,
    isPrivate.value,
    password.value
  );

  await joinSession();
}

function joinSession() {
  closeModal();

  const url = `/karaoke/${sessionName.value}`;
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
