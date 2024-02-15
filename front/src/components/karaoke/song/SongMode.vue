<template>
  <div>
    <normal-mode
      ref="normalModeRef"
      v-if="!store.songMode"
      :songData="store.song"
    />
    <perfect-score
      ref="perfectScoreRef"
      v-if="store.songMode"
      :songData="store.song"
    />
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import axios from "axios";
import NormalMode from "@/components/karaoke/NormalMode.vue";
import PerfectScore from "@/components/karaoke/PerfectScore.vue";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
const store = useKaraokeStore();

const perfectScoreRef = ref(null);
const normalModeRef = ref(null);

watch(
  () => store.singing,
  (newSinging) => {
    if (newSinging) {
      if (store.songMode) {
        perfectScoreRef.value.choose();
        perfectScoreRef.value.play();
      } else {
        normalModeRef.value.choose();
        normalModeRef.value.play();
      }
    } else {
      if (store.songMode) {
        perfectScoreRef.value.stop();
      } else {
        normalModeRef.value.stop();
      }
    }
  }
);

watch(
  () => store.reservedSongsLength,
  () => {
    if (store.reservedSongs.length == 0) {
    } else {
      axios
        .get(
          store.APPLICATION_SERVER_URL +
            "/song/songInfo/" +
            store.reservedSongs[0].songId,
          {
            headers: {
              Authorization: getCookie("Authorization"),
              refreshToken: getCookie("refreshToken"),
              "Content-Type": "application/json",
            },
          }
        )
        .then((res) => {
          store.song = null;
          if (JSON.parse(JSON.stringify(res.data)) != "") {
            store.song = JSON.parse(JSON.stringify(res.data));
          }
        })
        .catch((error) => {
          console.error("songInfo 불러오기 실패" + error);
        });
    }
  }
);
</script>
