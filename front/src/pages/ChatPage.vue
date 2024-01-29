<template>
   <div>
      <template v-if="messageType === 'text'">
        {{ messageContent }}
      </template>
      <template v-else-if="messageType === 'image'">
        <img :src="messageContent" alt="Image">
      </template>
      <template v-else>
        Unknown message type: {{ messageType }}
      </template>
    </div>
</template>


<script setup>
import { ref } from "vue";
import logoImage from "@/assets/icon/logo1-removebg-preview.png"

const messageType = ref(null);
const messageContent = ref(null);

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

function setMessage(type, content) {
  messageType.value = type;
  messageContent.value = content;
}


const textMessageString = '{"type": "text", "content": "Hello, World!"}';
// const imageMessageString = '{"type": "image", "content": "https://via.placeholder.com/150"}'
const imageMessageString = `{"type": "image", "content": "${logoImage}"}`
const invalidMessageString = '{"type": "text"}'; // content 필드가 누락된 메시지

// handleMessage 함수 호출
handleMessage(textMessageString);
handleMessage(imageMessageString);
handleMessage(invalidMessageString);
</script>

<style scoped>

</style>
