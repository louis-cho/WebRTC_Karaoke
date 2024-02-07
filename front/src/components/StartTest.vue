<template>
  <div>
    <q-btn label="시작하기" color="black" @click="prompt = true" />
    <q-dialog v-model="prompt" persistent>
      <q-card style="min-width: 350px">
        <!-- <q-card-actions
          class="q-mb-md"
          style="position: absolute; top: 0; right: 0"
        >
        </q-card-actions> -->
        <q-card-actions align="right" class="text-primary">
          <q-btn flat round dense icon="close" v-close-popup />
        </q-card-actions>

        <q-card-section>
          <div class="text-h6" align="center" >로그인</div>
          <hr />
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input rounded outlined v-model="loginForm.username" label="아이디" />
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-input rounded outlined v-model="loginForm.password" label="비밀번호" type="password" @keyup.enter="loginUser"/>
        </q-card-section>

        <q-card-actions align="center" class="text-primary">
          <q-btn flat label="로그인" @click="loginUser"/>
          <q-btn flat label="회원가입" @click="openSignupModal"/>
        </q-card-actions>

        <p class="gray-text" align="center">아이디 또는 비밀번호를 잊으셨나요?</p>

        <q-card-actions align="center" class="text-primary">
          <div>
            <img
              src="@/assets/icon/kakao_login_medium_narrow.png"
              alt="카카오로그인"
              style="width: 185px; height: 40px"
            />
            <br />
            <img
              src="@/assets/icon/web_light_sq_ctn@1x.png"
              alt="구글로그인"
              style="width: 185px; height: 40px"
            />
          </div>
          <!-- <q-btn @click="handleButtonClick">
            <img src="@\assets\kakao_login_medium_narrow.png" alt="카카오" />
          </q-btn>
          <q-btn @click="handleButtonClick">
            <img src="@/assets/web_light_sq_ctn@1x.png" alt="구글" />
          </q-btn> -->
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
import { getPublicKey, register, login, } from "@/js/encrypt/authRequest.js"
import { ref } from 'vue'



// export default {
//   setup() {
//     const prompt = ref(false);
//     const id = ref('');
//     const password = ref('');

//     return {
//       prompt,
//       id,
//       password
//     };
//   },
// };



export default {
  setup() {
    const prompt = ref(false);
    const id = ref('');
    const password = ref('');

    const loginModal = ref(false);
    const signupModal = ref(false);

    const loginForm = ref({
      username: "",
      password: "",
    });

    const signupForm = ref({
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
      nickname: "",
    });

    async function openLoginModal() {
      await getPublicKey();
      loginModal.value = true;
    }

    function closeLoginModal() {
      loginModal.value = false;
    }

    async function loginUser() {
      console.log("로그인:", loginForm.value);

      let username = loginForm.value.username;
      let password = loginForm.value.password;

      await login(username, password);

      closeLoginModal();
    }

    async function openSignupModal() {
      await getPublicKey();
      signupModal.value = true;
    }

    function closeSignupModal() {
      signupModal.value = false;
    }

    async function signup() {
      // 비밀번호 유효성 검사
      if (!isPasswordValid(signupForm.value.password)) {
        alert("비밀번호가 유효하지 않습니다. 비밀번호는 최소 8자 이상이어야 하며, 숫자/영문/특수문자를 모두 포함해야 합니다.");
        return;
      }
      // 비밀번호 일치 여부
      if (signupForm.value.password !== signupForm.value.confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.")
        return
      }
      // 회원가입
      console.log("회원가입:", signupForm.value);

      let username = signupForm.value.username;
      let password = signupForm.value.password;
      let email = signupForm.value.email;
      let nickname = signupForm.value.nickname;

      await register(username, password, email, nickname);

      closeSignupModal();
    }

    function isPasswordValid(password) {
      // 비밀번호 유효성 검사 정규표현식(8자 이상이며 영문,숫자,특수문자 모두 포함)
      const passwordRegex = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[\W_]).{8,}$/;
      return passwordRegex.test(password);
    }

    return {
      prompt,
      id,
      password,
      loginModal,
      signupModal,
      loginForm,
      signupForm,
      openLoginModal,
      closeLoginModal,
      loginUser,
      openSignupModal,
      closeSignupModal,
      signup,
    };
  },
};
</script>

<style scoped>
.gray-text {
  color: gray;
}
</style>
