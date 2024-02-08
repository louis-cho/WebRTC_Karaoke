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
          <q-tab name="menu" icon="menu" @click="toggleDropdown2"/>
        </q-tabs>

        <!-- Dropdown 내용 -->
        <!-- <div v-if="isDropdownOpen1" class="dropdown-content">
          <q-tabs vertical>
            <q-tab name="유저검색" label="유저검색" @click="handleDropdownItemClick('/item1')" />
            <q-tab name="로그아웃" label="로그아웃" @click="handleDropdownItemClick('/item6')" />
          </q-tabs>
        </div> -->

        <q-card  v-if="isDropdownOpen1" class="dropdown-content my-card">
          <q-card-actions vertical>
            <q-btn flat>노래방에 초대되었습니다</q-btn>
            <q-btn flat>친구 요청</q-btn>
          </q-card-actions>
          <q-card-actions class="justify-center">
            <q-btn flat>모두 읽음 처리</q-btn>
            <q-btn flat>닫기</q-btn>
          </q-card-actions>
        </q-card>

        <!-- Dropdown 내용 -->
        <div v-if="isDropdownOpen2" class="dropdown-content">
          <q-tabs vertical>
            <q-tab name="유저검색" label="유저검색" @click="handleDropdownItemClick('/item1')" />
            <q-tab name="친구목록" label="친구목록" @click="handleDropdownItemClick('/item2')" />
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

// const tab = ref('feed')
const router = useRouter()
const isDropdownOpen1 = ref(false)
const isDropdownOpen2 = ref(false)



// 이벤트 핸들러 추가
const goToPage = (path) => {
  router.push(path);
}

const toggleDropdown1 = () => {
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


