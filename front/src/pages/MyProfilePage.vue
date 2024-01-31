<template>
  <div>
    <TabItem/>
    <!-- 내 피드 페이지 -->
    <div>
      <!-- 첫번째 div -->
      <div class="first">
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기">
        </div>
        <div>
          <!-- 수정 필요 -->
          <h3>JennierubyJane</h3>
        </div>
        <div class="first">
          <div>
            <img src="@/assets/icon/send.png" alt="dm">
          </div>
          <div>
            <img src="@/assets/icon/wallet.png" alt="지갑">
          </div>
          <div>
            <img src="@/assets/icon/setting.png" alt="설정">
          </div>
        </div>
      </div>
      <hr>
      <!-- 두번째 div -->
      <div>
        <div class="row-container">
          <div>
            <!-- 수정 필요 -->
            <img src="src/assets/img/capture.png" style='border-radius: 20%; width: 200px; height: 200px;' alt="프로필 이미지">
          </div>

          <div>
            <div class="row-container">
              <div class="column-container">
                <!-- 수정 -->
                <div>게시글 개수?</div>
                <div><h6>게시글</h6></div>
              </div>
              <div class="column-container">
                <!-- 수정 -->
                <div>댓글 개수?</div>
                <div><h6>댓글</h6></div>
              </div>
              <div class="column-container">
                <!-- 수정 -->
                <div>좋아요 개수?</div>
                <div><h6>좋아요</h6></div>
              </div>
              <div class="column-container">
                <!-- 수정 -->
                <div>친구 개수?</div>
                <div><h6>친구</h6></div>
              </div>
            </div>
            <div>
              <h5>For athletes, high altitude produces two contradictory effects on performance. For explosive events </h5>
            </div>
            <div class="row-container">
              <!-- 수정 기능도 안 만듦 -->
              <div><button>좋아요 관리</button></div>
              <div><button>댓글 관리</button></div>
            </div>
          </div>

        </div>
      </div>
      <hr>

      <!-- 세번째 div -->
      <div class="feed-list">
        <!-- 각 피드 항목을 감싸는 그리드 레이아웃 부모 요소 -->
        <div v-for="feed in feeds" :key="feed.id" class="feed-item" style="display: flex; justify-content:center;">
          <!-- 피드 내용 및 요소들을 표시 -->
          <img :src="feed.imageUrl" alt="피드 이미지" class="feed-image">
        </div>
      </div>


    </div>

  </div>
</template>

<script setup>
import { ref } from "vue";
import TabItem from "@/layouts/TabItem.vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter()

const goBack = function() {
  router.go(-1);
}


// 가상의 피드 데이터 예시
const feeds = ref([
  { id: 1, imageUrl: "src/assets/icon/logo.png" },
  { id: 2, imageUrl: "src/assets/icon/web_light_sq_ctn@1x.png" },
  { id: 3, imageUrl: "src/assets/img/capture3.png" },
  { id: 4, imageUrl: "src/assets/img/capture3.png" },
  // ... 다른 피드 데이터들 ...
]);

</script>

<style scoped>
.first {
  display:flex;
  flex-direction: row;
  justify-content: space-between;
}

.row-container {
  display: flex;
  flex-direction: row;
}

.column-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

/* 그리드 레이아웃 스타일 */
.feed-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 한 줄에 3개의 열 */
  gap: 20px;
  /* 각 피드 사이의 간격 조절 */
}

/* .feed-item {
  각 피드 항목 스타일
  예시: width, height, padding, border 등을 설정
} */

/* .feed-image { */
  /* width: 30%;  */
  /* 이미지를 부모 요소의 100%로 설정하여 크기를 조절 */
  /* height: auto;  */
  /* 이미지의 가로세로 비율을 유지하도록 설정 */
/* } */

.feed-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.width_5 {
  width: 5%;
}
.width_10 {
  width: 10%;
}
.width_15 {
  width: 15%;
}
.width_20 {
  width: 20%;
}
.width_25 {
  width: 25%;
}
.height_10 {
  height:10%
}
</style>
