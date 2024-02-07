<template>
  <div>
    <NavBar/>
    <!-- <TabItem /> -->
    <!-- <h4>상세 피드 페이지</h4> -->
    <div class="my-feed">

      <!-- 첫번째 div -->
      <div class="header ">
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기">
        </div>
      </div>
      <hr>

      <!-- 두번째 div -->
      <div class="profile">
        <!-- <div class="profile-img-container" :style="{ backgroundImage: `url(${getUserProfile(feed.USER_PK)})` }"> -->
          <img src="@/assets/img/capture.png" alt="프로필 이미지" class="profile-img">
        <!-- </div> -->

        <div class="width-100">
          <div class="space-between" >
            <div>
              <!-- <p>{{ getNickName(feed.USER_PK) }}</p> -->
              <p>JennierubyJane</p>
            </div>
            <div @click="toggleModal">
              <img src="@/assets/icon/setting.png" alt="설정">
            </div>
          </div>
          <div class="space-start">
            <!-- <div>{{ getSongTitle(feed.FEED_ID) }} </div> -->
            <p>거짓말-</p>
            <!-- <div>{{ getSongSinger(feed.FEED_ID) }}</div> -->
            <p>빅뱅</p>
            <!-- <q-btn :color="feed.STATUS === '0' ? 'primary' : (feed.STATUS === '1' ? 'secondary' : 'black')" :label="feed.STATUS === '0' ? '전체 공개' : (feed.STATUS === '1' ? '친구 공개' : '비공개')" size="sm" /> -->
          </div>
        </div>
      </div>

      <p>{{ 게시글내용 }} 오랜만에 빅뱅 노래</p>
      <video controls width="100%">
        <source src="your_video_url.mp4" type="video/mp4">
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
      </div>
      <hr>

      <!-- 세번째 div(내 댓글 입력창) -->
      <div class="profile">
        <div class="comment-img-container">
          <!-- <img src="@/assets/img/capture3.png" class="comment-img" alt="내 프로필 이미지"> -->
        </div>
        <div class="comment-input-container">
          <textarea v-model="newComment"  @keydown.enter.prevent="addComment" placeholder="댓글을 입력하세요..."></textarea>
        </div>
      </div>
      <hr>

      <!-- 네번째 div(댓글 목록) -->
      <div ref="commentContainer">
        <div v-for="comment in comments" :key="comment.COMMENT_ID">
          <div class="display-flex">
            <div class="comment-img-container2">
              <!-- <img :src="comment.profileImage" class="profile-image" alt="댓글 작성자 프로필 이미지"> -->
            </div>
            <div class="comments">
              <div><strong>{{ comment.nickname }} 닉네임</strong></div>
              <div>{{ comment.CONTENT }}</div>
            </div>
          </div>
          <hr>
        </div>
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
            <q-btn label="게시글 수정" color="primary"/>
            <q-btn label="게시글 삭제" color="negative" @click="deletePost" />
          </q-card-actions>
        </q-card-section>
      </q-card>
    </q-dialog>

  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from "vue";
import TabItem from "@/layouts/TabItem.vue";
import NavBar from "@/layouts/NavBar.vue";
import { useRouter, useRoute } from "vue-router";
import { fetchComment, renderComments } from "@/js/comment/comment.js";

const router = useRouter();

const newComment = ref("");
const commentContainer = ref(null);
const modal = ref(false);

const goBack = function () {
  router.go(-1);
};


const getUserProfile = (user_pk) => {
  // 사용자 프로필 이미지 가져오기 로직..
  return '@/assets/img/capture3.png';
}

const getNickName = (user_pk) => {
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


const addComment = function() {
  // 댓글 추가
  const newCommentData = {
    COMMENT_ID: comments.value.length + 1,
    USER_PK: 15, //임의로..수정 필요
    FEED_ID: 8, // 임의로..수정 필요
    CONTENT: newComment.value,
    CREATED_AT: new Date().toLocaleString(),
    MODIFIED_AT: new Date().toLocaleString(),
    // userProfile,
    // username,
  };

  comments.value.push(newCommentData);
  // 댓글 입력 창 초기화
  newComment.value = '';
  // 스크롤을 항상 아래로 내림
  scrollToBottom();
}



const toggleModal = () => {
  modal.value = !modal.value;
}


// 게시글 삭제 함수
const deletePost = () => {
  // 여기에 게시글 삭제 로직 추가
  toggleModal();
}

const scrollToBottom = () => {
  nextTick(() => {
    commentContainer.value.scrollTop = commentContainer.value.scrollHeight;
  });
}


// 가상의 댓글 예시
// const comments = ref([
//   { COMMENT_ID: 1, USER_PK:2, FEED_ID: 10,
//     CONTENT: '노래 잘 들었슴다',
//     // ROOT_COMMENT_ID : 3,
//     // PARENT_COMMENT_ID : 4,
//     CREATED_AT: "2021-10-08-10:27",
//     MODIFIED_AT: "2021-10-08-11:20"
//   },
//   { COMMENT_ID: 1, USER_PK:2, FEED_ID: 10,
//     CONTENT: '음정이 조큼 아쉽네여',
//     // ROOT_COMMENT_ID : 3,
//     // PARENT_COMMENT_ID : 4,
//     CREATED_AT: "2023-03-08-10:27",
//     MODIFIED_AT: "2023-03-11-11:20" },
// ]);


const comments = ref([])

onMounted(async () => {
  // 페이지 로드 시 댓글을 가져오도록 설정하거나, 필요한 이벤트에 맞게 호출하세요.
  await fetchAndRenderComments();
});

async function fetchAndRenderComments() {
  try {
    // 피드 아이디와 페이지 번호를 지정하여 댓글을 가져옴
    const feedId = 123; // 피드 아이디를 실제 값으로 변경
    const pageNo = 0; // 페이지 번호를 필요에 맞게 변경

    const commentTree = await fetchComment(feedId, pageNo);

    // 댓글 렌더링
    renderComments(commentTree);

    // 댓글 상태 업데이트
    comments.value = commentTree;
  } catch (error) {
    console.error('Error fetching and rendering comments:', error);
  }
}


</script>
<style scoped>
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
  display: flex;
  justify-content: center;
  align-items: center;
}

.comment-img-container {
  width: 70px;
  height: 70px;
  background-image: url("@/assets/img/capture3.png");
  /* object-fit : contain; */
  border-radius: 25px;
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
}

.comment-img-container2 {
  width: 50px;
  height: 50px;
  background-image: url("@/assets/img/capture3.png");
  /* object-fit : contain; */
  border-radius: 25px;
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
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
  width:100%;
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

/* .just-row-container {
  display: flex;
  justify-content: row;
}

.row-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.column-container {
  display: flex;
  flex-direction: column;
} */

/* .profile-img {
  border-radius: 30%;
  object-fit: cover;
  width: 50%;
  height: 50%;
} */
</style>
