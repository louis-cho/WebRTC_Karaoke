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
            <q-item v-for="friend in friendRequestList" :key="friend.userUUID">
              <q-item-section>
                <q-item-label>
                  {{friend.friendName}} 님의 친구 요청
                </q-item-label
                >
              </q-item-section>
              <q-item-section side>
                <q-btn
                  @click="acceptFriend(friend)"
                  :label="pref.app.kor.friends.accept"
                  color="primary"
                />
              </q-item-section>
              <q-item-section side>
                <q-btn
                  @click="rejectFriend(friend)"
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
            <q-item v-for="friend in friendList" :key="friend.userUUID">
              <q-item-section>
                <q-item-label>
                  {{friend.friendName}}
                </q-item-label
                >
              </q-item-section>
              <q-item-section side>
                <q-btn
                  @click="deleteFriend(friend)"
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

  <q-dialog v-model="icon">
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">새 친구 찾기</div>
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
              <q-item v-for="user in filteredUsers" :key="user.userUUID">
                <q-item-section>
                  <q-img class="img-container" :src="user.profileImgUrl" />
                </q-item-section>
                <q-item-section>
                  <q-item-label>{{ user.nickname }}</q-item-label>
                  <q-item-label caption>{{ user.introduction }}</q-item-label>
                  <!-- 친구 아닌사람만 보여주기-->
                    <q-btn @click="requestFriend()" color="primary" label="친구요청"></q-btn>
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
import { fetchFriendList, fetchFriendRequest, fetchFriendRequestList, fetchFriendCount } from "@/js/friends/friends.js";
import { ref, onMounted, computed } from "vue";
import { searchUser, fetchUser } from "@/js/user/user.js";
import pref from "@/js/config/preference.js"
import useCookie from "@/js/cookie.js";
import axios from "axios";
const { setCookie, getCookie, removeCookie } = useCookie();
const icon = ref(false);
const search = ref("");
const friendList = ref([])
const friendRequestList = ref([])

onMounted(async () => {
  fetchFriendCount()
  //친구 목록 호출
  let tempFriendList = await fetchFriendList(0,100);
  friendList.value = await Promise.all(
    tempFriendList
    .map(async(friend) => {
      const friendId = friend.friendId
      const userUUID = (friend.fromUserKey == getCookie('uuid'))? friend.toUserKey : friend.fromUserKey;
      const friendName = (friend.fromUserKey == getCookie('uuid'))? friend.toUserNickname : friend.fromUserNickname;
      return {friendId, userUUID,  friendName}
    })
  );

  //친구 요청목록
  tempFriendList = await fetchFriendRequestList(0,100);

  friendRequestList.value = await Promise.all(
    tempFriendList
    .filter( (friend) =>{
      return friend.status === "1" && friend.toUserKey === getCookie('uuid');
    })
    .map(async(friend) => {
      const friendId = friend.friendId;
      const userUUID = friend.fromUserkey === getCookie('uuid')? friend.toUserkey : friend.fromUserKey;
      const friendName = friend.fromUserkey === getCookie('uuid')? friend.toUserNickname : friend.fromUserNickname;
      return {friendId, userUUID,  friendName}
    })
  )

  //유저 검색 초기목록
  // users.value =

});

//친구수락
const acceptFriend = async (friend) => {
  await axios.post(pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.accept + `?friendId=${friend.friendId}`,null,{
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",
    },
  }).then((response)=>{
    console.log("수락완료.")
  }).catch((err) => {console.log("error occured")})

  // friendList 변경
  friendList.value.push(friend)
  // friendRequestList 변경
  friendRequestList.value = friendRequestList.value.filter(element => element.friendId !== friend.friendId );
}

//친구거절
const rejectFriend = async (friend) => {
  await axios.post(pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.delete + `?friendId=${friend.friendId}`,null,{
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",
    },
  }).then((response)=>{
    console.log("거절완료.")
  }).catch((err) => {console.log("error occured")})

  // friendRequestList 변경
  friendRequestList.value = friendRequestList.value.filter(element => element.friendId !== friend.friendId );
}

//친구삭제
const deleteFriend = async (friend) =>{
  await axios.post(pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.delete + `?friendId=${friend.friendId}`,null,{
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",
    },
  }).then((response)=>{
    console.log("삭제완료.")
  }).catch((err) => {console.log("error occured")})

  // friendList 변경
  friendList.value = friendList.value.filter(element => element.friendId !== friend.friendId );
}

//친구요청
const requestFriend = () =>{
  console.log("요청 완료")
    //// fetchFriendRequest(getCookie('uuid'),friend.value)
  //친구데이터 생성 요청
}

// 예시 데이터
// const users = ref([]);
// const users = ref([
//   {
//     userPk: 1,
//     nickname: "친구를 검색해보세요",
//     profileImgUrl:"https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
//     role: 0,
//     introduction: "저 노송임ㅇㅇ",
//   },
//   {
//     userPk: 2,
//     nickname: "티모시",
//     profileImgUrl:
//       "https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
//     role: 0,
//     introduction: "나 웡카",
//   },
//   {
//     userPk: 3,
//     nickname: "오타니",
//     profileImgUrl: "https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
//     role: 0,
//     introduction: "인사드립니다ㅏ",
//   },
//   {
//     userPk: 4,
//     nickname: "황희찬",
//     profileImgUrl: "https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg",
//     role: 0,
//     introduction: "하이",
//   },
// ]);


// const filteredUsers = computed(() => {
//   const query = search.value.toLowerCase();
//   return users.value.filter(
//     (user) =>
//       user.nickname.toLowerCase().includes(query) ||
//       user.introduction.toLowerCase().includes(query)
//   );
// });

const users = computed(() => {
  return searchUser(search.value);
})

const filteredUsers = computed(() => {
  const query = search.value.toLowerCase();
  return users.value.filter(
    (user) => !isFriend(user.userUuid)
  );
});

const isFriend = (userUuid)=> {
  for (let i = 0; i < friendList.value.length; i++) {
    if (friendList.value[i].userUUID === userUuid) {
      return true; // 해당 유저는 로그인한사람친구목록에있다.
    }
  }
  return false; // 해당 유저는 친구아니다.
}

// const searchNickname = async function () {
//   console.log("searchNickname 호출")
//   try {
//     // 백엔드 서버에서 유저 검색 결과 가져오기
//     const response = await searchUser(search.value);
//     users.value = response; // 서버 응답에 따라 데이터를 업데이트

//     for (let idx in users.value) {
//       let userPk = users.value[idx].userPk;
//       users.value[idx] = await fetchUser(userPk);
//     }
//   } catch (error) {
//     console.error("Error fetching user data:", error);
//   }
// };
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
