<template>
  <div v-if="streamManager">
    <q-card bordered class="q-mb-md">
      <ov-video :stream-manager="streamManager" />
      <!-- {{ clientData }}이게 내 현재 이름임 -->
      <div class="flex-container">
        <div>{{ clientData }}</div>
        <div v-if="store.isModerator && props.streamManager.remote">
          <q-btn
            type="submit"
            color="negative"
            :label="pref.app.kor.karaoke.session.kick"
            @click="kickUser"
          />
        </div>
      </div>
    </q-card>
  </div>
</template>

<script setup>
import { computed } from "vue";
import OvVideo from "@/components/karaoke/video/OvVideo.vue";
import axios from "axios";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import pref from "@/js/config/preference.js";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
const store = useKaraokeStore();

const props = defineProps({
  streamManager: Object,
});

const connectionId = computed(() => {
  const { connection } = props.streamManager.stream;
  return connection.connectionId;
});

// clientData는 computed로 진행됨
const clientData = computed(() => {
  const { clientData } = getConnectionData();
  return clientData;
});

function getConnectionData() {
  const { connection } = props.streamManager.stream;
  return JSON.parse(connection.data);
}

function kickUser() {
  axios.post(
    store.APPLICATION_SERVER_URL + "/karaoke/sessions/kickUser",
    {
      sessionName: store.sessionName,
      reqUser: store.token,
      connectionId: connectionId.value,
    },
    {
      headers: {
        Authorization: getCookie("Authorization"),
        refreshToken: getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
    }
  );
}
</script>

<style scoped>
.flex-container {
  display: flex;
  justify-content: space-evenly;
  align-items: flex-end;
}
</style>
