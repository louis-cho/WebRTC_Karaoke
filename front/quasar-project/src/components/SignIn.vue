<template>
  <div>
    <q-btn @click="openLoginModal" color="black">로그인</q-btn>
    <q-btn @click="openSignupModal" color="black">회원가입</q-btn>

    <!-- 로그인 모달 -->

    <q-dialog v-model="loginModal" persistent>
      <q-card>
        <q-card-section>
          <q-input v-model="loginForm.username" label="아이디" />
          <q-input
            v-model="loginForm.password"
            label="비밀번호"
            type="password"
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
          <q-input v-model="signupForm.username" label="아이디" />
          <q-input v-model="signupForm.email" label="이메일" type="email" />
          <q-input
            v-model="signupForm.password"
            label="비밀번호"
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
      },
    };
  },
  methods: {
    openLoginModal() {
      this.loginModal = true;
    },
    closeLoginModal() {
      this.loginModal = false;
    },
    login() {
      // 로그인 로직을 여기에 추가 (서버 연동 등)
      console.log("로그인:", this.loginForm);
      this.closeLoginModal();
    },
    openSignupModal() {
      this.signupModal = true;
    },
    closeSignupModal() {
      this.signupModal = false;
    },
    signup() {
      // 회원가입 로직을 여기에 추가 (서버 연동 등)
      console.log("회원가입:", this.signupForm);
      this.closeSignupModal();
    },
  },
};
</script>

<style scoped></style>
