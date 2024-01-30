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
          <q-input v-model="signupForm.nickname" label="닉네임"/>
          <q-input v-model="signupForm.username" label="아이디" />
          <q-input v-model="signupForm.email" label="이메일" />
          <q-input v-model="signupForm.password" label="비밀번호" type="password"/>
          <q-input v-model="signupForm.confirmPassword" label="비밀번호 확인" type="password"/>
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
        confirmPassword:"",
        nickname: "",
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
      // 비밀번호 유효성 검사
      if (!this.isPasswordValid(this.signupForm.password)) {
        alert("비밀번호가 유효하지 않습니다. 비밀번호는 최소 8자 이상이어야 하며, 숫자/영문/특수문자를 모두 포함해야 합니다.");
        return;
      }
      // 비밀번호 일치 여부
      if (this.signupForm.password !== this.signupForm.confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.")
        return
      }
      // 회원가입
      console.log("회원가입:", this.signupForm);
      this.closeSignupModal();
    },

    isPasswordValid(password) {
      // 비밀번호 유효성 검사 정규표현식(8자 이상이며 영문,숫자,특수문자 모두 포함)
      const passwordRegex = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[\W_]).{8,}$/
      return passwordRegex.test(password);
    },

  },
};

// <vu3 스타일>
// import { ref } from 'vue';

// export default {
//   setup() {
//     // data
//     const loginModal = ref(false);
//     const signupModal = ref(false);

//     const loginForm = ref({
//       username: "",
//       password: "",
//     });

//     const signupForm = ref({
//       username: "",
//       email: "",
//       password: "",
//       confirmPassword: "",
//       nickname: "",
//     });

//     // methods
//     const openLoginModal = () => {
//       loginModal.value = true;
//     };

//     const closeLoginModal = () => {
//       loginModal.value = false;
//     };

//     const login = () => {
//       console.log("로그인:", loginForm.value);
//       closeLoginModal();
//     };

//     const openSignupModal = () => {
//       signupModal.value = true;
//     };

//     const closeSignupModal = () => {
//       signupModal.value = false;
//     };

//     const signup = () => {
//       // 비밀번호 유효성 검사
//       if (!isPasswordValid(signupForm.value.password)) {
//         alert("비밀번호가 유효하지 않습니다. 비밀번호는 최소 8자 이상이어야 하며, 숫자/영문/특수문자를 모두 포함해야 합니다.");
//         return;
//       }
//       // 비밀번호 일치 여부
//       if (signupForm.value.password !== signupForm.value.confirmPassword) {
//         alert("비밀번호가 일치하지 않습니다.");
//         return;
//       }
//       // 회원가입
//       console.log("회원가입:", signupForm.value);
//       closeSignupModal();
//     };

//     const isPasswordValid = (password) => {
//       // 비밀번호 유효성 검사 정규표현식(8자 이상이며 영문,숫자,특수문자 모두 포함)
//       const passwordRegex = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[\W_]).{8,}$/;
//       return passwordRegex.test(password);
//     };

//     return {
//       loginModal,
//       signupModal,
//       loginForm,
//       signupForm,
//       openLoginModal,
//       closeLoginModal,
//       login,
//       openSignupModal,
//       closeSignupModal,
//       signup,
//     };
//   },
// };
</script>

<style scoped>
</style>
