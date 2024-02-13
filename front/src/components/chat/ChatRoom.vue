<template>
  <router-link :to="{ path: '/chat/' + chatRoom.roomPk }">
    <li class="chat-room-item">
      <span class="room-id">Room ID: {{ chatRoom.roomPk }}</span>
      <span class="room-name">Room Name : {{ roomName }}</span>
      <span class="user-pk">User PK: {{ chatRoom.userPk }}</span>
      <span class="status">Status: {{ chatRoom.status }}</span>
      <span class="time">Time: {{ chatRoom.time }}</span>
    </li>
  </router-link>
</template>

<script setup>
import pref from "@/js/config/preference.js";
import { ref, onMounted } from "vue";
import axios from "axios";

const props = defineProps({
  chatRoom: Object
});

const roomName = ref('');

onMounted(async () => {
  try {
    const response = await axios.get(`${pref.app.api.host}/chatroom/info/${props.chatRoom.roomPk}`);
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
