<template>
  <q-layout style="min-height: 500px">
    <q-page-container>
      <q-page style="min-height: 500px">
        <q-header
          style="
            background-color: #be2ce2;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
          "
        >
          <q-toolbar>
            <q-toolbar-title>
              {{ pref.app.kor.karaoke.list.sessionList }}
            </q-toolbar-title>
            <q-btn
              @click="openModal"
              :label="pref.app.kor.karaoke.list.createSession"
              style="background-color: rgb(143, 22, 173)"
            />
          </q-toolbar>
        </q-header>

        <q-page-container
          style="
            background-color: white;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
          "
        >
          <q-item-section>
            <q-input
              type="text"
              :placeholder="pref.app.kor.karaoke.session.search"
              v-model="searchQuery"
              outlined
              dense
              class="inline-input"
            />
          </q-item-section>
          <q-list bordered separator>
            <q-item v-for="session in filteredSession" :key="session.sessionId">
              <q-item-section>
                <q-item-label
                  >{{ pref.app.kor.karaoke.list.sessionId }} :
                  {{ decodeBase64(session.sessionId) }}</q-item-label
                >
                <q-item-label caption>
                  {{ pref.app.kor.karaoke.list.numberOfParticipants }} :
                  {{ session.connections.numberOfElements }} /
                  {{ session.numberOfParticipants }}
                </q-item-label>
                <q-item-label caption>
                  {{ pref.app.kor.karaoke.list.status }} :
                  {{
                    session.isPrivate
                      ? pref.app.kor.karaoke.list.private
                      : pref.app.kor.karaoke.list.public
                  }}
                </q-item-label>
              </q-item-section>

              <q-item-section side>
                <q-item-label
                  >{{ pref.app.kor.karaoke.list.manager }} :
                  {{ session.manager }}
                </q-item-label>
              </q-item-section>

              <q-item-section side>
                <q-btn
                  @click="joinSession(session.sessionId)"
                  :label="pref.app.kor.karaoke.list.joinSession"
                  color="primary"
                />
              </q-item-section>

              <q-item-section side>
                <q-item-label caption>
                  {{
                    session.recording
                      ? pref.app.kor.karaoke.list.recording
                      : pref.app.kor.karaoke.list.waiting
                  }}
                </q-item-label>
              </q-item-section>
            </q-item>
          </q-list>
        </q-page-container>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import pref from "@/js/config/preference.js";
import axios from "axios";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();

// store 사용
const store = useKaraokeStore();

const { changeRoute } = defineProps(["changeRoute"]);
const router = changeRoute;
const searchQuery = ref("");
const pages = ref([]);

// 페이지가 마운트된 후에 실행되는 코드
onMounted(() => {
  axios
    .get(store.APPLICATION_SERVER_URL + "/karaoke/sessions/sessionList", {
      headers: {
        Authorization: getCookie("Authorization"),
        refreshToken: getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
    })
    .then((response) => {
      pages.value = response.data;
    })
    .catch((error) => {});
});

const openModal = () => {
  store.createModal = true;
};

async function joinSession(url) {
  router.push("karaoke/" + url);
}

function decodeBase64(encodedString) {
  return decodeURIComponent(escape(atob(encodedString)));
}

const filteredSession = computed(() => {
  if (searchQuery.value.trim() == "") {
    return pages.value;
  }

  const query = searchQuery.value.trim();
  return pages.value.filter((page) => {
    return (
      decodeBase64(page.sessionId).includes(query) ||
      page.manager.includes(query)
    );
  });
});
</script>

<style scoped></style>
