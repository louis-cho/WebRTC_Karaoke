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
        <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">대화 상대 검색</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            rounded
            outlined
            v-model="search"
            label="Search NickName"
            @keydown.enter.stop
            @change="searchNickname"
          >
            <template v-slot:append>
              <q-icon name="search" />
            </template>
          </q-input>
        </q-card-section>

        <q-scroll-area style="height: 300px; max-width: 300px">
          <div>
            <!-- 유저 목록 뜨게 -->
            <q-list v-if="searchUsers && searchUsers.length && filteredUsers.length">
              <q-item v-for="user in filteredUsers" :key="user.userKey">
                <q-item-section>
                  <q-img class="img-container" :src="user.profileImgUrl" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{ user.nickname }}</q-item-label>
                  <q-item-label caption>{{ user.introduction }}</q-item-label>
                  <!-- 친구 아니라면 -->
                  <div v-if="user.userKey === userUUID">
                    <q-btn color="black" label="본인" :disable="true"></q-btn>
                  </div>
                  <div v-else-if="checkInvited(user.userKey)">
                    <q-btn color="primary" label="참가중" :disable="true"></q-btn>
                  </div>
                  <div v-else>
                    <q-btn color="red" label="초대하기" @click="inviteUser(user.userKey)" v-if="!checkInvited(user.userKey)" />
                  </div>
                </q-item-section>
              </q-item>
            </q-list>
            <!-- Display a message if no users match the search -->
            <q-item v-else>
              <q-item-section>
                <q-item-label align="center"
                  >일치하는 유저가 없습니다</q-item-label
                >
              </q-item-section>
            </q-item>
          </div>
        </q-scroll-area>
      </q-card>
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
import { ref, onMounted, computed } from "vue";
import axios from "axios";
import ChatRoom from "@/components/chat/ChatRoom.vue";
import NavBar from "@/layouts/NavBar.vue";
import useCookie from "@/js/cookie.js";
import { searchUser, fetchUser } from "@/js/user/user.js";

const search = ref("");
const searchUsers = ref([]);
const chatroomUsers = ref([]);

const filteredUsers = computed(() => {
  const query = search.value ? search.value.toLowerCase() : ''; // null 체크를 수행하여 null이 아닐 때만 toLowerCase 호출
  // 검색어가 비어있으면 빈 배열 반환
  if (!query) return [];

  return searchUsers.value.filter((user) => {
    const nickname = user.nickname ? user.nickname.toLowerCase() : ''; // null 체크 추가
    const introduction = user.introduction ? user.introduction.toLowerCase() : ''; // null 체크 추가
    return nickname.includes(query) || introduction.includes(query);
  });
});

const searchNickname = async function () {
  try {
    // 백엔드 서버에서 유저 검색 결과 가져오기
    console.log(search.value)
    const response = await searchUser(search.value);
    console.log(response)
    searchUsers.value = response; // 서버 응답에 따라 데이터를 업데이트

    for (let idx in searchUsers.value) {
      let userUuid = searchUsers.value[idx].userUuid;
      searchUsers.value[idx] = await fetchUser(userUuid);
    }
  } catch (error) {
    console.error("Error fetching user data:", error);
  }
};

const inviteUser = async (userUuid) => {
  // 이미 초대된 사용자인지 확인
  if (!checkInvited(userUuid)) {
    // 초대할 사용자의 userUuid를 newGuests 배열에 추가
    newGuests.value += userUuid;
    await handleInviteUsers();
  }
};

// chatroomUsers에 대한 유저 초대 여부 확인 함수
const checkInvited = (userUuid) => {
  for (const user of chatroomUsers.value) {
    if (String(user.userKey) === String(userUuid)) {
      return true; // 초대된 경우 true 반환
    }
  }
  return false; // 초대되지 않은 경우 false 반환
};

const { getCookie } = useCookie();
const userUUID = getCookie("uuid");

let pageNumber = ref(1); // 현재 페이지 번호
const pageSize = 10; // 페이지당 표시할 아이템 수
let totalPages = ref(1); // 전체 페이지 수

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
    const response = await axios.get(`${pref.app.api.host}/chatroom/list/${userUUID}`, {
      params: {
        page: pageNumber.value - 1,
        size: pageSize
      },
      headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
    });
    paginatedChatRooms.value = response.data.content;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error("Failed to fetch chat rooms:", error);
  }
};

// 다음 페이지로 이동
const nextPage = () => {
  if (pageNumber.value < totalPages.value) {
    pageNumber.value++;
    fetchData();
  }
};

// 이전 페이지로 이동
const prevPage = () => {
  if (pageNumber.value > 1) {
    pageNumber.value--;
    fetchData();
  }
};

onMounted(async () => {
  try {
    fetchData(); // 데이터 가져오기
  } catch (error) {
    console.error("Failed to initialize:", error);
  }
});

const handleCreateChatRoom = async () => {
  try {
    const response = await axios.post(`${pref.app.api.host}/chatroom/create?name=${newRoomName.value}&host=${userUUID}&guests=${newGuests.value.split(',').map(guest => guest.trim())}`,null,{
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
