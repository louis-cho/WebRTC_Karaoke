<template>
  <div>
    <div>
      <!-- 네브바 같은거? -->
    </div>
    <div>
        <!-- 프로필이미지 -->
        <!-- 친구 수 -->
        <!-- 프로필 보기 -->
    </div>
    <div>
      <!-- 채팅창 -->
      <div>
        <template v-for="(chatMessage, index) in messages" :key="index">
          <div class="message_container" v-if="chatMessage.type === 'text'">
            {{ chatMessage.content }}
          </div>
          <div class="message_container" v-else-if="chatMessage.type === 'image'">
            <img :src="chatMessage.content" alt="Image">
          </div>
          <div class="message_container" v-else>
            Unknown message type: {{ chatMessage.type }}
          </div>
        </template>
      </div>
    </div>
    <div class="input_container">

      <div>
        <div>
          <label>
            <input type="text" v-model="inputText">
            <button @click="sendMessage">입력</button>
          </label>
        </div>
        <div>
          <label>
            <!-- Image Upload:  -->
            <input type="file" @change="handleFileUpload">
          </label>
        </div>
      </div>

    </div>
  </div>

    <!-- <div>
      <template v-if="messageType === 'text'">
        {{ messageContent }}
      </template>
      <template v-else-if="messageType === 'image'">
        <img :src="messageContent" alt="Image">
      </template>
      <template v-else>
        Unknown message type: {{ messageType }}
      </template>
    </div> -->
</template>


<script setup>
import { ref } from "vue"
import logoImage from "@/assets/icon/logo1-removebg-preview.png"

const messages = ref([]) // messages 배열 추가

// const messageType = ref(null)
// const messageContent = ref(null)
const inputText = ref("")
const imageURL = ref("")

function handleMessage(msg) {
  try {
    // 문자열을 객체로 변환
    const result = JSON.parse(msg);

    // result 객체에 type 및 content 필드가 있는지 확인
    if (result.type != null && result.content != null) {
      // switch 문을 통한 분기
      switch (result.type) {
        case "text":
          console.log('텍스트')
          // text 메시지의 경우 화면에 출력
          setMessage(result.type, result.content);
          break;
        case "image":
          console.log('이미지')
          // image 메시지의 경우 화면에 이미지로 출력
          setMessage(result.type, result.content);
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

// function setMessage(type, content) {
//   messageType.value = type;
//   messageContent.value = content;
// }

function setMessage(type, content) {
  messages.value.push({ type, content }); // messages 배열에 새로운 채팅 메시지 추가
}

function sendMessage() {
  const textMessageString = `{"type": "text", "content": "${inputText.value}"}`;
  handleMessage(textMessageString);

  // 입력창 초기화
  inputText.value = "";
}

// 파일 업로드 처리 함수
function handleFileUpload(event) {
  const file = event.target.files[0];
  if (file) {
    // 선택한 파일을 이미지로 표시
    const reader = new FileReader();
    reader.onload = () => {
      const imageMessageString = `{"type": "image", "content": "${reader.result}"}`;
      handleMessage(imageMessageString);
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
.message_container {
  text-align: right;
}

.input_container {
  width: 100%;
  position: fixed;
  bottom: 0;
  display: flex;
  justify-content: center;
}

</style>
