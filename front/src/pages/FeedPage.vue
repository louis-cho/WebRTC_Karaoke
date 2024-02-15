<template>
  <div @scroll="handleScroll">
    <!-- <TabItem/> -->
    <NavBar />
    <!-- <h3>전체 피드 페이지</h3> -->
    <div class="my-feed">
      <!-- 첫번째 div -->
      <!-- 검색창 -->
      <!-- 닉네임/노래제목 검색 가능 -->
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
        <q-btn
          @click="search"
          class="search-button"
          color="primary"
          label="검색"
          style="height: 40px; width: 45px;"
          dense
        />
        <div class="q-pa-md">
          <q-btn-dropdown color="primary" label="피드 정렬">
            <q-list>
              <q-item clickable v-close-popup @click="onItemClick(0)">
                <q-item-section>
                  <q-item-label>최신순</q-item-label>
                </q-item-section>
              </q-item>

              <q-item clickable v-close-popup @click="onItemClick(1)">
                <q-item-section>
                  <q-item-label>오래된순</q-item-label>
                </q-item-section>
              </q-item>

              <q-item clickable v-close-popup @click="onItemClick(2)">
                <q-item-section>
                  <q-item-label>랭킹순</q-item-label>
                </q-item-section>
              </q-item>
            </q-list>
          </q-btn-dropdown>
        </div>
      </div>

      <!-- 두번째 div -->

      <div v-for="feed in filteredFeeds" :key="feed.feedId">
        <div class="profile">
          {{ feed.user.profileImgUrl }}
          <div
            class="profile-img-container"
            :style="{
              backgroundImage: `url(${(
              feed.user.profileImgUrl || 'https://picsum.photos/200'
            ).trim()})`,
            }"
            @click="gotoUserFeed(feed.user.userKey)"
          ></div>

          <div class="width-100" style="margin-left: 20px;">
            <div class="space-between">
              <div>
                <!-- 닉네임 -->
                <p v-if="feed && feed.user">{{ feed.user.nickname }}</p>
              </div>
            </div>
            <div class="space-start">
              <div v-if="feed && feed.song" class="margin-right-20">
                <span>
                  {{
                    feed.song.title ? feed.song.title + "-" : "Default Title-"
                  }}
                </span>
                <span>
                  {{ feed.song.singer ? feed.song.singer : "Default Singer" }}
                </span>
              </div>
              <q-btn
                :color="getButtonColor(feed.status)"
                :label="getButtonLabel(feed.status)"
                size="sm"
              />
            </div>
          </div>
        </div>

        <p v-if="feed">{{ feed.content }}</p>
        <video controls width="100%" ref="videoPlayer">
          <source :src="feed.videoUrl" type="video/mp4" />
        </video>

        <div class="flex-row">
          <div class="margin-right-20" @click="goFeedDetail(feed.feedId)">
            <img
              class="margin-right-10"
              src="@/assets/icon/chat.png"
              alt="댓글"
            />
            <span v-if="feed">{{ feed.commentCount }}</span>
          </div>
          <div class="margin-right-20" @click="toggleLike(feed.feedId)">
            <img
              class="margin-right-10"
              src="@/assets/icon/love.png"
              alt="좋아요"
            />
            <span v-if="feed">{{ feed.likeCount }}</span>
          </div>
          <div class="margin-right-20">
            <img
              class="margin-right-10"
              src="@/assets/icon/show.png"
              alt="조회수"
            />
            <span v-if="feed">{{ feed.viewCount }}</span>
          </div>
          <!-- <div class="margin-right-20">
            <img
              class="margin-right-10"
              src="@/assets/icon/dollar.png"
              alt="후원"
            />
            <span v-if="feed">{{ feed.TOTAL_POINT }}</span>
          </div> -->
          <q-btn @click="goFeedDetail(feed.feedId)">피드 디테일 페이지로</q-btn>
        </div>
        <hr />
      </div>
    </div>

    <div v-if="loading">Loading...</div>
  </div>
</template>

<script setup>
import {
  ref,
  onMounted,
  onUnmounted,
  computed,
  onBeforeMount,
  nextTick,
} from "vue";
import TabItem from "@/layouts/TabItem.vue";
import NavBar from "@/layouts/NavBar.vue";
import { useRouter } from "vue-router";
import app from "@/js/config/preference.js";
import { fetchHitCount } from "@/js/hit/hit.js";
import { fetchLikeCount, createLike, deleteLike } from "@/js/like/like.js";
import { fetchFeedList } from "@/js/feed/feed.js";
import { fetchSong } from "@/js/song/song.js";
import { fetchUser } from "@/js/user/user.js";
import { fetchCommentCount } from "@/js/comment/comment.js";

let pref = app;
const feeds = ref([]);
const router = useRouter();
const select = ref(0);

let page = 0;
const amount = 3;

const onItemClick = (value) => {
  // 클릭된 항목의 값에 따라 다른 동작 수행
  switch (value) {
        case 0:
          select.value = 0;
          page = 0;
          feeds.value = [];
          fetchFeedData(select.value);
          break;
        case 1:
          select.value = 1;
          page = 0;
          feeds.value = [];
          fetchFeedData(select.value);
          break;
        case 2:
          select.value = 2;
          page = 0;
          feeds.value = [];
          fetchFeedData(select.value);
          break;
        default:
          break;
      }
};

const goFeedDetail = (feedId) => {
  router.push({ name: "feed_detail", params: { feedId } });
};

onBeforeMount(async () => {
  await fetchFeedData(select.value);
});
const itemsPerLoad = 10; // 한 번에 로드할 피드 수
const loading = ref(false);

const gotoUserFeed  = (userKey) => {
  router.push({ name: "feed", params: { userUUID:  userKey }});
}

//가상 피드 데이터
const fetchFeedData = async (select) => {
  const newFeeds = await fetchFeedList(page++, amount, select);

  if(newFeeds == null) {
    return;
  }

  console.log(newFeeds)
  for (let elem of newFeeds) {
    elem.song = await fetchSong(elem.songId);
    elem.user = await fetchUser(elem.userUUID);
    elem.commentCount = await fetchCommentCount(elem.feedId);
    elem.viewCount = await fetchHitCount(elem.feedId);
    elem.likeCount = await fetchLikeCount(elem.feedId);
  }

  // feeds.value를 새로운 데이터로 갱신
  feeds.value = feeds.value.concat(newFeeds.slice(0, itemsPerLoad));

  // nextTick을 사용하여 Vue에게 DOM 업데이트를 기다리도록 함
  await nextTick();
  console.log("Feeds:", feeds.value);
};

const getButtonColor = (status) => {
  return status === "0" ? "primary" : status === "1" ? "secondary" : "black";
};
const getButtonLabel = (status) => {
  return status === "0" ? "전체 공개" : status === "1" ? "친구 공개" : "비공개";
};

const getUser = async (userPk) => {
  let user = await fetchUser(userPk);
  return user;
};


// 스크롤 이벤트 핸들러
const handleScroll = async () => {
  const element = document.documentElement;
  const { scrollTop, scrollHeight, clientHeight } = element;

  if (scrollTop + clientHeight >= scrollHeight - 10) {
    loading.value = true;

    try {
      await fetchFeedData(select.value);
    } catch (error) {
      console.error("Error fetching new feeds:", error);
    } finally {
      loading.value = false;
    }
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
const searchQuery = ref("");


const filteredFeeds = computed(() => {
  return feeds.value.filter(feed => {
    const userNameLowerCase = feed.user.nickname.toLowerCase();
    const songTitleLowerCase = feed.song.title.toLowerCase();

    return (
      userNameLowerCase.includes(searchQuery.value.toLowerCase()) ||
      songTitleLowerCase.includes(searchQuery.value.toLowerCase())
    );
  });
});

const search = () => {
  const searchQueryLowerCase = searchQuery.value.toLowerCase();

  feeds.value = feeds.value.filter(feed => {
    const userNameLowerCase = feed.user.nickname.toLowerCase();
    const songTitleLowerCase = feed.song.title.toLowerCase();

    return (
      userNameLowerCase.includes(searchQueryLowerCase) ||
      songTitleLowerCase.includes(searchQueryLowerCase)
    );
  });
};

const toggleLike = async (feedId) => {
  const likeStatus = await createLike
  console.log('라이크 상태',likeStatus)
};
</script>
<style scoped>
.search-container {
  display: flex;
  flex-direction: row;
  align-items: center;
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
.space-start {
  display: flex;
  justify-content: start;
}

.my-feed {
  /* padding: 20px; */
  padding-left: 400px;
  padding-right: 400px;
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

.profile {
  display: flex;
  justify-content: start;
  align-items: center;
  margin: 20px 0;
}

</style>
