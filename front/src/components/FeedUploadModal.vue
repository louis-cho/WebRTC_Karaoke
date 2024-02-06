<template>
  <div>
    <q-btn label="피드 업로드 모달" @click="openModal" />

    <q-dialog v-model="modalOpen">
      <q-card class="modal-card">
        <q-card-section class="modal-header">
          <div class="user-info">
            <!-- 프로필 이미지 가져오기... -->
            <q-avatar class="img-container" />
            <q-item-section>
              <!-- 닉네임 가져오기 -->
              <q-item-label>닉네임</q-item-label>
            </q-item-section>
          </div>
          <span><strong>게시물 업로드</strong></span>
          <span class="upload-label" @click="UploadFeed"><strong>업로드</strong></span>
        </q-card-section>
        <hr>

        <q-card-section class="display-flex-row">
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
            <div>
              <!-- 공개범위 토글 -->
              <q-select
                v-model="selectedOption"
                :options="privacyOptions"
                emit-value
                map-options
              />
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
const privacyOptions = ['전체공개', '친구공개', '비공개']

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

const uploadFeed = () => {
  // 피드 업로드 로직..
  console.log('Uploading feed...');

  // 피드 업로드를 후 모달 닫기
  closeModal()
};

const closeModal = () => {
  modalOpen.value = false;
};

// const onItemClick = (option) => {
//   selectedOption.value = option;
// };
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
  margin-bottom: 0px;
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
  margin-bottom: 0px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: vertical; /* 세로로 길게 조절 가능하도록 함 */
  min-height: 80px; /* 최소 높이 설정 */
}

</style>
