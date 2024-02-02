<template>
  <!-- <div class="q-pa-md q-gutter-sm"> -->
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
          <div class="text-h6" align="center">로그인</div>
          <hr />
        </q-card-section>

        <q-card-section class="q-pt-none">
          <q-input rounded outlined v-model="id" label="아이디" />
        </q-card-section>
        <q-card-section class="q-pt-none">
          <q-input rounded outlined v-model="password" label="비밀번호" />
        </q-card-section>

        <q-card-actions align="center" class="text-primary">
          <q-btn flat label="로그인" />
          <q-btn flat label="회원가입" />
        </q-card-actions>

        <p align="center">아이디 또는 비밀번호를 잊으셨나요?</p>

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
  </div>
</template>

<script>
import { ref } from "vue";

export default {
  setup() {
    const prompt = ref(false);
    const id = ref('');
    const password = ref('');

    return {
      prompt,
      id,
      password
    };
  },
};
</script>

<style scoped>
</style>
