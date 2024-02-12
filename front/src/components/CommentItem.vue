<template>
  <div>
    <!-- 댓글을 반복적으로 표시 -->
    <div v-for="elem in comments" :key="elem.commentId" :style="{ marginLeft: elem.comment.level * 20 + 'px' }">
      <div class="display-flex">
        <div class="comment-img-container2">
          <!-- 프로필 이미지 -->
        </div>
        <div class="comments">
          <div><strong>{{ elem.nickname }}</strong></div>
          <div>
            {{ elem.comment.content }}
          </div>
          <p>답글달기</p>
          <div>
            {{ elem.comment.createdAt }}
          </div>
        </div>
      </div>
      <hr>

      <!-- 재귀적으로 CommentItem 컴포넌트 호출하여 자식 댓글 렌더링 -->
      <div class="child-comments">
        <CommentItem v-if="elem.children && elem.children.length > 0" :comments="elem.children" />
      </div>
    </div>
  </div>
</template>

<script setup>
// props로 comments를 받아 사용
const props = defineProps(["comments"]);


</script>

<style scoped>

/* 스타일링 추가 (필요에 따라 수정 가능) */
</style>
