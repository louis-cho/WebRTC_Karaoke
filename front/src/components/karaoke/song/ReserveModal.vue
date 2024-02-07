<template>
  <q-dialog v-model="store.toggleModals['reserve-song']">
    <q-card>
      <q-card-section>
        <q-layout>
          <q-header
            class="bg-transparent"
            style="border-bottom: 1px solid #ddd"
          >
            <q-card-section
              style="display: flex; justify-content: space-between"
            >
              <q-toolbar-title style="color: black">인기차트</q-toolbar-title>
              <q-btn @click="closeModal" color="negative" label="닫기" />
            </q-card-section>
          </q-header>

          <q-page-container>
            <q-page class="flex flex-center">
              <q-list>
                <q-item v-for="song in songs" :key="song.songId">
                  <q-item-section>
                    <q-item-label>{{ song.title }}</q-item-label>
                    <q-item-label caption>{{ song.singer }}</q-item-label>
                  </q-item-section>

                  <q-btn
                    color="primary"
                    label="예약하기"
                    @click="reserveSong(song.songId)"
                  />
                </q-item>
              </q-list>
            </q-page>
          </q-page-container>
        </q-layout>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import { ref, onMounted } from "vue";
import axios from "axios";

const store = useKaraokeStore();

const songs = ref([]);

onMounted(() => {
  // API 호출을 통해 노래 데이터 가져오기
  axios
    .get(store.APPLICATION_SERVER_URL + "/song/list")
    .then((response) => {
      songs.value = response.data;
    })
    .catch((error) => {
      console.error("Error fetching songs:", error);
    });
});

function reserveSong(songId) {
  axios
    .post(store.APPLICATION_SERVER_URL + "/song/reserve", {
      userName: store.userName,
      songId: songId,
    })
    .then((response) => {
      console.log(response.data);
      closeModal();
    })
    .catch((error) => {
      console.error("Error fetching songs:", error);
    });
}

function closeModal() {
  store.toggleModals["reserve-song"] = false;
}
</script>

<style scoped></style>
