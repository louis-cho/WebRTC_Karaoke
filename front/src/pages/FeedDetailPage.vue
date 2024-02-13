<template>
  <div>
    <NavBar />
    <!-- <TabItem /> -->
    <!-- <h4>상세 피드 페이지</h4> -->
    <div class="my-feed">
      <!-- 첫번째 div -->
      <div class="header">
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기" />
        </div>
      </div>
      <hr />

      <!-- 두번째 div -->
      <div class="profile">
        <div
          class="profile-img-container"
          :style="{
            backgroundImage: `url(${(
              (feed && feed.user && feed.user.profileImgUrl) ||
              'https://picsum.photos/200'
            ).trim()})`,
          }"
        ></div>

        <div class="width-100">
          <div class="space-between">
            <div>
              <p v-if="feed && feed.user">{{ feed.user.nickname }}</p>
            </div>
            <!-- 게시글 작성자가 로그인되어 있는 사람이라면 -->
            <div v-if="feed && feed.user && feed.user.userUUID === uuid.value" @click="toggleModal">
              <img src="@/assets/icon/setting.png" alt="설정" />
            </div>
          </div>
          <div class="space-start">
            <div v-if="feed && feed.song">
              <span>
                {{ feed.song.title ? feed.song.title + "-" : "Default Title-" }}
              </span>
              <span>
                {{ feed.song.singer ? feed.song.singer : "Default Singer" }}
              </span>
            </div>
            <q-btn
              :color="
                feed && feed.status
                  ? getButtonColor(feed.status)
                  : getButtonColor(null)

              "
              :label="
                feed && feed.status
                  ? getButtonLabel(feed.status)
                  : getButtonLabel(null)
              "
              size="sm"
            />
            <!-- <q-btn
            :color="getButtonColor(feedUpdateStatus)"
            :label="getButtonLabel(feedUpdateStatus)"
            size="sm"
          /> -->

          </div>
        </div>
      </div>

      <!-- <p>{{ feed }}</p>
      -----------------------
      <p>{{feed.user}}</p>
      --------------------------- -->
      <!-- {{ feed.user.userPk }}
      ----------------------------------------
      {{ LoggedUserPK }} -->


      <p v-if="feed">{{ feed.content }}</p>
      <video controls width="100%">
        <source src="your_video_url.mp4" type="video/mp4" />
      </video>
      <div class="flex-row">
        <div class="margin-right-20">
          <img
            class="margin-right-10"
            src="@/assets/icon/chat.png"
            alt="댓글"
          />
          <span v-if="feed">{{ feed.commentCount }}</span>
        </div>
        <div class="margin-right-20" @click="handleLikeClick">
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
      </div>
      <hr />

      <!-- 세번째 div(내 댓글 입력창) -->
      <div class="profile">
        <div class="comment-img-container">
          <!-- <img src="@/assets/img/capture3.png" class="comment-img" alt="내 프로필 이미지"> -->
        </div>
        <div class="comment-input-container">
          <input
            v-model="newComment"
            placeholder="댓글을 입력하세요..."
            class="comment-input"
          />
          <button class="comment-button bg-blue-7" @click="registComment">
            등록
          </button>
        </div>
      </div>
      <hr />

      <!-- 네번째 div(댓글 목록) -->
      <div ref="commentContainer">
        <CommentItem :comments="comments" />
      </div>
    </div>

    <q-dialog v-model="modal" persistent>
      <q-card>
        <q-card-section>
          <q-item>
            <q-item-section>
              <q-item-label header>게시글을 수정하시겠습니까?</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-btn icon="close" flat round @click="toggleModal" />
            </q-item-section>
          </q-item>
          <q-card-actions align="center">
            <q-btn label="게시글 수정" color="primary" @click="openModal"/>
            <q-btn label="게시글 삭제" color="negative" @click="deleteFeed(feedId)" />
          </q-card-actions>
        </q-card-section>
      </q-card>
    </q-dialog>

    <!-- 게시글 수정 모달 -->
    <q-dialog v-model="modalOpen">
      <q-card class="modal-card">
        <q-card-section class="modal-header">
          <div class="user-info">
            <!-- 프로필 이미지 가져오기... -->
            <!-- <q-avatar class="img-container" /> -->
            <q-item-section>
              <!-- 닉네임 가져오기 -->
              <!-- <q-item-label>닉네임</q-item-label> -->
            </q-item-section>
          </div>
          <span><strong>게시물 업로드</strong></span>
          <span class="upload-label" @click="updateFeed(feedId)"><strong>업로드</strong></span>
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
              <textarea class="caption-input" placeholder="문구 입력..." v-model="newContent"></textarea>
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
import { ref, nextTick, onMounted, onBeforeMount, computed } from "vue";
import TabItem from "@/layouts/TabItem.vue";
import NavBar from "@/layouts/NavBar.vue";
import useCookie from "@/js/cookie.js";
import { useRouter, useRoute } from "vue-router";
import {
  fetchComment,
  fetchCommentCount,
  addComment,
} from "@/js/comment/comment.js";
import CommentItem from "@/components/CommentItem.vue";

import { fetchHitCount, createHit } from "@/js/hit/hit.js";
import { fetchLikeCount, createLike, deleteLike } from "@/js/like/like.js";
import { fetchFeedList, fetchFeed, fetchFeedDelete, fetchFeedUpdate} from "@/js/feed/feed.js";
import { fetchSong } from "@/js/song/song.js";
import { fetchUser, getUserPk  } from "@/js/user/user.js";
// import { login } from '@/js/encrypt/authRequest.js';
import { useNotificationStore } from "@/stores/notificationStore.js";

const { setCookie, getCookie, removeCookie } = useCookie();
const feed = ref();
const router = useRouter();
const comments = ref([]);
const newComment = ref("");
const commentContainer = ref(null);
const modal = ref(false);
const isLiked = ref(false);
const feedId = ref();
const newContent = ref();
// const newStatus = ref();
const uuid = ref(getCookie("uuid"));
const notificationStore = useNotificationStore();

const goBack = function () {
  router.go(-1);
};

const handleLikeClick = async () => {
  if (!isLiked.value) {
    await createLike(feedId.value, uuid.value);
    //좋아요알림 발송. 자기자신 제외.
    //if(feed.value.user.userKey != 현재로그인한유저.)
    const body = {
    toUser : feed.value.user.userUUID, //받는사람 userPk.
    info : `${feed.value.feedId}`,
    type : "like",
    status : '0'
    }
    notificationStore.sendNotification(body);
  } else {
    feed.value.likeCount = await deleteLike(feedId.value, uuid.value);
  }

  isLiked.value = !isLiked.value;
};


const getButtonColor = (status) => {
  return status === "0" ? "primary" : status === "1" ? "secondary" : "black";
};
const getButtonLabel = (status) => {
  return status === "0" ? "전체 공개" : status === "1" ? "친구 공개" : "비공개";
};



const toggleModal = () => {
  modal.value = !modal.value;
};

const registComment = () => {
  let comment = {};
  comment.userUUID = getCookie("uuid");
  comment.parentCommentId = -1;
  comment.rootCommentId = -1;
  comment.content = newComment.value;
  comment.feedId = feed.value.feedId;
  comment.isDeleted = false;

  addComment(comment);
  //여기 댓글알림 보내기 자기자신게시글일경우 제외.
  // if(feed.value.user.userKey != 현재로그인정보) 인 경우에만 알림보내기
  const body = {
    toUser : feed.value.user.userUUID, //받는사람 userPk.
    info : `${feed.value.feedId}`,//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "comment", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
  }
  notificationStore.sendNotification(body);
  location.reload();
};

// const scrollToBottom = () => {
//   nextTick(() => {
//     commentContainer.value.scrollTop = commentContainer.value.scrollHeight;
//   });
// }

onBeforeMount(async () => {

  console.log(this);

  uuid.value = getCookie("uuid");
  feedId.value = window.location.href.split("/").pop();

  feedId.value = isNaN(feedId.value) ? 0 : parseInt(feedId.value);

  await increaseHitCount(feedId.value, uuid.value);

  let elem = await fetchFeed(feedId.value);
  elem.song = await fetchSong(elem.songId);
  elem.user = await fetchUser(elem.userUUID);
  elem.commentCount = await fetchCommentCount(elem.feedId);
  elem.viewCount = await fetchHitCount(elem.feedId);
  elem.likeCount = await fetchLikeCount(elem.feedId);

  feed.value = elem;

  await fetchAndRenderComments(feedId.value);
  // await updateFeed(feedId.value)
});

const increaseHitCount = async (feedId) => {
  try {
    await createHit(feedId);
  } catch (error) {
    console.error("조회수 증가 중 오류 발생:", error);
  }
};

// fetchAndRenderComments 함수 내부에 추가
async function fetchAndRenderComments(feedId) {
  try {
    // const feedId = 1; // 실제 feedId로 교체
    const pageNo = 0; // 실제 pageNo로 교체

    // 댓글 가져오기
    const commentTree = await fetchComment(feedId, pageNo);

    // 댓글 ref 업데이트
    comments.value = commentTree;

    // 콘솔에 댓글 출력
    console.log("Comments:", comments.value);
  } catch (error) {
    console.error("댓글 가져오기 및 렌더링 중 오류 발생:", error);
  }
}



const deleteFeed = async(feedId) => {
  try {
    //게시글 삭제
    const feedDelete = await fetchFeedDelete(feedId);
    // console.log('피드 delete',feedDelete);
    modal.value = false;
    router.push({ name: "feed", params: { userUUID: uuid.value }});
  } catch (error) {
    console.error("피드 삭제 에러", error);
  }
}


// 게시글 삭제 함수
// const deleteFeed = (feedId) => {
//   toggleModal();
// };
const privacyStatus = computed(() => {
  return selectedOption.value === '전체공개' ? '0': selectedOption.value === '친구공개' ? '1' : '2';
});

const feedUpdateStatus = ref(null);
const updateFeed = async(feedId) => {
  try {
    //게시글 수정
    const feedUpdate = await fetchFeedUpdate(feedId,newContent.value, privacyStatus.value);
    console.log('피드 update',feedUpdate);
    feedUpdateStatus.value = feedUpdate.status;
    console.log('이거거거',feedUpdateStatus.value)
    modalOpen.value = false;
    modal.value=false;
    // location.reload();
    // await nextTick();
    router.replace({ name: "feed_detail", params: { feedId: feedUpdate.feedId  } });
    location.reload();
  } catch (error) {
    console.error("피드 수정 에러", error);
  }
}
// ---------------------------수정모달---------------------------
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
  modal.value=false;
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


</script>
<style scoped>
.display-flex {
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

/* .profile-img {
  width: 100%;
  height: 100%;
  border-radius: 30%;
  display: block;
  object-fit: contain;
  max-width: 100%;
  max-height: 200px;
} */

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
  width: 100%;
  padding-left: 5%;
}
/* .justify-content-start {
  display: flex;
  justify-content: start;
} */

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
.comment-container {
  margin-top: 20px;
}
.comment-input-container {
  display: flex;
  align-items: center;
}

.comment-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-left: 10px;
  margin-right: 10px;
}

.comment-button {
  padding: 10px 20px;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

/* ------------------------------ */
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
