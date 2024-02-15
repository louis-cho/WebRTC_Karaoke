<template>
  <q-layout view="hHh lpR fFf">
    <nav-bar />
    <q-page-container style="min-height: 400px; font-size:100%;">
      <div class="dm-container" style="min-height: 400px; max-height: 700px;">
        <div class="content-container">
          <!-- 채팅방 정보 표시 -->
          <div class="chatroom-info">
            <img
              src="@/assets/icon/back.png"
              alt="뒤로가기"
              class="back-icon"
              @click="goBack"
            />
            <!-- 채팅방 정보 내용 -->
            <h5 :title="roomName">{{ roomName }}</h5>
            <q-btn @click="openModal" color="primary" label="Invite" />
            <!-- 참여자 목록 -->
            <div class="participant-list" style="font-size:50%;">
              <span class="participant-list-title">채팅방 참여자</span>
              <div class="participants-container">
                <div class="participants">
                  <div
                    v-for="(user, index) in chatroomUsers"
                    :key="index"
                    class="participant"
                  >
                    {{ user.nickname }}
                    <span v-if="user.userKey === userUUID">(나)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 채팅창 내역 -->
          <div
            class="chatting-container"
            style="min-width: 600px; max-width: 600px; font-size:65%;"
          >
            <div
              class="dm-messages"
              ref="messagesContainer"
              @scroll="handleScroll"
            >
              <div v-for="(message, index) in messages" :key="index">
                <div
                  :class="[
                    'message',
                    message.sender == userUUID ? 'my-message' : 'other-message',
                  ]"
                >
                  <template v-if="message.sender != userUUID">
                    <div
                      v-if="message.type === 'TALK'">
                      <q-chat-message
                        :name="getNickname(message.sender)"
                        :text="[message.message]"
                        bg-color="primary"
                        text-color="white"
                        size="auto"
                      />
                    </div>
                    <div v-else-if="message.type === 'TYPE'">
                      <q-chat-message
                        :name="getNickname(message.sender)"
                        bg-color="primary"
                        text-color="white"
                        size="1">
                        <q-spinner-dots size="2rem" />
                     </q-chat-message>
                    </div>
                    <div v-else-if="message.type === 'MEDIA'">
                      <q-chat-message
                        :name="getNickname(message.sender)"
                        bg-color="primary"
                        text-color="white"
                        size="auto">
                        <img
                          class="message-img"
                          :src="message.message"
                          alt="MEDIA"
                          @click="openOriginalImage(message.message)"
                        />
                      </q-chat-message>
                    </div>
                  </template>
                  <template v-else>
                    <div
                      v-if="message.type === 'TALK'"
                    >
                      <q-chat-message
                        :text="[message.message]"
                        bg-color="amber-7"
                        size="auto"
                        sent
                      />
                    </div>
                    <div v-else-if="message.type === 'MEDIA'">
                      <q-chat-message
                        bg-color="amber-7"
                        size="auto"
                        sent>
                        <img
                          class="message-img"
                          :src="message.message"
                          alt="MEDIA"
                          @click="openOriginalImage(message.message)"
                        />
                      </q-chat-message>
                    </div>
                  </template>
                  <!-- <div v-else>Unknown message type: {{ message.type }}</div> -->
                </div>
              </div>
            </div>

            <!-- 메시지 입력창 -->
            <div class="img_class1">
              <textarea
                v-model="newMessage"
                @keydown.enter.prevent="sendMessage"
                @input="sendTypingHandler"
                placeholder="메시지를 입력하세요..."
              ></textarea>
              <label for="fileInput" class="img_label">
                <img
                  src="@/assets/icon/image.png"
                  alt="File Icon"
                  class="img_class2"
                />
              </label>
              <input
                type="file"
                ref="fileInput"
                id="fileInput"
                @change="handleFileUpload"
                style="display: none"
              />
            </div>
          </div>
        </div>
      </div>
    </q-page-container>
    <!-- Modal -->
    <q-dialog v-model="modalOpen" persistent>
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">유저검색</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            rounded
            outlined
            v-model="search"
            label="Search User"
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
                      label="참가중"
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
                  >일치하는 유저가 없습니다</q-item-label
                >
              </q-item-section>
            </q-item>
          </div>
        </q-scroll-area>
      </q-card>
    </q-dialog>
  </q-layout>
</template>

<script setup>
import Stomp from "stompjs";
import pref from "@/js/config/preference.js";
import { ref, nextTick, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import NavBar from "@/layouts/NavBar.vue";
import useCookie from "@/js/cookie.js";
import { searchUser, fetchUser } from "@/js/user/user.js";

const router = useRouter();

const { getCookie } = useCookie();
const userUUID = getCookie("uuid");

const search = ref("");
const searchUsers = ref([]);

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
    console.error("Error fetching user data:", error);
  }
};

const inviteUser = async (userUuid) => {
  if (!checkInvited(userUuid)) {
    newGuests.value = userUuid;
    await handleInviteUsers();
  }
};

const checkInvited = (userUuid) => {
  for (const user of chatroomUsers.value) {
    if (String(user.userKey) === String(userUuid)) {
      return true;
    }
  }
  return false;
};

onMounted(async () => {
  roomId.value = route.params.roomPk;
  const socket = new WebSocket(`${pref.app.api.websocket}/api/ws`);
  stompClient.value = Stomp.over(socket);

  stompClient.value.connect({}, () => {
    stompClient.value.subscribe(
      `/exchange/chat.exchange/room.${roomId.value}`,
      (message) => {
        handleIncomingMessage(JSON.parse(message.body));
      }
    );
    fetchData().then(() => {
      loadOldMessages().then(() => {
        loadNewMessages().then(() => {
          setTimeout(() => {
            nextTick(() => {
              if (messagesContainer.value) {
                messagesContainer.value.scrollTop =
                  messagesContainer.value.scrollHeight;
              }
            });
          }, 150);
        });
      });
    });
  });

  const response = await axios.get(
    `${pref.app.api.host}/chatroom/info/${roomId.value}`,
    {
      headers: {
        Authorization: getCookie("Authorization"),
        refreshToken: getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
    }
  );
  roomName.value = response.data.roomName;
});

function handleIncomingMessage(message) {
  if (message) {
    const existingTypeMessages = messages.value.filter(
      (msg) => msg.type === "TYPE"
    );
    existingTypeMessages.forEach((msg) => {
      const index = messages.value.indexOf(msg);
      messages.value.splice(index, 1);
    });

    if (message.type === "TYPE") {
      const existingTypeMessage = messages.value.find(
        (msg) => msg.sender === message.sender && msg.type === "TYPE"
      );

      if (existingTypeMessage) {
        existingTypeMessage.message = "...";
        clearTimeout(existingTypeMessage.timer);
      } else {
        setTemporaryMessage(message.sender, "TYPE", "...", message.time);
      }

      setTimeout(() => {
        nextTick(() => {
          messagesContainer.value.scrollTop =
            messagesContainer.value.scrollHeight;
        });
      }, 300);

      setTimeout(() => {
        removeTemporaryMessage(message.sender, "TYPE");
      }, 5000);
    } else if (message.type === "TALK") {
      setMessage(message.sender, message.type, message.message, message.time);
      setTimeout(() => {
        nextTick(() => {
          messagesContainer.value.scrollTop =
            messagesContainer.value.scrollHeight;
        });
      }, 300);
    } else if (message.type === "MEDIA") {
      setMessage(message.sender, message.type, message.message, message.time);
      setTimeout(() => {
        nextTick(() => {
          messagesContainer.value.scrollTop =
            messagesContainer.value.scrollHeight;
        });
      }, 300);
    }
  }
}

function removeTemporaryMessage(sender, type) {
  const temporaryMessages = messages.value.filter(
    (msg) => msg.temporary && msg.sender === sender && msg.type === type
  );
  temporaryMessages.forEach((msg) => {
    const index = messages.value.indexOf(msg);
    messages.value.splice(index, 1);
  });
}

function setTemporaryMessage(sender, type, message, time) {
  const temporaryMessage = { sender, type, message, time, temporary: true };

  if (userUUID != sender) {
    messages.value.push(temporaryMessage);

    temporaryMessage.timer = setTimeout(() => {
      removeTemporaryMessage(sender, type);
    }, 5000);
  }
}

function loadOldMessages() {
  if (loading) return;
  loading = true;
  return axios
    .get(
      `${pref.app.api.host}/chat/room/${roomId.value}/oldMsg?page=${page}&size=50`,
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    )
    .then((response) => {
      const oldMessages = response.data;
      if (oldMessages.length === 0) {
        // alert("마지막 페이지입니다.");
      } else if (page === 1) {
        oldMessages.forEach((message) => {
          message = JSON.parse(message);
          setMessage(
            message.sender,
            message.type,
            message.message,
            message.time
          );
        });
        page++;
        if (oldMessages.length <= 25) {
          loading = false;
          loadOldMessages().then(() => {
            nextTick(() => {
              messagesContainer.value.scrollTop +=
                messagesContainer.value.scrollHeight;
            });
          });
        }
      } else {
        oldMessages.reverse().forEach((message) => {
          message = JSON.parse(message);
          setNewMessage(
            message.sender,
            message.type,
            message.message,
            message.time
          );
        });
        nextTick(() => {
          messagesContainer.value.scrollTop += 1;
        });
        page++;
      }
    })
    .catch((error) => {
      console.error("이전 메시지 불러오기 실패:", error);
    })
    .finally(() => {
      loading = false;
    });
}

function handleScroll() {
  if (messagesContainer.value.scrollTop === 0 && !loading) {
    loadOldMessages();
  }
}

function loadNewMessages() {
  return axios
    .get(`${pref.app.api.host}/chat/room/${roomId.value}/newMsg`, {
      headers: {
        Authorization: getCookie("Authorization"),
        refreshToken: getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
    })
    .then((response) => {
      const newMessages = response.data;
      newMessages.reverse().forEach((message) => {
        message = JSON.parse(message);
        setMessage(message.sender, message.type, message.message, message.time);
      });
    })
    .catch((error) => {
      console.error("최근 메시지 불러오기 실패:", error);
    });
}

const messages = ref([]);
const newMessage = ref("");
const messagesContainer = ref(null);
const roomId = ref("");
const modalOpen = ref(false);
const roomName = ref("");
const chatroomUsers = ref([]);
const newGuests = ref("");
const route = useRoute();
const stompClient = ref(null);

let page = 1;
let loading = false;

let typingTimer = null;
const throttleTime = 500;

const throttleSendTyping = function () {
  clearTimeout(typingTimer);
  typingTimer = setTimeout(() => {
    sendTyping();
  }, throttleTime);
};

const sendTypingHandler = function () {
  throttleSendTyping();
};

const sendMessage = function () {
  if (newMessage.value.trim() !== "") {
    const textMessageString = `{"type": "TALK", "roomId" : ${roomId.value}, "sender" : "${userUUID}", "message": "${newMessage.value}", "time" : ""}`;
    handleMessage(textMessageString);
    newMessage.value = "";
  }
};

const sendTyping = function () {
  const textMessageString = `{"type": "TYPE", "roomId" : ${roomId.value}, "sender" : "${userUUID}", "message": "...", "time" : ""}`;
  handleMessage(textMessageString);
};

function handleMessage(msg) {
  try {
    const result = JSON.parse(msg);

    if (result.type != null && result.message != null) {
      switch (result.type) {
        case "TALK":
          stompClient.value.send(
            `/pub/chat.message.${roomId.value}`,
            {},
            JSON.stringify(result)
          );
          break;
        case "MEDIA":
          stompClient.value.send(
            `/pub/chat.message.${roomId.value}`,
            {},
            JSON.stringify(result)
          );
          setTimeout(() => {
            nextTick(() => {
              messagesContainer.value.scrollTop =
                messagesContainer.value.scrollHeight;
            });
          }, 300);
          break;
        case "TYPE":
          stompClient.value.send(
            `/pub/chat.typing.${roomId.value}`,
            {},
            JSON.stringify(result)
          );
          break;
        default:
          setMessage("unknown", result.type);
      }
    } else {
      setMessage("invalid", null);
    }
  } catch (error) {
    setMessage("error", error);
  }
}

function setMessage(sender, type, message, time) {
  messages.value.push({ sender, type, message, time });
}

function setNewMessage(sender, type, message, time) {
  messages.value.unshift({ sender, type, message, time });
}

async function handleFileUpload(event) {
  const file = event.target.files[0];
  if (file) {
    try {
      const formData = new FormData();
      formData.append("file", file);

      const response = await axios.post(
        `${pref.app.api.host}/upload`,
        formData,
        {
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "multipart/form-data",
          },
        }
      );

      const fileUrl = response.data;

      const imageMessageString = `{"type": "MEDIA", "roomId" : ${roomId.value}, "sender" : "${userUUID}", "message": "${fileUrl}", "time" : ""}`;
      handleMessage(imageMessageString);
    } catch (error) {
      console.error("파일 업로드 실패:", error);
    }
  }
}

const openModal = () => {
  modalOpen.value = true;
};

const closeModal = () => {
  modalOpen.value = false;
};

const handleInviteUsers = async () => {
  try {
    await axios.post(
      `${pref.app.api.host}/chatroom/invite/${roomId.value}?guests=${newGuests.value}`,
      null,
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    );
    fetchData();
  } catch (error) {
    console.error("Failed to invite chat room:", error);
  }
};

const fetchData = async () => {
  try {
    const response = await axios.get(
      `${pref.app.api.host}/chatroom/list/users/${roomId.value}`,
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    );
    const users = response.data;
    chatroomUsers.value = [];
    for (const user of users) {
      const userInfo = await axios.post(
        `${pref.app.api.host}/user/get/${user.userUuid}`,
        {
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "application/json",
          },
        }
      );
      chatroomUsers.value.push(userInfo.data);
    }
  } catch (error) {
    console.error("Failed to fetch chat rooms:", error);
  }
};

const openOriginalImage = (imageUrl) => {
  window.open(
    imageUrl,
    "_blank",
    "width=800,height=600,resizable=yes,scrollbars=yes"
  );
};

const getNickname = (userUuid) => {
  for (const user of chatroomUsers.value) {
    if (String(user.userKey) == String(userUuid)) {
      return user.nickname;
    }
  }
  return "(알수없음)";
};

const goBack = function () {
  router.go(-1);
};
</script>

<style scoped>
.dm-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  text-align: right;
}

.content-container {
  display: flex;
  flex-direction: row;
  justify-content: center; /* 화면 가운데 정렬 */
  /* align-items: center; 화면 가운데 정렬 */
}

.chatroom-info {
  width: 200px; /* 예시로 지정한 폭 */
  background-color: #f3f3f3;
  padding: 20px;
  margin-right: 20px; /* 채팅창과 간격 주기 */
  overflow: hidden; /* 내부 요소가 넘치면 숨기기 */
  white-space: nowrap; /* 줄 바꿈 방지 */
  text-overflow: ellipsis; /* 내용이 너무 길면 ...으로 표시 */
}

.chatroom-info h5 {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chatting-container {
  display: flex;
  flex-direction: column;
  height: 80vh;
  text-align: right;
}

.dm-messages {
  flex: 1;
  overflow-y: auto;
  transition: scrollTop 0.3s ease;
}

.dm-messages::-webkit-scrollbar {
  display: none;
}

.message {
  padding: 10px;
}

.my-message {
  text-align: right;
}

.other-message {
  text-align: left;
}

textarea {
  width: 100%;
  resize: none;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 10px;
}

.img_class1 {
  display: flex;
  justify-content: flex-end;
  position: relative;
}

.img_class2 {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
}

.message-img {
  max-width: 150px; /* 최대 너비 지정 */
  height: auto; /* 비율에 맞춰 자동 조정 */
  cursor: pointer; /* 마우스 오버 시 포인터 모양 변경 */
}

.participants-container {
  flex: 1;
  overflow-y: scroll;
  overflow-x: hidden;
  max-height: 250px;
  margin-top: 20px;
}

.participants-container::-webkit-scrollbar {
  display: none;
}

.participants {
  padding: 10px;
}

.participant {
  margin-bottom: 5px;
}

.participant-list {
  margin-top: 20px; /* 참여자 목록과의 간격 조정 */
}

.participant-list-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  display: block;
}

.back-icon {
  cursor: pointer;
  float: left;
}
</style>
