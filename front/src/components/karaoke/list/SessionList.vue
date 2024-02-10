<template>
  <q-layout>
    <q-page-container>
      <q-page>
        <q-header>
          <q-toolbar>
            <q-toolbar-title>
              {{ pref.app.kor.karaoke.list.sessionList }}
            </q-toolbar-title>
            <q-btn
              @click="openModal"
              :label="pref.app.kor.karaoke.list.createSession"
              color="secondary"
            />
          </q-toolbar>
        </q-header>

        <q-page-container>
          <q-list bordered separator>
            <q-item v-for="session in pages" :key="session.sessionId">
              <q-item-section>
                <q-item-label
                  >{{ pref.app.kor.karaoke.list.sessionId }} :
                  {{ session.sessionId }}</q-item-label
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
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import pref from "@/js/config/preference.js";

// store 사용
const store = useKaraokeStore();

const { pages, changeRoute } = defineProps(["pages", "changeRoute"]);
const router = changeRoute;

const openModal = () => {
  store.createModal = true;
};

async function joinSession(url) {
  router.push("karaoke/" + url);
}
</script>

<style scoped></style>
