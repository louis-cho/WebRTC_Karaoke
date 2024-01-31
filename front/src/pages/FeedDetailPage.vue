<template>
  <div>
    <TabItem/>
    <!-- <h4>상세 피드 페이지</h4> -->
    <div>
      <!-- 첫번째 div -->
      <div>
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기">
        </div>
      </div>
      <!-- 두번째 div -->
      <div class="row-container">
        <div>
          <!-- 수정 필요 -->
          <img src="@/assets/img/capture.png" alt="게시글 작성자 프로필 이미지" class="profile-image">
        </div>
        <div>
          <div class="row-container">
            <!-- 수정 필요 -->
            <div>
              <p>{{ 닉네임 }}JennierubyJane</p>
            </div>
            <div @click="toggleModal">
              <img src="@/assets/icon/setting.png" alt="설정">
            </div>
          </div>
          <div class="row-container">
            <!-- 수정 필요 -->
            <div>{{ 노래제목 }}거짓말/</div>
            <div>{{ 가수 }}빅뱅/</div>
            <div>{{ 공개여부 }}공개</div>
          </div>
        </div>
      </div>

      <!-- 세번째 div -->
      <div>
        <!-- 수정 필요 -->
        <h6>오랜만에 빅뱅 노래 :)</h6>
      </div>
      <!-- 네번째 div -->
      <div>
        <!-- 수정 필요 -->
        <video controls width="100%">
          <source src="your_video_url.mp4" type="video/mp4">
          Your browser does not support the video tag.
        </video>

      </div>
      <!-- 다섯번째 div -->
      <div class="row-container">
        <div>
          <img src="@/assets/icon/chat.png" alt="댓글">
        </div>
        <div>
          <img src="@/assets/icon/love.png" alt="좋아요">
        </div>
        <div>
          <img src="@/assets/icon/show.png" alt="조회수">
        </div>
      </div>
      <!-- 여섯번째 div -->
      <div class="row-container">
        <div>
          <!-- 수정 필요 -->
          <img src="src/assets/img/capture3.png" class="profile-image" alt="댓글 작성자 프로필 이미지">
        </div>
        <div>
          <input v-model="newComment" style="height: 100px; width: 100%; box-sizing: border-box;" @keydown.enter.prevent="addComment" placeholder="댓글을 입력하세요...">
        </div>
      </div>
      <hr>

      <!-- 일곱번째 div -->
      <div ref="commentContainer">
        <!-- 댓글 목록 -->
        <div v-for="comment in comments" :key="comment.id">
          <div class="row-container">
            <div>
              <!-- 수정 필요 -->
              <!-- <img :src="comment.profileImage" alt=""> -->
              <img src="@/assets/img/capture4.png" class="profile-image" alt="">
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

    <!-- 수정 및 삭제 모달 -->
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

          <!-- 수정 및 삭제 버튼 -->
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

.comment-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.profile-image {
  border-radius: 20%;
  object-fit: cover;
  width: 100px;
  height: 100px;
}
</style>



