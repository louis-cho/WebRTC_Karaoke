<template>
  <div>
    <nav-bar />
    <div class="my-feed">
      <!-- <p>개인정보수정페이지</p> -->
      <div class="flex-container">
        <div @click="goBack">
          <img src="@/assets/icon/back.png" alt="뒤로가기" />
        </div>
        <span class="text-center"><strong>프로필 편집</strong></span>
        <div></div>
      </div>
      <hr />

      <div>
        <div class="center2">
          <div for="profileImage" class="profile-img-container">
            <!-- :style="profileImageStyle" -->
            <input
              type="file"
              id="profileImage"
              style="display: none"
              @change="handleProfileImageChange"
            />
          </div>
          <!-- <div class="profile-img-container" :style="{ backgroundImage: `url(${getUserProfile()})` }" > -->
          <!-- <img src="@/assets/img/capture.png" alt="프로필 이미지" class="profile-img"> -->
          <!-- </div> -->
          <p class="hover-black" @click="triggerProfileImageInput">
            프로필 이미지 변경하기
          </p>
        </div>
        <div class="flex-row center input-group">
          <label class="input-group-label" for="nickname">닉네임</label>
          <input
            v-model="newNickname"
            class="input-group-input margin-right-10"
            type="text"
          />
          <!-- <span>변경</span> -->
        </div>
        <div class="flex-row center input-group">
          <label class="input-group-label" for="nickname">소개 </label>
          <input
            v-model="newContent"
            class="introduce-padding margin-right-10"
            type="text"
          />
          <!-- <span>변경</span> -->
        </div>
        <!-- <div class="flex-row center input-group">
          <label class="input-group-label" for="nickname">비밀번호</label>
          <input class="input-group-input margin-right-10" type="password">
          <span>변경</span>
        </div> -->
      </div>

      <div class="center">
        <div>
          <q-btn
            @click="updatedUser"
            class="button-style1 bg-blue-7 margin-right-10"
            >수정 완료</q-btn
          >
        </div>
        <div>
          <q-btn class="button-style2 bg-red-6">회원 탈퇴</q-btn>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeMount } from "vue";
import NavBar from "@/layouts/NavBar.vue";
import TabItem from "@/layouts/TabItem.vue";
import { useRouter, useRoute } from "vue-router";
import { fetchUser, updateUser, getUserPk } from "@/js/user/user.js";
import useCookie from "@/js/cookie.js";

const router = useRouter();
const newNickname = ref();
const newContent = ref();
const newprofileImgUrl = ref(null);
const uuid = ref();
const LoggedUserPK = ref();
const userKey = ref();
const { setCookie, getCookie, removeCookie } = useCookie();

onBeforeMount(async () => {
  await getLoggedUserPk();
  await getUser();
  // 주석처리...!
  // await updatedUser(userKey);
});

//로그인한 유저 pk 가져오기
const getLoggedUserPk = async () => {
  try {
    uuid.value = getCookie("uuid");
    const getCurrentUserPk = await getUserPk(uuid.value);
    // console.log(getCurrentUserPk);
    LoggedUserPK.value = getCurrentUserPk;
    // console.log(LoggedUserPK.value)
  } catch (error) {
    console.error("오류 발생!!!:", error);
  }
};

//유저 정보 가져오기
const getUser = async () => {
  try {
    const user = await fetchUser(LoggedUserPK.value);
    console.log(user);
    // console.log(user.userKey)
    userKey.value = user.userKey;
    console.log(userKey.value);
    // console.log(user.introduction)
    // console.log(user.nickname)
    // console.log(user.profileImgUrl)
  } catch (error) {
    console.error("user정보 가져오기 오류 발생:", error);
  }
};

const goBack = function () {
  router.go(-1);
};

//user 정보 수정
const updatedUser = async () => {
  try {
    const userUpdate = await updateUser(
      userKey.value,
      newNickname.value,
      newprofileImgUrl.value,
      newContent.value
      // LoggedUserPK.value,
    );
    console.log("-------2222222222222222------------------");
    console.log("유저 update 실행 완료", userUpdate);
  } catch (error) {
    console.error("유저 수정 에러", error);
  }
};

const selectedProfileImage = ref("");

const handleProfileImageChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = () => {
      selectedProfileImage.value = reader.result;
    };
    reader.readAsDataURL(file);
  }
};

const triggerProfileImageInput = () => {
  // Trigger the file input when the text is clicked
  const profileImageInput = document.getElementById("profileImage");
  if (profileImageInput) {
    profileImageInput.click();
  }
};

// const profileImageStyle = computed(() => ({
//   backgroundImage: `url(${selectedProfileImage.value || getUserProfile()})`
// }))

// const userKey = ref('d25f0071-8bc7-427a-9163-93ed2a6cf4b2');
// const nickname = ref('dd');
// const profileImg = ref('https://image.utoimage.com/preview/cp872722/2022/12/202212008462_500.jpg');
// const introduction = ref('소개글임');
</script>

<style scoped>
.flex-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.text-center {
  text-align: center;
}

.flex-row {
  display: flex;
  flex-direction: row;
}

.align-center {
  align-items: center;
}

.my-feed {
  padding-left: 200px;
  padding-right: 200px;
}

.center {
  /* margin-left: auto;
  margin-right: auto; */
  display: flex;
  justify-content: center;
}
.center2 {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.input-group {
  margin-top: 10px;
  margin-bottom: 10px;
  align-items: center;
}

.input-group-label {
  margin-right: 10px;
  font-weight: bold;
}

.input-group-input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  flex: 0.2;
}

.introduce-padding {
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 4px;
  flex: 0.2;
}
.input-group2 input {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
  flex: 0.2;
}

.button-style1 {
  padding: 8px 12px;
  /* background-color:steelblue; */
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.button-style2 {
  padding: 8px 12px;
  /* background-color:indianred; */
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.margin-right-10 {
  margin-right: 10px;
}

.input-group span:hover {
  color: black;
  font-weight: bold;
  cursor: pointer;
}

.hover-black:hover {
  color: black;
  font-weight: bold;
  cursor: pointer;
}

.profile-img-container {
  width: 70px;
  height: 70px;
  background-image: url("@/assets/img/capture.png");
  /* object-fit : contain; */
  border-radius: 25px;
  background-size: cover;
  background-position: center;
  display: flex; /* Flexbox 사용 */
  justify-content: center; /* 수평 정렬을 위한 가로 중앙 정렬 */
  align-items: center; /* 수직 정렬을 위한 세로 중앙 정렬 */
}
</style>
