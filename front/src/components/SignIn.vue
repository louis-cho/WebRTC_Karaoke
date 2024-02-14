<template>
  <div class="parent">
    <div @click="goToPage('/')" style="cursor: pointer">
      <img
        src="@/assets/icon/logo1-removebg-preview.png"
        alt="Logo"
        width="150"
        class="d-inline-block align-text-top"
      />
    </div>
    <div class="center">
      <div class="login-margin">
        <q-btn @click="openLoginModal" color="black">로그인</q-btn>
      </div>
      <div>
        <q-btn @click="openSignupModal" color="black">회원가입</q-btn>
      </div>
    </div>

    <!-- 로그인 모달 -->

    <q-dialog v-model="loginModal" persistent>
      <q-card>
        <q-card-section>
          <q-input v-model="loginForm.username" label="아이디" />
          <q-input
            v-model="loginForm.password"
            label="비밀번호"
            type="password"
            @keydown.enter="login"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn label="취소" color="primary" @click="closeLoginModal" />
          <q-btn label="로그인" color="positive" @click="login" />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 회원가입 모달 -->

    <q-dialog v-model="signupModal" persistent>
      <q-card>
        <q-card-section>
          <q-input v-model="signupForm.nickname" label="닉네임" />
          <q-input v-model="signupForm.username" label="아이디" />
          <q-input v-model="signupForm.email" label="이메일" />
          <q-input
            v-model="signupForm.password"
            label="비밀번호"
            type="password"
          />
          <q-input
            v-model="signupForm.confirmPassword"
            label="비밀번호 확인"
            type="password"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn label="취소" color="primary" @click="closeSignupModal" />
          <q-btn label="가입" color="positive" @click="signup" />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import { useRouter } from "vue-router";
import { getPublicKey, register, login } from "@/js/encrypt/authRequest.js";
import { useNotificationStore } from "@/stores/notificationStore.js";
const notificationStore = useNotificationStore();
// const router = useRouter()

export default {
  data() {
    return {
      loginModal: false,
      signupModal: false,
      loginForm: {
        username: "",
        password: "",
      },
      signupForm: {
        username: "",
        email: "",
        password: "",
        confirmPassword: "",
        nickname: "",
      },
    };
  },
  methods: {
    goToPage(path) {
      this.$router.push(path);
    },
    async openLoginModal() {
      await getPublicKey();
      this.loginModal = true;
    },
    closeLoginModal() {
      this.loginModal = false;
    },
    async subNotification() {
      await notificationStore.setSse();
    },
    async login() {
      console.log("로그인:", this.loginForm);

      let username = this.loginForm.username;
      let password = this.loginForm.password;

      await login(username, password);
      await this.subNotification();

      this.closeLoginModal();
      // console.log(isLoggedIn)
    },

    async openSignupModal() {
      await getPublicKey();
      this.signupModal = true;
    },
    closeSignupModal() {
      this.signupModal = false;
    },
    async signup() {
      // 비밀번호 유효성 검사
      if (!this.isPasswordValid(this.signupForm.password)) {
        alert(
          "비밀번호가 유효하지 않습니다. 비밀번호는 최소 8자 이상이어야 하며, 숫자/영문/특수문자를 모두 포함해야 합니다."
        );
        return;
      }
      // 비밀번호 일치 여부
      if (this.signupForm.password !== this.signupForm.confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
      }
      // 회원가입
      console.log("회원가입:", this.signupForm);

      let username = this.signupForm.username;
      let password = this.signupForm.password;
      let email = this.signupForm.email;
      let nickname = this.signupForm.nickname;

      await register(username, password, email, nickname);

      this.closeSignupModal();
    },

    isPasswordValid(password) {
      // 비밀번호 유효성 검사 정규표현식(8자 이상이며 영문,숫자,특수문자 모두 포함)
      const passwordRegex = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[\W_]).{8,}$/;
      return passwordRegex.test(password);
    },
  },
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

.center {
  display: flex;
  flex-direction: row;
  align-items: center;
}

/* 로그인 버튼 오른쪽 마진 */
.login-margin {
  margin-right: 10px;
}
</style>
