<template>
  <!-- <div class="q-pa-md"> -->
  <div class="parent">
    <div @click="goToPage('/')" style="cursor: pointer">
      <img
        src="@/assets/icon/logo1-removebg-preview.png"
        alt="Logo"
        width="150"
        class="d-inline-block align-text-top"
      />
    </div>

    <div>
      <q-tabs>
        <q-tab
          name="feed"
          icon="feed"
          label="피드"
          @click="goToPage('/feed')"
        />
        <q-tab
          name="karaoke"
          icon="library_music"
          label="노래방"
          @click="goToPage('/karaoke')"
        />
        <q-tab
          name="message"
          icon="send"
          label="메시지"
          @click="goToPage('/message')"
        />
      </q-tabs>
    </div>

    <div class="container">
      <q-tabs>
        <q-tab name="person" icon="person" @click="goToFeedPage(uuid)" />
        <q-tab
          name="notifications"
          icon="notifications"
          @click="toggleDropdown1"
        />
        {{ notificationStore.bellCount }}
        <q-tab name="menu" icon="menu" @click="toggleDropdown2" />
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
      <q-card
        v-if="isDropdownOpen1"
        class="dropdown-content my-card"
        bordered
        separator
      >
        <q-card-actions vertical>
          <q-btn
            flat
            v-for="notification in notificationStore.notificationList"
            :key="notification.notificationId"
            clickable
            @click="handleNotificationClick(notification)"
          >
            <q-item-section>
              <q-item-label>
                {{ notification.message }}
              </q-item-label>
            </q-item-section>
          </q-btn>
        </q-card-actions>
        <q-card-actions class="justify-center">
          <q-btn flat @click="readAllNotification">모두 읽음 처리</q-btn>
          <q-btn flat @click="isDropdownOpen1 = false">닫기</q-btn>
        </q-card-actions>
      </q-card>

      <!-- Dropdown 내용 -->
      <div v-if="isDropdownOpen2" class="dropdown-content">
        <q-tabs vertical>
          <q-tab
            name="유저검색"
            label="유저검색"
            @click="openUserSearchModal"
          />
          <q-tab
            name="친구목록"
            label="친구목록"
            @click="goToPage('/friend_list')"
          />
          <!-- <q-tab name="포인트" label="포인트" @click="handleDropdownItemClick('/item3')" /> -->
          <!-- <q-tab
            name="프로필 수정"
            label="프로필 수정"
            @click="goToPage('/info_edit')"
          /> -->
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
              @keydown.enter.stop
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
              <q-list
                v-if="searchUsers && searchUsers.length && filteredUsers.length"
              >
                <q-item v-for="user in filteredUsers" :key="user.userKey">
                  <q-item-section @click="gotoUserFeed(user.userKey)">
                    <q-img class="img-container" :src="user.profileImgUrl" />
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ user.nickname }}</q-item-label>
                    <q-item-label caption>{{ user.introduction }}</q-item-label>
                  </q-item-section>
                </q-item>
              </q-list>

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
import { ref, computed, onBeforeMount, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useNotificationStore } from "@/stores/notificationStore.js";
import pref from "@/js/config/preference.js";
import axios from "axios";
import { searchUser, fetchUser } from "@/js/user/user.js";
import useCookie from "@/js/cookie.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import { fetchFriendList, fetchFriendRequest, fetchFriendRequestList, fetchFriendCount } from "@/js/friends/friends.js";
const store = useKaraokeStore();

const { setCookie, getCookie, removeCookie } = useCookie();
// const userUUID = getCookie("uuid");
const uuid = ref("");
const notificationStore = useNotificationStore();
const router = useRouter();
const isDropdownOpen1 = ref(false);
const isDropdownOpen2 = ref(false);

const userSearchModal = ref(false);
const openUserSearchModal = () => {
  userSearchModal.value = true;
  isDropdownOpen2.value = false;
};

const gotoUserFeed = (param) => {
  router.push({ name: "feed", params: { userUUID:  param }});
}

onBeforeMount(async () => {
  uuid.value = getCookie("uuid");
});

// 이벤트 핸들러 추가
const goToPage = (path) => {
  router.push(path);
};

const goToFeedPage = (param) => {
  router.push({ name: "feed", params: { userUUID: param } });
};

const toggleDropdown1 = () => {
  if (isDropdownOpen1.value == false) {
    //드랍다운이 닫혀있었다면
    axios
      .get(`${pref.app.api.protocol}${pref.app.api.host}/notifications`, {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        notificationStore.notificationList = response.data;
      })
      .catch((err) => {});
  }
  isDropdownOpen1.value = !isDropdownOpen1.value;
};

const toggleDropdown2 = () => {
  isDropdownOpen2.value = !isDropdownOpen2.value;
};

// 로그아웃
const logout = () => {
  if (
    getCookie("Authorization") != null &&
    getCookie("refreshToken") != null &&
    getCookie("uuid")
  ) {
    removeCookie("Authorization");
    removeCookie("refreshToken");
    removeCookie("uuid");
    localStorage.clear();
    alert("로그아웃되었습니다.");
    window.location.replace("/");
  } else {
    alert("로그인도 안했는데 로그아웃을?");
  }
};

const handleNotificationClick = (notification) => {
  //알림 읽음으로변경 요청
  axios
    .get(
      `${pref.app.api.protocol}${pref.app.api.host}/notifications/read/${notification.notificationId}`,
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    )
    .then((response) => {
      //읽음으로 변경됐으면 알림삭제
      notificationStore.notificationList =
        notificationStore.notificationList.filter(
          (item) => item != notification
        );

      //종소리 카운트 줄이기
      notificationStore.bellCount--;

      //링크로보내버리기.
      if (notification.type == "karaoke") {
        router.push(`/karaoke/${notification.info}`);
      } else if (notification.type == "comment") {
        router.push(`/feed_detail/${notification.info}`);
      } else if (notification.type == "like") {
        router.push(`/feed_detail/${notification.info}`);
      } else if (notification.type == "friend") {
        router.push("/friend_list");
      }
    })
    .catch((err) => {});
};

const readAllNotification = () => {
  axios
    .post(
      `${pref.app.api.protocol}${pref.app.api.host}/notifications/readAll`,
      notificationStore.notificationList,
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    )
    .then((response) => {
      notificationStore.notificationList = [];
      notificationStore.bellCount = 0;
    })
    .catch((err) => {});
};

const search = ref("");
const searchUsers = ref([]);

const filteredUsers = computed(() => {
  const query = search.value ? search.value.toLowerCase() : "";
  if (!query) return [];

  return searchUsers.value.filter((user) => {
    const nickname = user.nickname ? user.nickname.toLowerCase() : "";
    const introduction = user.introduction
      ? user.introduction.toLowerCase()
      : "";
    return nickname.includes(query) || introduction.includes(query);
  });
});


const searchNickname = async function () {
  try {
    const response = await searchUser(search.value);
    searchUsers.value = response;

    for (let idx in searchUsers.value) {
      let userUuid = searchUsers.value[idx].userUuid;
      searchUsers.value[idx] = await fetchUser(userUuid);
    }
  } catch (error) {}
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
  /* display: flex;
  flex-direction: column;
  justify-content: center; */
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
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
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
