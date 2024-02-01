<template>
  <div>
    <TabItem />
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
        <div class="profile-img-container">
          <img src="@/assets/img/capture.png" alt="프로필 이미지" class="profile-img">
        </div>

        <div class="width-100">
          <div class="space-between" >
            <div>
              <p>{{ 닉네임 }}JennierubyJane</p>
            </div>
            <div @click="toggleModal">
              <img src="@/assets/icon/setting.png" alt="설정">
            </div>
          </div>
          <div class="space-start">
            <div>{{ 노래제목 }}거짓말/</div>
            <div>{{ 가수 }}빅뱅/</div>
            <q-btn color="secondary" label=" 공개 " size="sm" />
          </div>
        </div>
      </div>

      <p>{{ 게시글제목 }} 오랜만에 빅뱅 노래</p>
      <video controls width="100%">
        <source src="your_video_url.mp4" type="video/mp4">
        Your browser does not support the video tag.
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
        <div class="profile-img-container">
          <img src="@/assets/img/capture3.png" class="profile-img2" alt="내 프로필 이미지">
        </div>
        <div style="height: 100px; width: 100%; box-sizing: border-box;">
          <input v-model="newComment"  @keydown.enter.prevent="addComment" placeholder="댓글을 입력하세요...">
        </div>
      </div>
      <hr>

      <!-- 네번째 div(댓글 목록) -->
      <div ref="commentContainer">
        <div v-for="comment in comments" :key="comment.id">
          <div class="row-container">
            <div>
              <img :src="comment.profileImage" class="profile-image" alt="댓글 작성자 프로필 이미지">
            </div>
            <div>
              <div>{{ comment.username }}</div>
              <div>{{ comment.text }}</div>
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
          <q-card-actions align="right">
            <q-btn label="게시글 수정" color="primary"/>
            <q-btn label="게시글 삭제" color="negative" @click="deletePost" />
          </q-card-actions>
        </q-card-section>
      </q-card>
    </q-dialog>

  </div>
</template>

<script setup>
import { ref, nextTick } from "vue";
import TabItem from "@/layouts/TabItem.vue";

const newComment = ref("");
const commentContainer = ref(null);
const modal = ref(false);



const addComment = function() {
  // 댓글 추가 로직
  comments.value.push({
    id: comments.value.length + 1,
    username: '새로운 유저',
    profileImage: 'path/to/new/user/image.jpg',
    text: newComment.value,
  });

  // 댓글 입력 창 초기화
  newComment.value = ""

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
const comments = ref([
  { id: 1, username: '1번유저', profileImage: 'path/to/user1/image.jpg', text: '노래 잘 들었슴다' },
  { id: 2, username: '2번유저', profileImage: 'path/to/user1/image.jpg', text: '음정이 조큼 아쉽네여' },
]);


</script>
<style scoped>

/* .profile-img-container {
    width: 20%;
    height: auto;
    display: block;
  } */

.profile-img-container {
  width: 30px;
  height: 30px;
  background-image: url("@/assets/img/capture.png");
  object-fit: cover;
  border-radius: 25px;
  /* display: block; */
}


.profile-img {
  width: 100%;
  height: 100%;
  border-radius: 30%;
  display: block; /* 인라인 요소 간격 제거 */
  object-fit: cover;
  /* max-width: 100%;
  max-height: 200px; */
}

.profile-img2 {
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
