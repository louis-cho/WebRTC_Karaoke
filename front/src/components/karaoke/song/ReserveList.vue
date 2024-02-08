<template>
  <q-dialog v-model="store.toggleModals['reserve-list']">
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
              <q-toolbar-title style="color: black">예약 현황</q-toolbar-title>
              <q-btn @click="closeModal" color="negative" label="닫기" />
            </q-card-section>
          </q-header>

          <q-page-container>
            <q-page class="flex flex-center">
              <q-list>
                <q-item v-for="list in lists" :key="list.id">
                  <q-item-section
                    style="
                      display: flex;
                      justify-content: space-between;
                      width: 100%;
                    "
                  >
                    <q-item-label>{{ list.userName }}</q-item-label>
                    <q-item-label>{{ list.title }}</q-item-label>
                    <q-item-label>{{ list.singer }}</q-item-label>
                  </q-item-section>

                  <q-btn
                    color="negative"
                    label="취소"
                    @click="
                      cancelReserve(
                        list.userName + '&' + list.title + '&' + list.singer
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
import { ref, watchEffect } from "vue";
import axios from "axios";

const store = useKaraokeStore();

const lists = ref([]);

watchEffect(() => {
  if (store.toggleModals["reserve-list"]) {
    fetchReserveList();
  }
});

function fetchReserveList() {
  lists.value = [];

  // API 호출을 통해 노래 데이터 가져오기
  axios
    .post(store.APPLICATION_SERVER_URL + "/song/reserveList", {
      sessionName: store.sessionName,
    })
    .then((response) => {
      var id = 1;
      response.data.forEach((item) => {
        const parts = item.split("&");

        if (parts.length === 3) {
          const [userName, title, singer] = parts;
          lists.value.push({ id, userName, title, singer });
          id++;
        }
      });
      console.log(response.data);
    })
    .catch((error) => {
      console.error("Error fetching songs:", error);
    });
}

function cancelReserve(hashString) {
  axios
    .post(store.APPLICATION_SERVER_URL + "/song/cancel", {
      sessionName: store.sessionName,
      hashString: hashString,
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
  store.toggleModals["reserve-list"] = false;
}
</script>

<style scoped></style>
