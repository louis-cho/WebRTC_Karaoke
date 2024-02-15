<template>
  <q-layout view="hHh lpR fFf">
    <nav-bar />
    <q-page-container style="max-width: 480px; position: absolute; left: 50%; margin-left:-15%">
      <q-page style="min-width: 480px; min-height: 400px;">
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
          <ul v-if="paginatedChatRooms.length > 0" class="q-mt-md flex flex-wrap">
            <div v-for="(chatRoom, index) in paginatedChatRooms" :key="chatRoom.roomPk" class="w-1/2">
              <div style="width: 200px;">
                <q-card
                  class="my-card text-white"
                  style="background: radial-gradient(circle, #35a2ff 0%, #014a88 100%)"
                >
                <ChatRoom :chatRoom="chatRoom" />
                </q-card>
              </div>
              <!-- 한 줄에 2개의 ChatRoom이 표시될 때마다 줄 바꿈을 추가합니다 -->
              <!-- index가 홀수일 때에만 줄 바꿈을 추가합니다 -->
              <br v-if="(index + 1) % 2 === 0 && index !== paginatedChatRooms.length - 1">
            </div>
          </ul>
          <!-- No chat rooms message -->
          <p v-else class="text-caption text-center q-mt-md">
            No chat rooms found.
          </p>

          <!-- Pagination -->
          <div class="q-mt-md q-px-md" style="position: absolute; left: 50%; margin-left:-35%; font-size:100%;">
            <q-btn
              @click="prevPage"
              :disable="pageNumber === 1"
              color="primary"
              label="Previous"
              class="q-mr-sm"
            />
            <span class="text-body-2" style="font-size:65%;"
              >Page {{ pageNumber }} of {{ totalPages }}</span
            >
            <q-btn
              @click="nextPage"
              :disable="pageNumber === totalPages"
              color="primary"
              label="Next"
              class="q-ml-sm"
            />
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
            <q-btn
              icon="close"
              flat
              round
              dense
              v-close-popup
              @click="handleCancel"
            />
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
              <q-list
                v-if="searchUsers && searchUsers.length && filteredUsers.length"
              >
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
                      <q-btn
                        color="primary"
                        label="초대중"
                        :disable="true"
                      ></q-btn>
                    </div>
                    <div v-else>
                      <q-btn
                        color="red"
                        label="초대하기"
                        @click="inviteUser(user.userKey)"
                        v-if="!checkInvited(user.userKey)"
                      />
                    </div>
                  </q-item-section>
                </q-item>
              </q-list>
              <!-- Display a message if no users match the search -->
              <q-item v-else>
                <q-item-section>
                  <q-item-label align="center"
                    >일치하는 유저가 없습니다.</q-item-label
                  >
                </q-item-section>
              </q-item>
            </div>
          </q-scroll-area>
        </q-card>
        <q-card-section>
          <q-input v-model="newRoomName" label="Room Name" />
        </q-card-section>
        <q-card-actions align="right">
          <!-- cancel 버튼 -->
          <q-btn label="Cancel" color="primary" @click="handleCancel" />
          <q-btn
            label="Create"
            color="primary"
            :disable="!newRoomName || !newGuests.length"
            @click="handleCreateChatRoom"
          />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-layout>
</template>

<script setup>
import pref from "@/js/config/preference.js";
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import ChatRoom from "@/components/chat/ChatRoom.vue";
import NavBar from "@/layouts/NavBar.vue";
import useCookie from "@/js/cookie.js";
import { searchUser, fetchUser } from "@/js/user/user.js";

const router = useRouter();
const search = ref("");
const searchUsers = ref([]);
const chatroomUsers = ref([]);

const filteredUsers = computed(() => {
  const query = search.value ? search.value.toLowerCase() : "";
  if (!query) return [];

  return searchUsers.value.filter((user) => {
    const nickname = user.nickname ? user.nickname.toLowerCase() : "";
    const introduction = user.introduction
      ? user.introduction.toLowerCase()
      : "";
    return nickname.includes(query) || introduction.includes(query);
  });
});

const searchNickname = async function () {
  try {
    const response = await searchUser(search.value);
    searchUsers.value = response;

    for (let idx in searchUsers.value) {
      let userUuid = searchUsers.value[idx].userUuid;
      searchUsers.value[idx] = await fetchUser(userUuid);
    }
  } catch (error) {
    // console.error("Error fetching user data:", error);
  }
};

const inviteUser = async (userUuid) => {
  if (!checkInvited(userUuid)) {
    newGuests.value.push(userUuid);
  }
};

const checkInvited = (userUuid) => {
  return newGuests.value.some(
    (userKey) => String(userKey) === String(userUuid)
  );
};

const { getCookie } = useCookie();
const userUUID = getCookie("uuid");

let pageNumber = ref(1);
const pageSize = 10;
let totalPages = ref(1);

const paginatedChatRooms = ref([]);
const modalOpen = ref(false);
const newRoomName = ref("");
const newGuests = ref([]);

const openModal = () => {
  modalOpen.value = true;
};

const closeModal = () => {
  modalOpen.value = false;
};

const fetchData = async () => {
  try {
    const response = await axios.get(
      `${pref.app.api.host}/chatroom/list/${userUUID}`,
      {
        params: {
          page: pageNumber.value - 1,
          size: pageSize,
        },
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    );
    paginatedChatRooms.value = response.data.content;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    // console.error("Failed to fetch chat rooms:", error);
  }
};

const nextPage = () => {
  if (pageNumber.value < totalPages.value) {
    pageNumber.value++;
    fetchData();
  }
};

const prevPage = () => {
  if (pageNumber.value > 1) {
    pageNumber.value--;
    fetchData();
  }
};

onMounted(async () => {
  try {
    fetchData();
  } catch (error) {
    // console.error("Failed to initialize:", error);
  }
});

const handleCreateChatRoom = async () => {
  try {
    const guestsString = newGuests.value.join(",");
    const response = await axios.post(
      `${pref.app.api.host}/chatroom/create?name=${newRoomName.value}&host=${userUUID}&guests=${guestsString}`,
      null,
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    );
    const roomPk = response.data.roomPk;
    closeModal();
    fetchData();
    newRoomName.value = "";
    newGuests.value = [];
    await router.push(`/chat/${roomPk}`);
  } catch (error) {
    // console.error("Failed to create chat room:", error);
  }
};

const handleCancel = () => {
  newRoomName.value = "";
  newGuests.value = [];
  closeModal();
};
</script>

<style scoped>
.q-btn {
  margin-right: 10px;
}
.content-container {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
}
</style>
