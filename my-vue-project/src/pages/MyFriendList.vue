<template>
  <div>
    <NavBar/>
    <!-- <h3>나의 친구 목록 페이지</h3> -->
    <div class="my-feed">
      <!-- tab 사용해서 왼쪽탭은 친구 요청 들어온 목록, 오른쪽 탭 클릭 시 친구 목록 나오게  -->
    </div>
  </div>
</template>

<script setup>
import NavBar from '@/layouts/NavBar.vue';
import { fetchFriendList } from "@/js/friends/friends.js";
import { ref, onMounted } from "vue";


onMounted(async () => {
  await fetchAndRenderFriends();
});

async function fetchAndRenderFriends() {
  try {
    const userId = 1; //실제 유저Id로 변경해야함

    const friendList = await fetchFriendList(userId)

    console.log('친구목록 출력:', friendList);
  } catch (error) {
    console.error('친구목록 가져오기 및 렌더링 중 오류 발생:', error);
  }
}
</script>

<style scoped>
.my-feed {
  padding-left: 200px;
  padding-right: 200px;
  }

</style>
