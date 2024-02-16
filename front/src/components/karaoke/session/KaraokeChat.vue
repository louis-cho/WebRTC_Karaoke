<template>
  <q-dialog v-model="store.toggleModals['karaoke-chat']">
    <q-card>
      <q-card-section>
        <!-- 방에 들어갔을 때 같이 보이게 될 채팅창 -->
        <div id="chat-container" class="outer-border q-pa-md">
          <q-card class="q-mb-md chat-window">
            <q-card-section class="q-pa-md dark-bg" style="min-width: 512px">
              <div class="chat-title q-mb-md">
                <h2 class="q-mb-none title-font-size">
                  {{ pref.app.kor.karaoke.session.chatting }}
                </h2>
                <div class="section-divider"></div>
              </div>
              <ul
                class="chat-history q-mb-md"
                ref="messagesContainer"
                style="max-height: 300px; overflow-y: scroll"
              >
                <li
                  v-for="(message, index) in store.messages"
                  :key="index"
                  class="message-item q-py-md dark-item-bg"
                >
                  <strong class="message-username dark-text"
                    >{{ message.username }}:</strong
                  >
                  {{ message.message }}
                </li>
              </ul>
            </q-card-section>
          </q-card>
        </div>

        <form id="chat-write" class="q-mt-md" @submit.prevent="sendMessage">
          <q-input
            type="text"
            :placeholder="pref.app.kor.karaoke.session.message"
            v-model="store.inputMessage"
            outlined
            dense
            class="inline-input"
            @keydown.enter="sendMessage"
          />
          <q-btn
            @click="sendMessage"
            color="primary"
            :label="pref.app.kor.karaoke.session.send"
          />
        </form>

        <q-card-section class="q-mt-sm q-mb-sm float-right">
          <q-btn
            @click="closeModal"
            color="negative"
            :label="pref.app.kor.karaoke.list.close"
          />
        </q-card-section>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, nextTick, onMounted, watch } from "vue";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import pref from "@/js/config/preference.js";

const store = useKaraokeStore();
const messagesContainer = ref(null);

// 채팅창 구현을 위한 함수 제작
function sendMessage(event) {
  event.preventDefault();
  if (store.inputMessage.trim()) {
    // 다른 참가원에게 메시지 전송하기
    store.session.signal({
      data: JSON.stringify({
        username: store.userName,
        message: store.inputMessage,
      }), // 메시지 데이터를 문자열로 변환해서 전송
      type: "chat", // 신호 타입을 'chat'으로 설정
    });
    store.inputMessage = "";
  }
}

function closeModal() {
  store.toggleModals["karaoke-chat"] = false;
}

watch(
  messagesContainer,
  (newValue) => {
    if (newValue) {
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop =
          messagesContainer.value.scrollHeight;
      }
    }
  },
  { deep: true, immediate: true }
);

watch(
  store.messages,
  (newValue) => {
    if (newValue) {
      nextTick(() => {
        scrollMessagesContainer();
      });
    }
  },
  { deep: true, immediate: true }
);

const scrollMessagesContainer = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};
</script>

<style scoped>
.outer-border {
  border: 2px solid #3498db; /* 테두리 색상을 변경하세요 */
  border-radius: 10px; /* 라운드 코너를 적용하세요 */
  padding: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 그림자 효과를 추가하세요 */
}
.chat-window {
  background-color: #2c3e50; /* 어두운 배경색 */
  border-radius: 8px;
  color: #ecf0f1; /* 전체 텍스트 색상 */
}
/* 제목 스타일링을 위한 CSS */
.chat-title {
  text-align: left;
  margin-bottom: 20px;
}
/* 제목 폰트 크기 조절을 위한 CSS */
.title-font-size {
  font-size: 2rem; /* 원하는 크기로 조절하세요 */
}
/* 제목 밑에 줄을 추가하는 CSS */
.section-divider {
  border-bottom: 1px solid #ccc; /* 줄의 스타일 및 색상을 원하는대로 조절하세요 */
  margin-bottom: 10px; /* 원하는 간격으로 조절하세요 */
}
.dark-bg {
  background-color: #2c3e50 !important; /* !important로 우선순위 부여 */
}

.chat-history {
  list-style-type: none;
  padding: 0;
}

.message-item {
  background-color: #34495e; /* 어두운 메시지 배경색 */
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 12px;
  margin-bottom: 10px;
}

.dark-item-bg {
  background-color: #34495e !important; /* !important로 우선순위 부여 */
}

.message-username {
  font-weight: bold;
  color: #3498db; /* 사용자 이름 색상 */
}

.dark-text {
  color: #3498db !important; /* !important로 우선순위 부여 */
}
.inline-input {
  display: inline-block;
  width: calc(100% - 80px); /* 적절한 너비를 조절하세요 */
  margin-right: 10px; /* 필요한 간격을 조절하세요 */
}
</style>
