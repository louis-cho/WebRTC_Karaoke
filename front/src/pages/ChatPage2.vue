<template>
  <div class="dm-container">
    <div class="dm-messages" ref="messagesContainer">
      <div v-for="(message, index) in messages" :key="index" class="message">
        <span :class="{ 'my-message': message.from === 'me' }">{{ message.content }}</span>
      </div>
    </div>
    <div class="dm-input">
      <textarea v-model="newMessage" @keydown.enter.prevent="sendMessage" placeholder="메시지를 입력하세요..."></textarea>
    </div>
  </div>
</template>

<script>
// export default {
//   data() {
//     return {
//       messages: [],
//       newMessage: ''
//     };
//   },
//   methods: {
//     sendMessage() {
//       if (this.newMessage.trim() !== '') {
//         this.messages.push({ from: 'me', content: this.newMessage });
//         this.newMessage = '';

//         // 스크롤을 항상 아래로 내림
//         this.$nextTick(() => {
//           this.$refs.messagesContainer.scrollTop = this.$refs.messagesContainer.scrollHeight;
//         });
//       }
//     }
//   }
// };

import { ref, nextTick } from "vue";

export default {
  setup() {
    const messages = ref([]);
    const newMessage = ref('');

    const sendMessage = () => {
      if (newMessage.value.trim() !== '') {
        messages.value.push({ from: 'me', content: newMessage.value });
        newMessage.value = '';

        // 스크롤을 항상 아래로 내림
        nextTick(() => {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
        });
      }
    };

    const messagesContainer = ref(null);

    return {
      messages,
      newMessage,
      sendMessage,
      messagesContainer
    };
  }
};
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

/* .message {
  margin-bottom: 10px;
} */

.my-message {
  color:black; /* 내 메시지 색상 */
}

.dm-input {
  padding: 10px;
}

textarea {
  width: 100%;
  resize: none;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 10px;
}

</style>
