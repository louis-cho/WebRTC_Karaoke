<template>
  <div>
    <q-btn label="유저검색" color="black" @click="icon = true" />

    <q-dialog v-model="icon">
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">유저검색</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input
            rounded
            outlined
            v-model="search"
            label="Search User"
            @change="searchNickname"
          >
            <template v-slot:append>
              <q-icon name="search" />
            </template>
          </q-input>
        </q-card-section>

        <q-scroll-area style="height: 300px; max-width: 300px">
          <div>
            <!-- 유저 목록 뜨게 -->
            <q-list v-if="users && users.length && filteredUsers.length">
              <q-item v-for="user in filteredUsers" :key="user.userPk">
                <q-item-section>
                  <q-img class="img-container" :src="user.profileImgUrl" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{ user.nickname }}</q-item-label>
                  <q-item-label caption>{{ user.introduction }}</q-item-label>
                  <!-- 친구 아니라면 -->
                  <div v-if="ex">
                    <q-btn color="primary" label="친구요청"></q-btn>
                  </div>
                  <div v-else>
                    <q-btn color="red" label="친구삭제"></q-btn>
                  </div>
                </q-item-section>
              </q-item>
            </q-list>
            <!-- Display a message if no users match the search -->
            <q-item v-else>
              <q-item-section>
                <q-item-label align="center"
                  >일치하는 유저가 없습니다</q-item-label
                >
              </q-item-section>
            </q-item>
          </div>
        </q-scroll-area>
      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { searchUser, fetchUser } from "@/js/user/user.js";
const icon = ref(false);
const search = ref("");

//친구인지 아닌지 여부
const ex = ref(false);
// 예시 데이터
const users = ref([
  {
    userPk: 1,
    nickname: "노송",
    profileImgUrl: "프로필이미지1",
    role: 0,
    introduction: "저 노송임ㅇㅇ",
  },
  {
    userPk: 2,
    nickname: "티모시",
    profileImgUrl:
      "https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
    role: 0,
    introduction: "나 웡카",
  },
  {
    userPk: 3,
    nickname: "오타니",
    profileImgUrl: "프로필이미지3",
    role: 0,
    introduction: "인사드립니다ㅏ",
  },
  {
    userPk: 4,
    nickname: "황희찬",
    profileImgUrl: "프로필이미지4",
    role: 0,
    introduction: "하이",
  },
]);


const filteredUsers = computed(() => {
  const query = search.value.toLowerCase();
  return users.value.filter(
    (user) =>
      user.nickname.toLowerCase().includes(query) ||
      user.introduction.toLowerCase().includes(query)
  );
});

const searchNickname = async function () {
  try {
    // 백엔드 서버에서 유저 검색 결과 가져오기
    const response = await searchUser(search.value);
    users.value = response; // 서버 응답에 따라 데이터를 업데이트

    for (let idx in users.value) {
      let userPk = users.value[idx].userPk;
      users.value[idx] = await fetchUser(userPk);
    }
  } catch (error) {
    console.error("Error fetching user data:", error);
  }
};
</script>

<style scoped>
.img-container {
  width: 60px;
  height: 60px;
  /* background-image: url("@/assets/img/capture.png"); */
  border-radius: 25px;
  background-size: cover;
  background-position: center;
  display: flex; /* Flexbox 사용 */
  justify-content: center; /* 수평 정렬을 위한 가로 중앙 정렬 */
  align-items: center; /* 수직 정렬을 위한 세로 중앙 정렬 */
}
</style>
