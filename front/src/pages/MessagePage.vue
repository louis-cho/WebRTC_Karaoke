<template>
  <q-layout view="hHh lpR fFf">
    <q-page-container>
      <NavBar />
      <q-page class="flex flex-center">
        <div>
          <div class="flex justify-between items-center">
            <div>
              <h3 class="text-h6">DM List</h3>
            </div>
            <div>
              <q-btn @click="openModal" color="primary" label="Create" />
            </div>
          </div>
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

    <!-- Modal -->
    <q-dialog v-model="modalOpen" persistent>
      <q-card>
        <q-card-section>
          <q-input v-model="newRoomName" label="Room Name" />
          <q-input v-model="newGuests" label="Guests (comma separated)" />
        </q-card-section>
        <q-card-actions align="right">
          <q-btn label="Cancel" color="primary" @click="closeModal" />
          <q-btn label="Create" color="primary" @click="handleCreateChatRoom" />
        </q-card-actions>
      </q-card>
    </q-dialog>

  </q-layout>
</template>

<script setup>
import pref from "@/js/config/preference.js";
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import ChatRoom from "@/components/chat/ChatRoom.vue";
import NavBar from "@/layouts/NavBar.vue";
import useCookie from "@/js/cookie.js";

const { params } = useRoute();

// UUID를 이용하여 userPk를 가져오는 HTTP 요청 함수
async function fetchUserPk() {
  try {
    const response = await fetch(`http://i10a705.p.ssafy.io/api/v1/user/getPk?uuid=${userUUID}`);
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Failed to fetch userPk:", error);
    throw error;
  }
}

// const userUUID = params.userUUID;
const { getCookie } = useCookie();
const userUUID = getCookie("uuid");
let userPk = null; // userPk 초기화

let pageNumber = 1; // 현재 페이지 번호
const pageSize = 10; // 페이지당 표시할 아이템 수
let totalPages = 1; // 전체 페이지 수

const paginatedChatRooms = ref([]);
const modalOpen = ref(false); // 모달을 열기 위한 변수
const newRoomName = ref(''); // roomName 변수 정의
const newGuests = ref(''); // guests 변수 정의

const openModal = () => {
  modalOpen.value = true;
};

const closeModal = () => {
  modalOpen.value = false;
};

const fetchData = async () => {
  try {
    const response = await axios.get(`${pref.app.api.host}/chatroom/list/${userPk}`, {
      params: {
        page: pageNumber - 1,
        size: pageSize
      },
      headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
    });
    paginatedChatRooms.value = response.data.content;
    totalPages = response.data.totalPages;
  } catch (error) {
    console.error("Failed to fetch chat rooms:", error);
  }
};

onMounted(async () => {
  try {
    userPk = await fetchUserPk(); // userPk 가져오기
    console.log(userPk + "gdgdgdg")
    fetchData(); // 데이터 가져오기
  } catch (error) {
    console.error("Failed to initialize:", error);
  }
});

const handleCreateChatRoom = async () => {
  try {
    const response = await axios.post(`${pref.app.api.host}/chatroom/create?name=${newRoomName.value}&host=${userPk}&guests=${newGuests.value.split(',').map(guest => guest.trim())}`,null,{
      headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
    });
    console.log("Chat room created successfully");
    closeModal();
    fetchData();
  } catch (error) {
    console.error("Failed to create chat room:", error);
  }
};
</script>

<style scoped>
.q-btn {
  margin-right: 10px;
}
</style>
