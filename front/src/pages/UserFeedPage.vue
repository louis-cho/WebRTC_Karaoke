<template>
  <div>
    <nav-bar />
    <!-- 내 피드 페이지(마이페이지/혹은 타인 페이지)-->
    <div class="my-feed">
      <!-- style="padding-left:100px; padding-right:100px" -->
      <!-- 첫번째 div -->
      <div class="header">
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기" />
        </div>
        <div>
          <h3 v-if="user">{{ user.nickname }}</h3>
        </div>
        <!-- 게시글 작성자와 로그인 유저가 다르다면 -->
        <div class="icons" v-if="user.userKey !== uuid">
          <div><img src="@/assets/icon/send.png" alt="dm" /></div>
        </div>
        <div class="icons" v-else>
          <div></div>
        </div>
      </div>
      <hr />

      <!-- 두번째 div -->
      <div class="profile">
        <!-- 프로필 이미지 가져오기 -->
        <div
          class="profile-img-container"
          :style="{
            backgroundImage: `url(${(
              user.profileImgUrl || 'https://picsum.photos/200'
            ).trim()})`,
          }"
        >
          <!-- <img src="@/assets/img/capture.png" alt="프로필 이미지" class="profile-img"> -->
        </div>
        <div class="info" >
          <div class="stats">
            <div v-if="feedLength">
              <p>게시글</p>
              <span>{{ feedLength }}</span>
            </div>
            <div v-else>
              <p>게시글</p>
              <span>0</span>
            </div>
            <div>
              <p>댓글</p>
              <span>{{ totalCommentCount }}</span>
            </div>
            <div>
              <p>좋아요</p>
              <span>{{ totalLikeCount }}</span>
            </div>
            <!-- <div>
              <p>친구</p>
              <span> {{ FriendCount }} </span>
            </div> -->
          </div>
          <div class="bio">
            <p>{{ user.introduction }}</p>
          </div>
          <!-- <div class="actions"> -->
          <!-- <q-btn color="primary" label="좋아요 관리" /> -->
          <!-- <q-btn color="purple" label="댓글 관리" /> -->
          <!-- <div></div> -->
          <!-- <div></div> -->
          <!-- </div> -->
        </div>
      </div>
      <hr />

      <!-- 세번째 div -->
      <div class="feed-list">
        <!-- 각 피드 항목을 감싸는 그리드 레이아웃 부모 요소 -->
        <div
          v-for="feed in personalFeeds"
          :key="feed.feedId"
          class="feed-item"
          @click="goFeedDetail(feed.feedId)"
        >
          <!-- 피드 내용 및 요소들을 표시 -->
          <!-- <img v-if="feed" :src="feed.thumbnailUrl" alt="피드 이미지(썸네일)" class="feed-image"> -->
          <!-- {{ feed.videoUrl }} -->
          <video controls width="100%" ref="videoPlayer">
            <source :src="feed.videoUrl" type="video/mp4" />
          </video>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeMount, computed } from "vue";
import NavBar from "@/layouts/NavBar.vue";
import { useRouter, useRoute } from "vue-router";
import { getFeedsByUser } from "@/js/feed/feed.js";
import { fetchUser } from "@/js/user/user.js";
import { fetchLikeCount } from "src/js/like/like";
import { fetchCommentCount } from "@/js/comment/comment.js";
import { fetchFriendList, fetchFriendCount } from "@/js/friends/friends.js";
import useCookie from "@/js/cookie.js";

const { setCookie, getCookie, removeCookie } = useCookie();
const router = useRouter();
const route = useRoute();
// const uuid = ref("");
const uuid = ref(route.params.userUUID); // 동적인 userPk를 사용

// console.log('이거지금 uuid',uuid.value)

const goBack = function () {
  router.go(-1);
};

const goFeedDetail = (feedId) => {
  router.push({ name: "feed_detail", params: { feedId } });
};

const personalFeeds = ref([]);
const user = ref(null);
const FriendCount = ref("");
let page = 0;
const amount = 10;

onBeforeMount(async () => {
  // uuid.value = getCookie('uuid')
  await fetchPersonalFeedData();
  await fetchUserData();
  await getFriendCount();
  // console.log("비포마운트끝")
});

//유저 정보 가져오기(피드작성자)
const fetchUserData = async () => {
  try {
    const fetchedUser = await fetchUser(uuid.value);
    user.value = fetchedUser;
    console.log("유저정보", user.value);
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

//유저별 피드들 가져오기
const fetchPersonalFeedData = async () => {
  try {
    console.log("uuid.value? ", uuid.value);
    const feeds = await getFeedsByUser(uuid.value);
    for (let elem of feeds) {
      elem.likeCount = await fetchLikeCount(elem.feedId);
      elem.commentCount = await fetchCommentCount(elem.feedId);
    }

    personalFeeds.value = feeds;
    console.log("퍼스널피드", personalFeeds.value);
  } catch (error) {
    console.error("Error fetching personal feeds:", error.message);
  }
};

const getFriendCount = async () => {
  try {
    const FriendCounts = await fetchFriendCount();
    FriendCount.value = FriendCounts;
    console.log('이거뽑을래',FriendCount.value);
  } catch (error) {
    console.error("Error fetching personal feeds:", error.message);
  }
};

//피드 개수
const feedLength = computed(() => personalFeeds.value.length);

const totalLikeCount = computed(() => {
  return personalFeeds.value.reduce((total, feed) => total + feed.likeCount, 0);
});

const totalCommentCount = computed(() => {
  return personalFeeds.value.reduce(
    (total, feed) => total + feed.commentCount,
    0
  );
});
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
  padding-left: 400px;
  padding-right: 400px;
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
  /* justify-content: space-evenly; */
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

.info {
  margin-left: 150px; /* Adjust the margin as needed */
  flex-grow: 1; /* Allow the info div to grow and take remaining space */
  display: flex;
  flex-direction: column;
}
</style>
