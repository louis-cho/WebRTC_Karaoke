<template>
  <div>
    <TabItem />
    <!-- 내 피드 페이지(마이페이지)-->
    <div class="my-feed">
      <!-- style="padding-left:100px; padding-right:100px" -->

      <!-- 첫번째 div -->
      <div class="header">
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기">
        </div>
        <div>
          <h3>JennierubyJane</h3>
        </div>
        <div class="icons">
          <div><img src="@/assets/icon/send.png" alt="dm"></div>
          <div><img src="@/assets/icon/wallet.png" alt="지갑"></div>
          <div><img src="@/assets/icon/setting.png" alt="설정"></div>
        </div>
      </div>
      <hr>

      <!-- 두번째 div -->
      <div class="profile">
        <div class="center">
          <img src="@/assets/img/capture.png" alt="프로필 이미지" class="profile-img">
        </div>
        <div class="info">
          <div class="stats">
            <div><p>게시글</p><span>0</span></div>
            <div><p>댓글</p><span>0</span></div>
            <div><p>좋아요</p><span>0</span></div>
            <div><p>친구</p><span>0</span></div>
          </div>
          <div class="bio">
            <p>For athletes, high altitude produces two contradictory effects on performance. For explosive events.</p>
          </div>
          <div class="actions">
            <q-btn color="primary" label="좋아요 관리" />
            <q-btn color="purple" label="댓글 관리" />
          </div>
        </div>
      </div>
      <hr>

      <!-- 세번째 div -->
      <div class="feed-list">
        <!-- 각 피드 항목을 감싸는 그리드 레이아웃 부모 요소 -->
        <div v-for="feed in feeds" :key="feed.id" class="feed-item">
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

const router = useRouter();

const goBack = function () {
  router.go(-1);
};

// 가상의 피드 데이터 예시
const feeds = ref([
  { id: 1, imageUrl: "src/assets/icon/logo.png" },
  { id: 2, imageUrl: "src/assets/icon/web_light_sq_ctn@1x.png" },
  { id: 3, imageUrl: "src/assets/img/capture3.png" },
  { id: 4, imageUrl: "src/assets/img/capture3.png" },
  { id: 5, imageUrl: "src/assets/img/capture3.png" },
  { id: 6, imageUrl: "src/assets/img/capture3.png" },
  // ... 다른 피드 데이터들 ...
]);

</script>

<style scoped>
.my-feed {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header img {
  cursor: pointer;
}

.icons img {
  margin-left: 10px;
}

.profile {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  margin: 20px 0;
}

.profile-img {
  border-radius: 50%;
  width: 50%;
  height: 50%;
}

.stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 10px;
}

.stats div {
  text-align: center;
}

.stats p {
  font-size: 14px;
  margin: 5px 0;
}

.stats span {
  font-size: 18px;
  font-weight: bold;
}

.bio {
  text-align: center;
  margin-bottom: 20px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.actions q-btn {
  margin: 0 10px;
}

/* 그리드 레이아웃 스타일 */
.feed-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.feed-item {
  display: flex;
  justify-content: center;
}

.feed-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

</style>



