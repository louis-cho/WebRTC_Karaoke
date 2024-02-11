<template>
  <div class="dm-container">
    <NavBar />
    <!-- 채팅창 내역 -->
    <div class="dm-messages" ref="messagesContainer" @scroll="handleScroll">
      <div v-for="(message, index) in messages" :key="index">

        <div v-if="message.type === 'TALK'">
          {{ message.sender }}
          {{ message.message }}
        </div>
        <div v-else-if="message.type === 'image'">
          <img :src="message.message" alt="Image">
        </div>
        <div v-else>
          Unknown message type: {{ message.type }}
        </div>
      </div>
    </div>

    <!-- 메시지 입력창 -->
    <!-- <div class="input_class">
      <textarea v-model="newMessage" @keydown.enter.prevent="sendMessage" placeholder="메시지를 입력하세요..."></textarea>
      <input type="file" ref="fileInput" @change="handleFileUpload">
    </div> -->

    <div class="img_class1">
      <textarea v-model="newMessage" @keydown.enter.prevent="sendMessage" placeholder="메시지를 입력하세요..."></textarea>
      <label for="fileInput" class="img_label">
        <img src="@/assets/icon/image.png" alt="File Icon" class="img_class2">
      </label>
      <input type="file" ref="fileInput" id="fileInput" @change="handleFileUpload" style="display: none;">
    </div>

  </div>
</template>

<script setup>
import Stomp from "stompjs";
import { useCounterStore } from '@/stores/counter'
import { ref, nextTick, onMounted } from "vue";
import { useRoute } from 'vue-router';
import axios from 'axios';
import NavBar from "@/layouts/NavBar.vue";
import logoImage from "@/assets/icon/logo1-removebg-preview.png"

onMounted(async () => {
  userPk.value = route.query.userPk;
  roomId.value = route.params.roomPk;

  const socket = new WebSocket('wss://i10a705.p.ssafy.io/api/ws');
  stompClient.value = Stomp.over(socket);

  stompClient.value.connect({}, () => {
      stompClient.value.subscribe(`/exchange/chat.exchange/room.${roomId.value}`, (message) => {
          handleIncomingMessage(JSON.parse(message.body));
      });
      loadOldMessages().then(() => {
        loadNewMessages().then(() => {
          nextTick(() => {
            messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
          });
        });
      });
  });
});


function handleIncomingMessage(message) {
        if (message) {
          console.log(message.type);
            setMessage(message.sender, message.type, message.message, message.time);
            nextTick(() => {
              messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
            });
        }
    }

function loadOldMessages() {
  // 로딩 중이면 중복 요청 방지
  if (loading) return;
  loading = true;
  return axios.get(`https://i10a705.p.ssafy.io/api/v1/chat/room/${roomId.value}/oldMsg?page=${page}&size=50`)
    .then(response => {
      const oldMessages = response.data;
      if (oldMessages.length === 0) {
         // 빈 배열을 받으면 페이지 끝을 알리는 alert 표시
         alert("마지막 페이지입니다.");
      }
      else if (page === 1){
        oldMessages.forEach(message => {
         message = JSON.parse(message);
         setMessage(message.sender, message.type, message.message, message.time);
        });
        page++; // 다음 페이지로 이동
      }
      else {
       oldMessages.reverse().forEach(message => {
         message = JSON.parse(message);
         setNewMessage(message.sender, message.type, message.message, message.time);
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
  return axios.get(`https://i10a705.p.ssafy.io/api/v1/chat/room/${roomId.value}/newMsg`)
    .then(response => {
      const newMessages = response.data;
      newMessages.forEach(message => {
        message = JSON.parse(message);
        setNewMessage(message.sender, message.type, message.message, message.time);
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
const route = useRoute();
const stompClient = ref(null);

let page = 1; // 초기 페이지 설정
let loading = false; // 페이지 로딩 상태

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
        case "image":
          console.log('이미지')
          // image 메시지의 경우 화면에 이미지로 출력
          setMessage(result.type, result.message);
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
  messages.value.push({sender, type, message, time}); // messages 배열에 새로운 채팅 메시지 추가
}

function setNewMessage(sender, type, message, time){
  messages.value.unshift({sender, type, message, time});
}

function handleFileUpload(event) {
  const file = event.target.files[0];
  if (file) {
    // 선택한 파일을 이미지로 표시
    const reader = new FileReader();
    reader.onload = () => {
      const imageMessageString = `{"type": "image", "message": "${reader.result}"}`;
      handleMessage(imageMessageString);

      // 이미지 업로드 시 스크롤을 항상 아래로 내림
      nextTick(() => {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
      });
    };
    reader.readAsDataURL(file);

    // 선택한 파일을 저장
    selectedFile.value = file;
  }
}


// const textMessageString = '{"type": "text", "content": "Hello, World!"}';
// const imageMessageString = `{"type": "image", "content": "${logoImage}"}`
// const invalidMessageString = '{"type": "text"}'; // content 필드가 누락된 메시지

// handleMessage 함수 호출
// handleMessage(textMessageString);
// handleMessage(imageMessageString);
// handleMessage(invalidMessageString);

</script>

<style scoped>
.dm-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  text-align: right;
}

.dm-messages {
  flex: 1;
  overflow-y: auto;
  /* padding: 10px; */
  transition: scrollTop 0.3s ease;
}

.my-message {
  color: black;
}

textarea {
  width: 100%;
  resize: none;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 10px;
}

.input_class{
  display:flex;
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
</style>
