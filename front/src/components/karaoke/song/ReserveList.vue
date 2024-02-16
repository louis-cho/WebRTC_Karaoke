<template>
  <q-dialog v-model="store.toggleModals['reserve-list']">
    <q-card>
      <q-card-section style="min-width: 512px">
        <q-layout style="min-height: 512px">
          <q-header
            class="bg-transparent"
            style="border-bottom: 1px solid #ddd"
          >
            <q-card-section
              style="display: flex; justify-content: space-between"
            >
              <q-toolbar-title style="color: black">예약 현황</q-toolbar-title>
              <q-btn @click="closeModal" color="negative" label="닫기" />
            </q-card-section>
          </q-header>

          <q-page-container>
            <q-page class="flex flex-start" style="min-height: 512px">
              <q-list>
                <q-item v-for="list in lists" :key="list.id">
                  <q-item-section class="q-mb-md" style="min-width: 400px">
                    <q-item-label>
                      {{ list.title }}
                    </q-item-label>
                    <q-item-label caption>
                      {{ list.singer }}
                    </q-item-label>
                    <q-item-label> {{ list.userName }} </q-item-label>
                  </q-item-section>

                  <q-btn
                    v-if="list.userName == store.userName || store.isModerator"
                    color="negative"
                    label="취소"
                    @click="
                      cancelReserve(
                        list.userName +
                          '&' +
                          list.songId +
                          '&' +
                          list.title +
                          '&' +
                          list.singer
                      )
                    "
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
import { onMounted, ref, watch } from "vue";
import axios from "axios";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
const store = useKaraokeStore();

const lists = ref([]);

onMounted(() => {
  fetchReserveList();
});

watch(
  () => store.newReserve,
  (newValue) => {
    if (newValue === true) {
      fetchReserveList();
      store.newReserve = false;
    }
  }
);

function fetchReserveList() {
  lists.value = [];

  // API 호출을 통해 노래 데이터 가져오기
  axios
    .post(
      store.APPLICATION_SERVER_URL + "/song/reserveList",
      {
        sessionName: store.sessionName,
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
      var id = 1;
      response.data.forEach((item) => {
        const parts = item.split("&");

        if (parts.length === 4) {
          const [userName, songId, title, singer] = parts;
          if (id == 1) {
            store.singUser = userName;
          }

          lists.value.push({ id, userName, songId, title, singer });
          id++;
        }
      });
      store.reservedSongsLength = lists.value.length;
      store.reservedSongs = lists.value;
    })
    .catch((error) => {});
}

function cancelReserve(hashString) {
  axios
    .post(
      store.APPLICATION_SERVER_URL + "/song/cancel",
      {
        sessionName: store.sessionName,
        hashString: hashString,
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
    })
    .catch((error) => {
      alert(error.response.data);
    });
}

function closeModal() {
  store.toggleModals["reserve-list"] = false;
}
</script>

<style scoped></style>
