<template>
  <div>
    <q-btn label="Open Modal" @click="openModal" />

    <q-dialog v-model="modalOpen">
      <q-card class="modal-card">
        <q-card-section class="modal-header">
          <div class="user-info">
            <q-avatar class="img-container" />
            <q-item-section>
              <q-item-label>닉네임</q-item-label>
            </q-item-section>
          </div>
          <span><strong>게시물 업로드</strong></span>
          <span class="upload-label"><strong>업로드</strong></span>
        </q-card-section>
        <hr>

        <q-card-section class="display-flex-row">
          <!-- Modal 내용 추가 -->
          <div class="video-container">
            <!-- 영상 가져오기 -->
            <video controls width="100%" height="100%" v-if="videoUrl">
              <source :src="videoUrl" type="video/mp4">
            </video>
            <div v-else>No video available</div>
          </div>

          <div class="display-flex-column">
            <div>
              <!-- <input class="caption-input" type="text" placeholder="문구 입력..."> -->
              <textarea class="caption-input" placeholder="문구 입력..."></textarea>
            </div>
            <!-- 공개범위 토글 -->
            <div class="privacy-dropdown">
              <q-btn-dropdown :label="selectedOption" style="width: 100%;">
                <q-list>
                  <q-item clickable v-close-popup @click="onItemClick('전체공개')">
                    <q-item-section>
                      <q-item-label>전체공개</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item clickable v-close-popup @click="onItemClick('친구공개')">
                    <q-item-section>
                      <q-item-label>친구공개</q-item-label>
                    </q-item-section>
                  </q-item>
                  <q-item clickable v-close-popup @click="onItemClick('비공개')">
                    <q-item-section>
                      <q-item-label>비공개</q-item-label>
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-btn-dropdown>
            </div>
          </div>
        </q-card-section>

        <!-- <q-card-actions align="right">
          <q-btn label="게시" color="primary" @click="closeModal" />
        </q-card-actions> -->
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const modalOpen = ref(false);
const selectedOption = ref('공개범위');
const videoUrl = ref('');

const getVideoUrl = () => {
  // 비디오 URL을 가져오는 비동기 작업
  setTimeout(() => {
    videoUrl.value = 'https://your-s3-bucket-url/your-video.mp4'; // 실제 S3 버킷 URL로 대체
  }, 1000);
};

onMounted(() => {
  getVideoUrl();
});

const openModal = () => {
  modalOpen.value = true;
};

const closeModal = () => {
  modalOpen.value = false;
};

const onItemClick = (option) => {
  selectedOption.value = option;
};
</script>

<style scoped>
.modal-card {
  max-width: 400px;
  margin: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
}

.upload-label {
  color: royalblue;
}

.display-flex-row {
  display: flex;
  flex-direction: row;
}

.display-flex-column {
  display: flex;
  flex-direction: column;
}

.video-container {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 10px;
}

.caption-input {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.privacy-dropdown {
  padding-left: 0;
  padding-right: 0;
}

.img-container {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-image: url("@/assets/img/capture.png");
  background-size: cover;
  background-position: center;
  margin-right: 10px;
}


.caption-input {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: vertical; /* 세로로 길게 조절 가능하도록 함 */
  min-height: 80px; /* 최소 높이 설정 */
}

/* 공개범위 드롭다운 스타일 */
.privacy-dropdown .q-btn-inner {
  padding: 0; /* 기존 패딩 제거 */
}

.privacy-dropdown .q-btn-dropdown-icon {
  margin-left: 5px; /* 드롭다운 화살표와의 간격 설정 */
}

/* 드롭다운 메뉴 스타일 (평면적인 스타일) */
/*.q-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
}*/

/*.q-item {
  padding: 8px 16px;
  transition: background-color 0.3s;
}

.q-item:hover {
  background-color: #f0f0f0;
}*/
</style>
