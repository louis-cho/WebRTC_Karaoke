<template>
  <!-- <div class="q-pa-md"> -->
    <div class="parent">

      <div @click="goToPage('/')" style="cursor: pointer;">
        <img
          src="@/assets/icon/logo1-removebg-preview.png"
          alt="Logo"
          width="150"
          class="d-inline-block align-text-top"
        />
      </div>

      <div>
        <q-tabs>
          <q-tab name="feed" icon="feed" label="피드" @click="goToPage('/feed')"/>
          <q-tab name="karaoke" icon="library_music" label="노래방" @click="goToPage('/karaoke')" />
          <q-tab name="message" icon="send" label="메시지" @click="goToPage('/message')"/>
        </q-tabs>
      </div>

      <div class="container">
        <q-tabs>
          <q-tab name="person" icon="person" @click="goToFeedPage(LoggedUserPK)"/>
          <q-tab name="notifications" icon="notifications" @click="toggleDropdown1"/>
          {{ notificationStore.bellCount }}
          <q-tab name="menu" icon="menu" @click="toggleDropdown2"/>
        </q-tabs>

        <!-- <q-card  v-if="isDropdownOpen1" class="dropdown-content my-card">
          <q-card-actions vertical>
            <q-btn flat>노래방에 초대되었습니다</q-btn>
            <q-btn flat>친구 요청</q-btn>
          </q-card-actions>
          <q-card-actions class="justify-center">
            <q-btn flat>모두 읽음 처리</q-btn>
            <q-btn flat>닫기</q-btn>
          </q-card-actions>
        </q-card> -->
        <q-card v-if="isDropdownOpen1" class="dropdown-content my-card" bordered separator>
          <q-card-actions vertical>
          <q-btn flat
          v-for="notification in notificationStore.notificationList"
          :key="notification.notificationId"
          clickable
          @click="handleNotificationClick(notification)"
          >
            <q-item-section>
              <q-item-label >
                {{ notification.message }}
              </q-item-label>
            </q-item-section>
          </q-btn>
        </q-card-actions>
        <q-card-actions class="justify-center">
            <q-btn flat @click= readAllNotification>모두 읽음 처리</q-btn>
            <q-btn flat @click="isDropdownOpen1 = false" >닫기</q-btn>
          </q-card-actions>
        </q-card>


        <!-- Dropdown 내용 -->
        <div v-if="isDropdownOpen2" class="dropdown-content">
          <q-tabs vertical>
            <q-tab name="유저검색" label="유저검색" @click="openUserSearchModal" />
            <q-tab name="친구목록" label="친구목록" @click="goToPage('/friend_list')" />
            <!-- <q-tab name="포인트" label="포인트" @click="handleDropdownItemClick('/item3')" /> -->
            <q-tab name="프로필 수정" label="프로필 수정" @click="goToPage('/info_edit')" />
            <q-tab name="로그아웃" label="로그아웃" @click="logout()" />
          </q-tabs>
        </div>

      <!-- 유저검색 모달 -->
      <q-dialog v-model="userSearchModal">
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
            <!-- 친구들 목록 뜨게 -->
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
      <!-- 유저검색 모달---- -->

      </div>
    </div>
</template>

<script setup>
import { ref, computed, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';
import {useNotificationStore} from '@/stores/notificationStore.js';
import pref from "@/js/config/preference.js";
import axios from 'axios';
import { searchUser, fetchUser } from "@/js/user/user.js";
import useCookie from "@/js/cookie.js";
import { getUserPk  } from "@/js/user/user.js";

const { setCookie, getCookie, removeCookie } = useCookie();
const notificationStore = useNotificationStore();
const router = useRouter()
const isDropdownOpen1 = ref(false)
const isDropdownOpen2 = ref(false)
const LoggedUserPK = ref();
const uuid = ref(null);

const userSearchModal = ref(false);
const openUserSearchModal = () => {
  userSearchModal.value = true;
  isDropdownOpen2.value = false;
};

const search = ref('');
//친구인지 아닌지 판별 변수
const ex = ref(false);

onBeforeMount(async () => {
await getLoggedUserPk();
});

// 이벤트 핸들러 추가
const goToPage = (path) => {
  router.push(path);
}


const getLoggedUserPk = async () => {
  try {
    uuid.value = getCookie("uuid")
    console.log("UUID:", uuid.value);
    const getCurrentUserPk = await getUserPk(uuid.value);
    console.log(getCurrentUserPk);
    LoggedUserPK.value = getCurrentUserPk
  } catch (error) {
    console.error("오류 발생!!!:", error);
  }
};

const goToFeedPage = (param) => {
  router.push({ name: "feed", params: { userPk: param } });
}

const toggleDropdown1 = () => {

  if(isDropdownOpen1.value == false){ //드랍다운이 닫혀있었다면
    axios.get(`${pref.app.api.protocol}${pref.app.api.host}/notifications`,{
      headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
    })
    .then((response) =>{
      notificationStore.notificationList = response.data;
      console.log("알림관련 데이터잘받아와쓰요.");
    })
    .catch((err)=>{
      console.log(err.message);
    })
  }
  isDropdownOpen1.value = !isDropdownOpen1.value
}

const toggleDropdown2 = () => {
  isDropdownOpen2.value = !isDropdownOpen2.value
}

// 로그아웃
const logout = () => {
  if(getCookie("Authorization") != null && getCookie("refreshToken") != null && getCookie("uuid")) {
    removeCookie("Authorization");
    removeCookie("refreshToken");
    removeCookie("uuid");
    alert("로그아웃되었습니다.")
    window.location.replace("/")
  } else {
    alert("로그인도 안했는데 로그아웃을?")

  }
}



const handleNotificationClick = (notification) => {
  console.log("notification", notification.notificationId);
  //알림 읽음으로변경 요청
  axios.get(`${pref.app.api.protocol}${pref.app.api.host}/notifications/read/${notification.notificationId}`,{
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
  })
    .then((response) =>{
      console.log("알림상태 잘 수정됐음.");
      //읽음으로 변경됐으면 알림삭제
      notificationStore.notificationList = notificationStore.notificationList.filter(item => item != notification);
      console.log("알림잘삭제됨!  리스트 :", notificationStore.notificationList);
      //종소리 카운트 줄이기
      notificationStore.bellCount--;

      //링크로보내버리기.
      if(notification.type == 'karaoke'){
        router.push(`/karaoke/${notification.info}`);
      }
      else if(notification.type == 'comment'){
        router.push(`/feed_detail/${notification.info}`);
      }
      else if(notification.type == 'like'){
        router.push(`/feed_detail/${notification.info}`);
      }
      else if(notification.type == 'friend'){
        router.push("/friend_list");
      }

    })
    .catch((err)=>{
      console.log(err.message);
    })

}

const readAllNotification = () => {
  console.log("모두읽기가 눌렸어잉");
  axios.post(`${pref.app.api.protocol}${pref.app.api.host}/notifications/readAll`,notificationStore.notificationList,{
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
  })
  .then((response)=> {
    console.log("모두읽기처리완료");
    notificationStore.notificationList = [];
    notificationStore.bellCount = 0;
  })
  .catch((err)=>{
    console.log("모두읽기중 에러발생.");
  })
}



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
.parent {
  /* width : 300px; */
  /* height : 100px; */
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  /* border : 3px solid red; */
}
.container {
  position: relative;
}

.dropdown-content {
  display: flex;
  flex-direction: column;
  position: absolute;
  /* top: 100%;  */
  /* Dropdown이 아이콘 아래에 위치하도록 설정 */
  right: 0;
  background-color: #f9f9f9;
  min-width: 160px;
  box-shadow: 0 8px 16px rgba(0,0,0,0.2);
  z-index: 9999;
}

.dropdown-content q-tab {
  padding: 12px 16px;
  text-decoration: none;
  display: block;
  cursor: pointer;
}

.justify-center {
  display: flex;
  flex-direction: row;
  justify-content: center;
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



<!--
/* .full-width {
  width: 100%;
  flex-grow: 1;
} */

/* .container {
  display: flex;
  justify-content: center;
  gap: 150x;

}
.dropdown-content {
  display: flex;
  flex-direction: column;
  position: absolute;
  top: 100%;
  right: 0;
  background-color: #f9f9f9;
  min-width: 160px;
  box-shadow: 0 8px 16px rgba(0,0,0,0.2);
  z-index: 1;
} -->


