<template>
  <q-dialog v-model="store.toggleModals['reserve-song']">
    <q-card>
      <q-card-section>
        <q-layout style="min-height: 512px">
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
            <q-input
              type="text"
              :placeholder="pref.app.kor.karaoke.session.search"
              v-model="searchQuery"
              outlined
              dense
              class="inline-input"
            />
          </q-header>

          <q-page-container>
            <q-page class="flex flex-start" style="min-height: 512px">
              <q-list style="min-width: 512px">
                <q-item v-for="song in filteredSongs" :key="song.songId">
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
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import pref from "@/js/config/preference.js";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
const store = useKaraokeStore();

const songs = ref([]);
const searchQuery = ref("");

onMounted(() => {
  // API 호출을 통해 노래 데이터 가져오기
  axios
    .get(store.APPLICATION_SERVER_URL + "/song/list", {
      headers: {
        Authorization: getCookie("Authorization"),
        refreshToken: getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
    })
    .then((response) => {
      songs.value = response.data;
    })
    .catch((error) => {
      alert(error.response.data);
    });
});

function reserveSong(songId) {
  axios
    .post(
      store.APPLICATION_SERVER_URL + "/song/reserve",
      {
        sessionName: store.sessionName,
        userName: store.userName,
        songId: songId,
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
      store.session.signal({ type: "reserve" });
      searchQuery.value = "";
      closeModal();
    })
    .catch((error) => {
      alert(error.response.data);
    });
}

const filteredSongs = computed(() => {
  if (!searchQuery.value.trim()) {
    return songs.value;
  }

  const query = searchQuery.value.trim().toLowerCase();
  return songs.value.filter((song) => {
    return (
      song.title.toLowerCase().includes(query) ||
      song.singer.toLowerCase().includes(query)
    );
  });
});

function closeModal() {
  store.toggleModals["reserve-song"] = false;
}
</script>

<style scoped></style>
