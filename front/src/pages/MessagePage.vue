<template>
  <q-layout view="hHh lpR fFf">
    <q-page-container>
      <NavBar />
      <q-page class="flex flex-center">
        <div>
          <h3 class="text-h6">DM List</h3>
          <!-- Chat room list -->
          <ul v-if="paginatedChatRooms.length > 0" class="q-mt-md">
            <ChatRoom v-for="chatRoom in paginatedChatRooms" :key="chatRoom.roomPk" :chatRoom="chatRoom" />
          </ul>

          <!-- No chat rooms message -->
          <p v-else class="text-caption text-center q-mt-md">
            No chat rooms found.
          </p>

          <!-- Pagination -->
          <div class="q-mt-md q-px-md">
            <q-btn @click="prevPage" :disable="pageNumber === 1" color="primary" label="Previous" class="q-mr-sm"/>
            <span class="text-body-2">Page {{ pageNumber }} of {{ totalPages }}</span>
            <q-btn @click="nextPage" :disable="pageNumber === totalPages" color="primary" label="Next" class="q-ml-sm"/>
          </div>
        </div>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script setup>
import pref from "@/js/config/preference.js";
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import ChatRoom from "@/components/chat/ChatRoom.vue";
import NavBar from "@/layouts/NavBar.vue";

const { params } = useRoute();
const userPk = params.userPk;

let pageNumber = 1; // 현재 페이지 번호
const pageSize = 10; // 페이지당 표시할 아이템 수
let totalPages = 1; // 전체 페이지 수

const paginatedChatRooms = ref([]);

const fetchData = async () => {
  try {
    const response = await axios.get(`${pref.app.api.host}/chatroom/list/${userPk}`, {
      params: {
        page: pageNumber - 1,
        size: pageSize
      }
    });
    paginatedChatRooms.value = response.data.content;
    totalPages = response.data.totalPages;
  } catch (error) {
    console.error("Failed to fetch chat rooms:", error);
  }
};

onMounted(async () => {
  fetchData();
});

const nextPage = () => {
  if (pageNumber < totalPages) {
    pageNumber++;
    fetchData();
  }
};

const prevPage = () => {
  if (pageNumber > 1) {
    pageNumber--;
    fetchData();
  }
};
</script>

<style scoped>
.q-btn {
  margin-right: 10px;
}
</style>

