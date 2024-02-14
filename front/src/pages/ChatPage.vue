<template>
  <q-layout view="hHh lpR fFf">
    <q-page-container>
      <div class="dm-container">
        <NavBar />

        <div class="content-container">
          <!-- 채팅방 정보 표시 -->
          <div class="chatroom-info">
            <img src="@/assets/icon/back.png" alt="뒤로가기" class="back-icon" @click="goBack" />
            <!-- 채팅방 정보 내용 -->
            <h5 :title="roomName">{{ roomName }}</h5>
            <q-btn @click="openModal" color="primary" label="Invite" />
            <!-- 참여자 목록 -->
            <div class="participant-list">
              <span class="participant-list-title">채팅방 참여자</span>
              <div class="participants-container">
                <div class="participants">
                  <div v-for="(user, index) in chatroomUsers" :key="index" class="participant">
                    {{ user.nickname }}
                    <span v-if="user.userPk === userPk">(나)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>


          <!-- 채팅창 내역 -->
          <div class="chatting-container" style="min-width: 600px; max-width: 600px;">
            <div class="dm-messages" ref="messagesContainer" @scroll="handleScroll">
              <div v-for="(message, index) in messages" :key="index">
                <div :class="['message', message.sender == userPk ? 'my-message' : 'other-message']">
                  <template v-if="message.sender != userPk">
                    {{ getNickname(message.sender) }}
                  </template>
                  <div v-if="message.type === 'TALK' || message.type === 'TYPE'">
                    {{ message.message }}
                  </div>
                  <div v-else-if="message.type === 'MEDIA'">
                    <img class="message-img" :src="message.message" alt="MEDIA" @click="openOriginalImage(message.message)">
                  </div>
                  <div v-else>
                    Unknown message type: {{ message.type }}
                  </div>
                </div>
              </div>
            </div>

            <!-- 메시지 입력창 -->
            <div class="img_class1">
              <textarea v-model="newMessage" @keydown.enter.prevent="sendMessage" @input="sendTypingHandler" placeholder="메시지를 입력하세요..."></textarea>
              <label for="fileInput" class="img_label">
                <img src="@/assets/icon/image.png" alt="File Icon" class="img_class2">
              </label>
              <input type="file" ref="fileInput" id="fileInput" @change="handleFileUpload" style="display: none;">
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
            <q-list v-if="searchUsers && searchUsers.length && filteredUsers.length">
              <q-item v-for="user in filteredUsers" :key="user.userPk">
                <q-item-section>
                  <q-img class="img-container" :src="user.profileImgUrl" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{ user.nickname }}</q-item-label>
                  <q-item-label caption>{{ user.introduction }}</q-item-label>
                  <!-- 친구 아니라면 -->
                  <div v-if="user.userPk === userPk">
                    <q-btn color="black" label="본인" :disable="true"></q-btn>
                  </div>
                  <div v-else-if="checkInvited(user.userPk)">
                    <q-btn color="primary" label="참가중" :disable="true"></q-btn>
                  </div>
                  <div v-else>
                    <q-btn color="red" label="초대하기" @click="inviteUser(user.userPk)" v-if="!checkInvited(user.userPk)" />
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
import axios from 'axios';
import NavBar from "@/layouts/NavBar.vue";
import useCookie from "@/js/cookie.js";
import { searchUser, fetchUser } from "@/js/user/user.js";
import logoImage from "@/assets/icon/logo1-removebg-preview.png"

const router = useRouter();

const { getCookie } = useCookie();
const userUUID = getCookie("uuid");

const search = ref("");
const searchUsers = ref([]);

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
    const response = await searchUser(search.value);
    console.log(response)
    searchUsers.value = response; // 서버 응답에 따라 데이터를 업데이트

    for (let idx in searchUsers.value) {
      let userPk = searchUsers.value[idx].userPk;
      searchUsers.value[idx] = await fetchUser(userPk);
    }
  } catch (error) {
    console.error("Error fetching user data:", error);
  }
};

const inviteUser = async (userPk) => {
  // 이미 초대된 사용자인지 확인
  if (!checkInvited(userPk)) {
    // 초대할 사용자의 userPk를 newGuests 배열에 추가
    newGuests.value = userPk;
    await handleInviteUsers();
  }
};

// chatroomUsers에 대한 유저 초대 여부 확인 함수
const checkInvited = (userPk) => {
  for (const user of chatroomUsers.value) {
    if (String(user.userPk) === String(userPk)) {
      return true; // 초대된 경우 true 반환
    }
  }
  return false; // 초대되지 않은 경우 false 반환
};

async function fetchUserPk() {
  try {
    const response = await fetch(`https://i10a705.p.ssafy.io/api/v1/user/getPk?uuid=${userUUID}`);
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Failed to fetch userPk:", error);
    throw error;
  }
}

onMounted(async () => {
  userPk.value = await fetchUserPk();
  roomId.value = route.params.roomPk;
  const socket = new WebSocket(`${pref.app.api.websocket}/api/ws`);
  stompClient.value = Stomp.over(socket);

  stompClient.value.connect({}, () => {
      stompClient.value.subscribe(`/exchange/chat.exchange/room.${roomId.value}`, (message) => {
          handleIncomingMessage(JSON.parse(message.body));
      });
      fetchData().then(() => {
        loadOldMessages().then(() => {
          loadNewMessages().then(() => {
            setTimeout(() => {
              nextTick(() => {
                messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
              });
            }, 150);
          });
        });
      });
  });

  const response = await axios.get(`${pref.app.api.host}/chatroom/info/${roomId.value}`,{headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },});
  roomName.value = response.data.roomName;
});


// Inside the handleIncomingMessage function
function handleIncomingMessage(message) {
  if (message) {
    console.log(message.type);

    // Remove existing TYPE messages
    const existingTypeMessages = messages.value.filter(msg => msg.type === 'TYPE');
    existingTypeMessages.forEach(msg => {
      const index = messages.value.indexOf(msg);
      messages.value.splice(index, 1);
    });

    // Handle TYPE message
    if (message.type === 'TYPE') {
      // Check if there is an existing TYPE message from the same sender
      const existingTypeMessage = messages.value.find(msg => msg.sender === message.sender && msg.type === 'TYPE');

      if (existingTypeMessage) {
        // Update the existing TYPE message with the new content and reset the timer
        existingTypeMessage.message = '...';
        clearTimeout(existingTypeMessage.timer);
      } else {
        // Add a new TYPE message if no existing one is found
        setTemporaryMessage(message.sender, 'TYPE', '...', message.time);
      }

      setTimeout(() => {
        nextTick(() => {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        });
      }, 300);

      setTimeout(() => {
        removeTemporaryMessage(message.sender, 'TYPE');
      }, 5000); // Adjust the time as needed (e.g., 5000 milliseconds for 5 seconds)
    }

    // Handle TALK message
    else if (message.type === 'TALK') {
      setMessage(message.sender, message.type, message.message, message.time);
      setTimeout(() => {
        nextTick(() => {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        });
      }, 300);
    }
  }
}

function removeTemporaryMessage(sender, type) {
  const temporaryMessages = messages.value.filter(msg => msg.temporary && msg.sender === sender && msg.type === type);
  temporaryMessages.forEach(msg => {
    const index = messages.value.indexOf(msg);
    messages.value.splice(index, 1);
  });
}

// Add these functions to handle temporary messages
function setTemporaryMessage(sender, type, message, time) {
  const temporaryMessage = { sender, type, message, time, temporary: true };

  // Add the new TYPE message to the messages array
  if(userPk.value != sender){
    messages.value.push(temporaryMessage);

    // Set the timer for the TYPE message
    temporaryMessage.timer = setTimeout(() => {
      removeTemporaryMessage(sender, type);
    }, 5000); // Adjust the time as needed (e.g., 5000 milliseconds for 5 seconds)
  }
}


function loadOldMessages() {
  // 로딩 중이면 중복 요청 방지
  if (loading) return;
  loading = true;
  return axios.get(`https://i10a705.p.ssafy.io/api/v1/chat/room/${roomId.value}/oldMsg?page=${page}&size=50`,{headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },})
    .then(response => {
      const oldMessages = response.data;
      if (oldMessages.length === 0) {
        // 빈 배열을 받으면 페이지 끝을 알리는 alert 표시
        // alert("마지막 페이지입니다.");
      } else if (page === 1) {
        oldMessages.forEach(message => {
          message = JSON.parse(message);
          setMessage(message.sender, message.type, message.message, message.time);
        });
        page++; // 다음 페이지로 이동
        if(oldMessages.length <= 25){
          loading = false;
          loadOldMessages().then(()=>{
            nextTick(() => {
              messagesContainer.value.scrollTop += messagesContainer.value.scrollHeight;
            });
          });
        }
      } else {
        oldMessages.reverse().forEach(message => {
          message = JSON.parse(message);
          setNewMessage(message.sender, message.type, message.message, message.time);
        });
        nextTick(() => {
          messagesContainer.value.scrollTop += 1;
        });
        page++; // 다음 페이지로 이동
      }
    })
    .catch(error => {
      console.error('이전 메시지 불러오기 실패:', error);
    })
    .finally(() => {
      loading = false;
    });
}

function handleScroll() {
  // 스크롤이 맨 위에 도달하면 새로운 페이지 로드
  if (messagesContainer.value.scrollTop === 0 && !loading) {
    loadOldMessages();
  }
}

function loadNewMessages() {
  return axios.get(`https://i10a705.p.ssafy.io/api/v1/chat/room/${roomId.value}/newMsg`,{headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },})
    .then(response => {
      const newMessages = response.data;
      newMessages.reverse().forEach(message => {
        message = JSON.parse(message);
        setMessage(message.sender, message.type, message.message, message.time);
      });
    })
    .catch(error => {
      console.error('최근 메시지 불러오기 실패:', error);
    });
}

// const store = useCounterStore()
const messages = ref([]);
const newMessage = ref('');
const selectedFile = ref(null);
const messagesContainer = ref(null);
const userPk = ref('');
const roomId = ref('');
const modalOpen = ref(false);
const roomName = ref('');
const chatroomUsers = ref([]);
const newGuests = ref('');
const route = useRoute();
const stompClient = ref(null);

let page = 1; // 초기 페이지 설정
let loading = false; // 페이지 로딩 상태

let typingTimer = null;
const throttleTime = 500; // 2초

const throttleSendTyping = function() {
  clearTimeout(typingTimer);
  typingTimer = setTimeout(() => {
    sendTyping();
  }, throttleTime);
};

const sendTypingHandler = function() {
  throttleSendTyping();
};


const sendMessage = function() {
  if (newMessage.value.trim() !== "") {
    const textMessageString = `{"type": "TALK", "roomId" : ${roomId.value}, "sender" : ${userPk.value}, "message": "${newMessage.value}", "time" : ""}`;
    // const textMessageString = newMessage.value
    handleMessage(textMessageString);
    newMessage.value = "";

    // 스크롤 항상 아래로 내리기
    // nextTick(() => {
    //   messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    // });
  }
}

const sendTyping = function() {
  console.log("sendTyping");
    const textMessageString = `{"type": "TYPE", "roomId" : ${roomId.value}, "sender" : ${userPk.value}, "message": "...", "time" : ""}`;
    // const textMessageString = newMessage.value
    handleMessage(textMessageString);
}


function handleMessage(msg) {
  try {
    // 문자열을 객체로 변환
    const result = JSON.parse(msg);

    // result 객체에 type 및 content 필드가 있는지 확인
    if (result.type != null && result.message != null) {
      // switch 문을 통한 분기
      switch (result.type) {
        case "TALK":
          console.log('텍스트')
          // text 메시지의 경우 화면에 출력
          // setMessage(result.sender, result.type, result.message, result.time);
          stompClient.value.send(`/pub/chat.message.${roomId.value}`, {}, JSON.stringify(result));
          break;
        case "MEDIA":
          console.log('이미지')
          // MEDIA 메시지의 경우 화면에 이미지로 출력
          stompClient.value.send(`/pub/chat.message.${roomId.value}`, {}, JSON.stringify(result));
          setTimeout(() => {
            nextTick(() => {
              messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
            });
          }, 300);
          // setMessage(result.type, result.message);
          break;
        case "TYPE":
             stompClient.value.send(`/pub/chat.typing.${roomId.value}`, {}, JSON.stringify(result));
              break;
        default:
          console.log('알수없음')
          setMessage('unknown', result.type);
      }
    } else {
      console.log('invalid')
      setMessage('invalid', null);
    }
  } catch (error) {
    console.log('에러')
    setMessage('error', error);
  }
}

function setMessage(sender, type, message, time) {
  messages.value.push({ sender, type, message, time }); // messages 배열에 새로운 채팅 메시지 추가
}

function setNewMessage(sender, type, message, time) {
  messages.value.unshift({ sender, type, message, time });
}

async function handleFileUpload(event) {
  const file = event.target.files[0];
  if (file) {
    try {
      const formData = new FormData();
      formData.append('file', file);

      // Axios를 사용하여 파일 업로드 엔드포인트에 POST 요청 보내기
      const response = await axios.post(`https://i10a705.p.ssafy.io/api/v1/upload`, formData, {
        headers: {
          Authorization: getCookie("Authorization"),
           refreshToken: getCookie("refreshToken"),
          'Content-Type': 'multipart/form-data'
        }
      });

      // 파일 업로드가 성공하면 파일 URL을 받아옴
      const fileUrl = response.data;

      // 받아온 파일 URL을 이용하여 처리 (예: 이미지 메시지 전송 등)
      const imageMessageString = `{"type": "MEDIA", "roomId" : ${roomId.value}, "sender" : ${userPk.value}, "message": "${fileUrl}", "time" : ""}`;
      handleMessage(imageMessageString);
    } catch (error) {
      console.error('파일 업로드 실패:', error);
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
    await axios.post(`${pref.app.api.host}/chatroom/invite/${roomId.value}?guests=${newGuests.value}`,null,{
      headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
    });
    console.log("Users invited successfully");
    fetchData();
  } catch (error) {
    console.error("Failed to invite chat room:", error);
  }
};

const fetchData = async () => {
  try {
    const response = await axios.get(`${pref.app.api.host}/chatroom/list/users/${roomId.value}`,{    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },});
    const users = response.data;
    // chatRoomUsers 배열 초기화
    chatroomUsers.value = [];
    // chatRoomUsers 배열에 각 사용자의 정보 추가
    for (const user of users) {
      const pkInfo = await axios.get(`https://i10a705.p.ssafy.io/api/v1/user/getUUID?pk=${user.userPk}`);
      const userInfo = await axios.post(`https://i10a705.p.ssafy.io/api/v1/user/get/${pkInfo.data}`,{
        headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
      });
      chatroomUsers.value.push(userInfo.data);
    }
  } catch (error) {
    console.error("Failed to fetch chat rooms:", error);
  }
};

const openOriginalImage = (imageUrl) => {
  window.open(imageUrl, '_blank', 'width=800,height=600,resizable=yes,scrollbars=yes');
};

const getNickname = (userPk) => {
  for (const user of chatroomUsers.value) {
    if (String(user.userPk) === String(userPk)) {
      return user.nickname;
    }
  }
  return '(알수없음)';
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

.chatting-container{
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
  max-height: 140px;
  margin-top: 20px;
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
