<template>
  <div @scroll="handleScroll">
    <!-- <TabItem/> -->
    <NavBar/>
    <!-- <h3>전체 피드 페이지</h3> -->
    <div class="my-feed">
      <!-- 첫번째 div -->
      <!-- 검색창 --> <!-- 닉네임/노래제목 검색 가능 -->
      <div class="search-container">
        <q-input
          v-model="searchQuery"
          outlined
          placeholder="검색어를 입력하세요"
          dense
          class="search-input"
        >
          <template v-slot:before>
            <q-icon name="search" class="search-icon" />
          </template>
        </q-input>
        <q-btn @click="search" class="search-button" color="primary" label="검색" dense />
      </div>


      <!-- 두번째 div -->
      <q-btn @click="goFeedDetail">피드 디테일 페이지로</q-btn>

      <div v-for="feed in filteredFeeds" :key="feed.FEED_ID" >
        <div class="profile">
          <div class="profile-img-container" :style="{ backgroundImage: `url(${getUserProfile(feed.USER_PK)})` }">
            <!-- <img src="@/assets/img/capture.png" alt="프로필 이미지" class="profile-img"> -->
          </div>

          <div class="width-100">
            <div class="space-between" >
              <div>
                <!-- 닉네임 -->
                <p>{{ getUserName(feed.USER_PK) }}</p>
              </div>

            </div>
            <div class="space-start">
              <!-- 노래제목 -->
              <div>{{ getSongTitle(feed.FEED_ID) }}-</div>
              <!-- 가수 -->
              <div>{{ getSongSinger(feed.FEED_ID) }}</div>
              <q-btn :color="feed.STATUS === '0' ? 'primary' : (feed.STATUS === '1' ? 'secondary' : 'black')" :label="feed.STATUS === '0' ? '전체 공개' : (feed.STATUS === '1' ? '친구 공개' : '비공개')" size="sm" />
            </div>
          </div>
        </div>

        <p>{{ feed.CONTENT }}</p>
        <video controls width="100%" ref="videoPlayer">
          <source :src="feed.VIDEO_URL" type="video/mp4">
        </video>

        <div class="flex-row">
        <div class="margin-right-20">
          <img class="margin-right-10" src="@/assets/icon/chat.png" alt="댓글">
          <span>0</span>
        </div>
        <div class="margin-right-20">
          <img class="margin-right-10" src="@/assets/icon/love.png" alt="좋아요">
          <span>0</span>
        </div>
        <div class="margin-right-20">
          <img class="margin-right-10" src="@/assets/icon/show.png" alt="조회수">
          <span>0</span>
        </div>
        <div class="margin-right-20">
          <img class="margin-right-10" src="@/assets/icon/dollar.png" alt="후원">
          <span>{{ feed.TOTAL_POINT }}</span>
        </div>
        </div>
        <hr>

      </div>
    </div>

    <div v-if="loading">Loading...</div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import TabItem from "@/layouts/TabItem.vue"
import NavBar from '@/layouts/NavBar.vue'
import { useRouter } from 'vue-router';

const router = useRouter();

const goFeedDetail = () => {
  router.push('/feed_detail')
}

const itemsPerLoad = 10; // 한 번에 로드할 피드 수
const loading = ref(false)
//가상 피드 데이터
const feeds = ref([
  {
    FEED_ID: 1,
    USER_PK: 1,
    SONG_ID: 3,
    CONTENT: "오랜만에 노래 불러봄",
    THUMBNAIL_URL: "썸네일 주소1",
    VIDEO_URL: "your_video_url.mp4",
    VIDEO_LENGTH: "190",
    STATUS: "2",
    TOTAL_POINT: "20000"
  },
  {
    FEED_ID: 2,
    USER_PK: 4,
    SONG_ID: 7,
    CONTENT: "평가 좀 해주세요",
    THUMBNAIL_URL: "썸네일 주소2",
    VIDEO_URL: "your_video_url2.mp4",
    VIDEO_LENGTH: "170",
    STATUS: "0",
    TOTAL_POINT: "5000"
  }
])
const getUserProfile = (userPK) => {
  // 사용자 프로필 이미지 가져오기 로직..
  return 'https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg';
}

const getUserName = (userPK) => {
  // 닉네임 가져오기 로직...
  return '닉네임1';
}

const getSongId = (feed_id) => {
  // FEED_ID를 사용하여 SONG_ID를 가져오기...
  // 예를 들어 빅뱅 거짓말 SONG_ID 10번이라 할 때
  return 10;
}

const getSongTitle = (feed_id) => {
  // FEED_ID를 사용하여 SONG_ID를 가져오기...
  const song_id = getSongId(feed_id);
  // SONG_ID를 사용하여 TITLE을 가져오기...
  return '거짓말';
}

const getSongSinger = (feed_id) => {
  // FEED_ID를 사용하여 SONG_ID를 가져오기...
  const song_id = getSongId(feed_id);
  // SONG_ID를 사용하여 SINGER를 가져오기...
  return '빅뱅';
}

// 스크롤 이벤트 핸들러
const handleScroll = () => {
  const element = document.documentElement;
  const { scrollTop, scrollHeight, clientHeight } = element;

  // 스크롤이 아래로 내려갔는지 확인
  if (scrollTop + clientHeight >= scrollHeight - 10) {
    // 무한 스크롤 로딩 시작
    loading.value = true;

    // 새로운 피드를 불러오는 로직 (예시로 비동기 setTimeout 사용)
    setTimeout(() => {
      // 새로운 피드 데이터를 여기서 추가
      const newFeeds = [
        {
          FEED_ID: 3,
          USER_PK: 5,
          SONG_ID: 5,
          CONTENT: "새로운 피드 내용!!",
          THUMBNAIL_URL: "새 썸네일 주소",
          VIDEO_URL: "your_video_url.mp4",
          VIDEO_LENGTH: "220",
          STATUS: "1",
          TOTAL_POINT: "0"
        },

      ];

      // 새로운 피드를 기존 피드 배열에 추가
      feeds.value = feeds.value.concat(newFeeds.slice(0, itemsPerLoad));

      // 무한 스크롤 로딩 종료
      loading.value = false;
    }, 1000); // 적절한 비동기 로딩 시간으로 조절
  }
};


// 컴포넌트가 마운트된 후에 스크롤 이벤트를 추가
onMounted(() => {
  window.addEventListener("scroll", handleScroll);
});

// 컴포넌트가 언마운트될 때 스크롤 이벤트 리스너 제거
onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});

// 검색 기능을 위한 변수와 메소드 추가
const searchQuery = ref('');

const filteredFeeds = computed(() => {
  return feeds.value.filter(feed => {
    const userNameLowerCase = getUserName(feed.USER_PK).toLowerCase();
    const songTitleLowerCase = getSongTitle(feed.FEED_ID).toLowerCase();
    return (
      userNameLowerCase.includes(searchQuery.value.toLowerCase()) ||
      songTitleLowerCase.includes(searchQuery.value.toLowerCase())
    )
  })
})

const search = () => {
  const searchQueryLowerCase = searchQuery.value.toLowerCase();

  feeds.value = feeds.value.filter(feed => {
    const userNameLowerCase = getUserName(feed.USER_PK).toLowerCase();
    const songTitleLowerCase = getSongTitle(feed.FEED_ID).toLowerCase();

    // 닉네임이나 노래제목에 검색어가 포함되어 있는지 확인
    return (
      userNameLowerCase.includes(searchQueryLowerCase) ||
      songTitleLowerCase.includes(searchQueryLowerCase)
    );
  });
}



// const playVideo = (videoUrl) => {
//   const videoPlayer = document.getElementById('video-player');
//   videoPlayer.src = videoUrl;
//   videoPlayer.play();
// }
</script>

<style scoped>
.my-feed {
  padding-left: 200px;
  padding-right: 200px;
  }

.display-flex{
  display: flex;
}
.profile-img-container {
  width: 70px;
  height: 70px;
  background-image: url("@/assets/img/capture.png");
  /* object-fit : contain; */
  border-radius: 25px;
  background-size: cover;
  background-position: center;
  display: flex; /* Flexbox 사용 */
  justify-content: center; /* 수평 정렬을 위한 가로 중앙 정렬 */
  align-items: center; /* 수직 정렬을 위한 세로 중앙 정렬 */
}

.comment-img-container {
  width: 70px;
  height: 70px;
  background-image: url("@/assets/img/capture3.png");
  /* object-fit : contain; */
  border-radius: 25px;
  background-size: cover;
  background-position: center;
  display: flex; /* Flexbox 사용 */
  justify-content: center; /* 수평 정렬을 위한 가로 중앙 정렬 */
  align-items: center; /* 수직 정렬을 위한 세로 중앙 정렬 */
}

.comment-img-container2 {
  width: 50px;
  height: 50px;
  background-image: url("@/assets/img/capture3.png");
  /* object-fit : contain; */
  border-radius: 25px;
  background-size: cover;
  background-position: center;
  display: flex; /* Flexbox 사용 */
  justify-content: center; /* 수평 정렬을 위한 가로 중앙 정렬 */
  align-items: center; /* 수직 정렬을 위한 세로 중앙 정렬 */
}


.comment-img {
  width: 100%;
  height: 100%;
  border-radius: 30%;
  display: block; /* 인라인 요소 간격 제거 */
  object-fit: cover;
}

.my-feed {
    /* padding: 20px; */
  padding-left: 200px;
  padding-right: 200px;
  }
.profile {
  display: flex;
  justify-content: start;
  align-items: center;
  margin: 20px 0;

}

.width-100 {
  width:100%;
  padding-left: 5%;
}

.comments {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.space-between {
  display: flex;
  justify-content: space-between;
}

.space-start {
  display: flex;
  justify-content: start;
}

.flex-row {
  display: flex;
}

.margin-right-10 {
  margin-right: 10px;
}

.margin-right-20 {
  margin-right: 20px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.align-between {
  flex-wrap: wrap;
  align-content: space-between;
}

.thumbnail {
  width: 100%;
  height: auto;
  border-radius: 10px;
}

/* .thumbnail-container {
  position: relative;
  cursor: pointer;
} */


.search-container {
  display: flex;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  border-radius: 5px;
}

.search-button {
  margin-left: 10px;
}

.search-icon {
  color: grey;
}
</style>
