<template>
  <div>
    <div v-for="comment in comments" :key="comment.commentId">
      <div class="display-flex">
        <div class="comment-img-container2">
          <!-- 프로필 이미지 -->
        </div>
        <div class="comments">
          <div><strong>{{ comment.userPk }} 닉네임</strong></div>
          <div>
            {{ comment.content }}
          </div>
        </div>
      </div>
      <hr>
      <!-- 재귀적으로 CommentItem 컴포넌트 호출하여 자식 댓글 렌더링 -->
      <div class="child-comments">
        <CommentItem v-if="comment.children && comment.children.length > 0" :comments="comment.children" />
      </div>
    </div>
  </div>
</template>

<script setup>
// props로 comments를 받아 사용
const props = defineProps(["comments"]);
</script>

<style scoped>
.child-comments {
  margin-left: 20px; /* 들여쓰기 설정 (필요에 따라 조절 가능) */
}
/* 스타일링 추가 (필요에 따라 수정 가능) */
</style>
