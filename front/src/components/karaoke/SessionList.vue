<template>
  <q-layout>
    <!-- QHeader, QDrawer, 등등 다양한 QLayout 컴포넌트들이 올 수 있습니다. -->

    <q-page-container>
      <!-- 여기에 QPage 컴포넌트를 배치합니다. -->
      <q-page>
        <q-header>
          <q-toolbar>
            <q-toolbar-title>노래방 목록</q-toolbar-title>
            <q-btn @click="openModal" label="방 만들기" color="secondary" />
          </q-toolbar>
        </q-header>

        <q-page-container>
          <q-list bordered separator>
            <q-item v-for="session in pages" :key="session.sessionId">
              <q-item-section>
                <q-item-label>방 제목 : {{ session.sessionId }}</q-item-label>
                <q-item-label caption>
                  참가자 수 : {{ session.connections.numberOfElements }}
                </q-item-label>
              </q-item-section>

              <q-btn
                @click="joinSession(session.sessionId)"
                label="입장하기"
                color="primary"
              />

              <q-item-section side>
                <q-item-label caption>
                  {{ session.recording ? "녹화 중" : "대기 중" }}
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-page-container>
      </q-page>
    </q-page-container>

    <!-- QFooter 등등 다양한 QLayout 컴포넌트들이 올 수 있습니다. -->
  </q-layout>
</template>

<script setup>
import { useKaraokeStore } from "@/stores/karaokeStore.js";

// store 사용
const store = useKaraokeStore();

const { pages, changeRoute } = defineProps(["pages", "changeRoute"]);
const router = changeRoute;

const openModal = () => {
  store.isModalOpen = true;
};

function joinSession(url) {
  router.push("karaoke/" + url);
}
</script>

<style scoped></style>
