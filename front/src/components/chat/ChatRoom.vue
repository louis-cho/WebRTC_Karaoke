<template>
  <router-link :to="{ path: '/chat/' + chatRoom.roomPk }">
    <li class="chat-room-item">
      <span class="room-name">{{ roomName }}</span>
      <!-- <span class="time">Time: {{ chatRoom.time }}</span> -->
    </li>
  </router-link>
</template>

<script setup>
import pref from "@/js/config/preference.js";
import { ref, onMounted } from "vue";
import axios from "axios";
import useCookie from "@/js/cookie.js";
const { getCookie } = useCookie();

const props = defineProps({
  chatRoom: Object
});

const roomName = ref('');

onMounted(async () => {
  try {
    const response = await axios.get(`${pref.app.api.host}/chatroom/info/${props.chatRoom.roomPk}`,{
      headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
    });
    roomName.value = response.data.roomName;
  } catch (error) {
    console.error("Failed to fetch room name:", error);
  }
});
</script>

<style scoped>
.chat-room-item {
  list-style-type: none;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.room-name {
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 160px;
  text-align: center;
  font-size: 20px;
  color: white;
}

.room-id, .user-pk, .status, .time {
  display: block;
  margin-bottom: 5px;
}

.room-id {
  color: #007bff;
}

.user-pk {
  color: #28a745;
}

.status {
  color: #dc3545;
}

.time {
  color: #6c757d;
}
</style>
