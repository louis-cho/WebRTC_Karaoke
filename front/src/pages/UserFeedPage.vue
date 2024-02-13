<template>
  <div>
    <NavBar/>
    <!-- <TabItem /> -->
    <!-- 내 피드 페이지(마이페이지/혹은 타인 페이지)-->
    <div class="my-feed">
      <!-- style="padding-left:100px; padding-right:100px" -->

      <!-- 첫번째 div -->
      <div class="header">
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기">
        </div>
        <div>
          <h3 v-if="user">{{ user.nickname }}</h3>
        </div>
        <div class="icons" v-if="feedWriterPk !== LoggedUserPK">
          <div><img src="@/assets/icon/send.png" alt="dm"></div>
          <!-- <div><img src="@/assets/icon/wallet.png" alt="지갑"></div> -->
          <!-- <div><img src="@/assets/icon/setting.png" alt="설정"></div> -->
        </div>
        <div class="icons" v-else>
          <div></div>
        </div>
      </div>
      <hr>

      <!-- 두번째 div -->
      <div class="profile">
        <!-- 프로필 이미지 가져오기 -->
        <div class="profile-img-container" :style="{ backgroundImage: `url('${user.profileImgUrl}')` }">
          <!-- <img src="@/assets/img/capture.png" alt="프로필 이미지" class="profile-img"> -->
        </div>
        <div class="info">
          <div class="stats">
            <div v-if="feedLength"><p>게시글</p><span>{{ feedLength }}</span></div>
            <div v-else><p>게시글</p><span>0</span></div>
            <div><p>댓글</p><span>{{ totalCommentCount }}</span></div>
            <div><p>좋아요</p><span>{{ totalLikeCount }}</span></div>
            <div><p>친구</p><span>{{ FriendCount }}</span></div>
          </div>
          <div class="bio">
            <p>{{ user.introduction }}</p>
          </div>
          <div class="actions">
            <q-btn color="primary" label="좋아요 관리" />
            <q-btn color="purple" label="댓글 관리" />
            <!-- <div></div> -->
            <!-- <div></div> -->
          </div>
        </div>
      </div>
      <hr>

      <!-- 세번째 div -->
      <div class="feed-list">
        <!-- 각 피드 항목을 감싸는 그리드 레이아웃 부모 요소 -->
        <div v-for="feed in personalFeeds" :key="feed.feedId" class="feed-item" @click="goFeedDetail(feed.feedId)">
          <!-- 피드 내용 및 요소들을 표시 -->
          <img v-if="feed" :src="feed.thumbnailUrl" alt="피드 이미지(썸네일)" class="feed-image">
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref,onMounted,onBeforeMount,computed } from "vue";
import TabItem from "@/layouts/TabItem.vue";
import NavBar from "@/layouts/NavBar.vue";
import { useRouter, useRoute } from "vue-router";
import { getFeedsByUser } from '@/js/feed/feed.js';
import { fetchUser } from "@/js/user/user.js";
import { fetchLikeCount } from "src/js/like/like";
import { fetchCommentCount } from "@/js/comment/comment.js";
import { fetchFriendCount } from "@/js/friends/friends.js";
import useCookie from "@/js/cookie.js";
import { getUserPk  } from "@/js/user/user.js";

const { setCookie, getCookie, removeCookie } = useCookie();
const router = useRouter();
const route = useRoute()
const LoggedUserPK = ref();
const uuid = ref(null);
const userPk = ref(route.params.userPk); // 동적인 userPk를 사용
const feedWriterPk = ref();


const goBack = function () {
  router.go(-1)
}

const goFeedDetail = (feedId) => {
  router.push({ name: "feed_detail", params: { feedId } });
};

const personalFeeds = ref([]);
const user = ref(null);
const FriendCount = ref('');
let page = 0;
const amount = 10;

onBeforeMount(async () => {
  await fetchPersonalFeedData();
  await fetchUserData();
  await getFriendCount();
  await getLoggedUserPk();
});

//로그인한 유저pk값 가져오기
const getLoggedUserPk = async () => {
  try {
    uuid.value = getCookie("uuid")
    // console.log("UUID:", uuid.value);
    const getCurrentUserPk = await getUserPk(uuid.value);
    console.log(getCurrentUserPk);
    LoggedUserPK.value = getCurrentUserPk
  } catch (error) {
    console.error("오류 발생!!!:", error);
  }
};

//유저 정보 가져오기
const fetchUserData = async () => {
  try {
    const fetchedUser = await fetchUser(userPk.value);
    user.value = fetchedUser;
    // console.log(user.value);
    //피드 작성자 pk
    feedWriterPk.value = user.value.userPk
    // console.log('피드 작성자 pk',feedWriterPk.value)
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
}


const fetchPersonalFeedData = async() => {
  try {
    const feeds = await getFeedsByUser(userPk.value);
    for (let elem of feeds) {
      elem.likeCount = await fetchLikeCount(elem.feedId)
      elem.commentCount = await fetchCommentCount(elem.feedId);
    }

    personalFeeds.value = feeds;
    // console.log(personalFeeds.value);
  } catch (error) {
    console.error("Error fetching personal feeds:", error.message);
  }
}

const getFriendCount = async() => {
  try {
    const FriendCounts = await fetchFriendCount(userPk.value, page++, amount);
    FriendCount.value = FriendCounts;
    // console.log(FriendCount.value);
  } catch (error) {
    console.error("Error fetching personal feeds:", error.message);
  }
}

const feedLength = computed(() => personalFeeds.value.length);

const totalLikeCount = computed(() => {
  return personalFeeds.value.reduce((total, feed) => total + feed.likeCount, 0);
});

const totalCommentCount = computed(() => {
  return personalFeeds.value.reduce((total, feed) => total + feed.commentCount, 0);
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



