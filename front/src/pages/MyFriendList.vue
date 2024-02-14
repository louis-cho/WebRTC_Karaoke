<template>
  <div>
    <NavBar/>
    <q-layout>
    <q-page-container>
      <q-page>
        <q-header>
          <q-toolbar>
            <q-toolbar-title>
              {{ pref.app.kor.friends.manage}}
            </q-toolbar-title>
            <q-btn
              @click="icon = true"
              :label="pref.app.kor.friends.search"
              color="secondary"
            />
          </q-toolbar>
        </q-header>
        <!-- 친구요청 -->
        <q-page-container>
          <q-list bordered separator>
            <q-item v-for="friend in friendRequestList" :key="friend.friendId">
              <q-item-section>
                <q-item-label>
                  {{friend.friendName}} 님의 친구 요청
                </q-item-label
                >
              </q-item-section>
              <q-item-section side>
                <q-btn
                  @click="acceptFriend()"
                  :label="pref.app.kor.friends.accept"
                  color="primary"
                />
              </q-item-section>
              <q-item-section side>
                <q-btn
                  @click="rejectFriend()"
                  :label="pref.app.kor.friends.reject"
                  color="primary"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-page-container>
        <!-- 친구목록 -->
        <q-page-container>
          <q-list bordered separator>
            <q-item v-for="friend in friendList" :key="friend.friendId">
              <q-item-section>
                <q-item-label>
                  {{friend.friendName}}
                </q-item-label
                >
              </q-item-section>
              <q-item-section side>
                <q-btn
                  @click="deleteFriend()"
                  :label="pref.app.kor.friends.request"
                  color="primary"
                />
              </q-item-section>
            </q-item>
          </q-list>
        </q-page-container>
      </q-page>
    </q-page-container>
  </q-layout>
  </div>
끝

  <q-dialog v-model="icon">
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">유저 검색</div>
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

</template>

<script setup>
import NavBar from '@/layouts/NavBar.vue';
import SearchUser from '@/components/SearchUser.vue';
import { fetchFriendList, fetchFriendRequest } from "@/js/friends/friends.js";
import { ref, onMounted, computed } from "vue";
import { searchUser, fetchUser } from "@/js/user/user.js";
import pref from "@/js/config/preference.js"
const icon = ref(false);
const search = ref("");
const friendList = ref([
  {
    friendId : 1,
    friendName : "친구이름"
  },
  {
    friendId : 2,
    friendName : "친구이름2"
  },
  {
    friendId : 3,
    friendName : "친구이름3"
  },
])
const friendRequestList = [
  {
    friendId : 3,
    friendName : "요청친구1"
  },
  {
    friendId : 4,
    friendName : "요청친구2"
  },
  {
    friendId : 5,
    friendName : "요청친구3"
  },
]
onMounted(async () => {
  // await fetchAndRenderFriends();
});

async function fetchAndRenderFriends() {
  try {
    const userId = 1; //실제 유저Id로 변경해야함
    const pageNo = 0; //실제 page로 변경해야함
    const sizeNo = 10; //실제 size로 변경해야함
    const friendList = await fetchFriendList(userId, pageNo, sizeNo)

    console.log('친구목록 출력:', friendList);
  } catch (error) {
    console.error('친구목록 가져오기 및 렌더링 중 오류 발생:', error);
  }
}
const joinSession = () =>{
  console.log("join session 누름")
}
// onMounted(async () => {
//   await fetchAndRequestFriends();
// });

// async function fetchAndRequestFriends() {
//   try {
//     const fromUser = 1;
//     const toUser = 2;
//     const friendRequest = await fetchFriendRequest(fromUser, toUser)

//     console.log('친구요청:', friendRequest);
//   } catch (error) {
//     console.error('친구요청 오류 발생:', error);
//   }
// }
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
.my-feed {
  padding-left: 200px;
  padding-right: 200px;
  }
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
