<template>
  <div>
    <NavBar/>
    <!-- <TabItem /> -->
    <!-- 내 피드 페이지(마이페이지)-->
    <div class="my-feed">
      <!-- style="padding-left:100px; padding-right:100px" -->

      <!-- 첫번째 div -->
      <div class="header">
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기">
        </div>
        <div>
          <!-- <h3>{{ getUserName(feed.USER_PK) }}</h3> -->
          <h3>닉네임</h3>
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
        <!-- 프로필 이미지 가져오기 -->
        <div class="profile-img-container">
          <!-- <img src="@/assets/img/capture.png" alt="프로필 이미지" class="profile-img"> -->
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
        <div v-for="feed in feeds" :key="feed.FEED_ID" class="feed-item">
          <!-- 피드 내용 및 요소들을 표시 -->
          <img :src="feed.THUMBNAIL_URL" alt="피드 이미지(썸네일)" class="feed-image">
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import TabItem from "@/layouts/TabItem.vue";
import NavBar from "@/layouts/NavBar.vue";
import { useRouter, useRoute } from "vue-router";


const router = useRouter();

const goBack = function () {
  router.go(-1)
}


// 가상의 피드 데이터 예시
// const feeds = ref([
//   {
//     FEED_ID: 1,
//     USER_PK: 1,
//     SONG_ID: 3,
//     CONTENT: "오랜만에 빅뱅 노래 불러봄",
//     THUMBNAIL_URL: "src/assets/icon/logo.png",
//     VIDEO_URL: "your_video_url.mp4",
//     VIDEO_LENGTH: "190",
//     STATUS: "2",
//     TOTAL_POINT: "20000"
//   },
//   {
//     FEED_ID: 2,
//     USER_PK: 1,
//     SONG_ID: 7,
//     CONTENT: "평가 좀 해주세요",
//     THUMBNAIL_URL: "src/assets/icon/web_light_sq_ctn@1x.png",
//     VIDEO_URL: "your_video_url2.mp4",
//     VIDEO_LENGTH: "170",
//     STATUS: "0",
//     TOTAL_POINT: "5000"
//   }
// ])



</script>

<style scoped>
.profile-img-container {
  width: 150px;
  height: 150px;
  background-image: url("@/assets/img/capture.png");
  /* object-fit : contain; */
  border-radius: 25px;
  background-size: cover;
  background-position: center;
  display: flex; /* Flexbox 사용 */
  justify-content: center; /* 수평 정렬을 위한 가로 중앙 정렬 */
  align-items: center; /* 수직 정렬을 위한 세로 중앙 정렬 */
}
/* .profile-img-container {
    width: 20%;
    height: auto;
    display: block;
  } */

.my-feed {
  /* padding: 20px; */
  padding-left: 200px;
  padding-right: 200px;
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
  width: 100%;
  height: auto;
  border-radius: 30%;
  display: block; /* 인라인 요소 간격 제거 */
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



