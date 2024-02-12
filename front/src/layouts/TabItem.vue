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
          <q-tab name="person" icon="person" @click="goToPage('/my_profile')"/>
          <q-tab name="notifications" icon="notifications" @click="toggleDropdown1"/>
          {{ notificationStore.bellCount }}
          <q-tab name="menu" icon="menu" @click="toggleDropdown2"/>
        </q-tabs>

        <!-- Dropdown 내용 -->
        <!-- <div v-if="isDropdownOpen1" class="dropdown-content">
          <q-tabs vertical>
            <q-tab name="유저검색" label="유저검색" @click="handleDropdownItemClick('/item1')" />
            <q-tab name="로그아웃" label="로그아웃" @click="handleDropdownItemClick('/item6')" />
          </q-tabs>
        </div> -->

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
            <q-tab name="유저검색" label="유저검색" @click="handleDropdownItemClick('/item1')" />
            <q-tab name="친구목록" label="친구목록" @click="handleDropdownItemClick('/friend_list')" />
            <q-tab name="포인트" label="포인트" @click="handleDropdownItemClick('/item3')" />
            <q-tab name="프로필 수정" label="프로필 수정" @click="handleDropdownItemClick('/info_edit')" />
            <q-tab name="로그아웃" label="로그아웃" @click="handleDropdownItemClick('/item6')" />
          </q-tabs>
        </div>
      </div>

    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {useNotificationStore} from '@/stores/notificationStore.js'
import pref from "@/js/config/preference.js";
import axios from 'axios'
const notificationStore = useNotificationStore();
// const tab = ref('feed')
const router = useRouter()
const isDropdownOpen1 = ref(false)
const isDropdownOpen2 = ref(false)



// 이벤트 핸들러 추가
const goToPage = (path) => {
  router.push(path);
}

const toggleDropdown1 = () => {

  if(isDropdownOpen1.value == false){ //드랍다운이 닫혀있었다면
    axios.get(`${pref.app.api.protocol}${pref.app.api.host}/notifications`)
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

const handleDropdownItemClick = (itemPath) => {
  // Dropdown 내 아이템을 클릭했을 때 수행할 로직 추가
  goToPage(itemPath)
  toggleDropdown2()
}

const handleNotificationClick = (notification) => {
  console.log("notification", notification.notificationId);
  //알림 읽음으로변경 요청
  axios.get(`${pref.app.api.protocol}${pref.app.api.host}/notifications/read/${notification.notificationId}`)
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
  axios.post(`${pref.app.api.protocol}${pref.app.api.host}/notifications/readAll`,notificationStore.notificationList)
  .then((response)=> {
    console.log("모두읽기처리완료");
    notificationStore.notificationList = [];
    notificationStore.bellCount = 0;
  })
  .catch((err)=>{
    console.log("모두읽기중 에러발생.");
  })
}
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
  z-index: 1;
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


