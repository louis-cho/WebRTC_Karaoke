<template>
  <!-- div class="q-pa-md" -->
  <div class="parent">
    <div @click="goToPage('/')" style="cursor: pointer;">
        <img
          src="@/assets/icon/logo1-removebg-preview.png"
          alt="Logo"
          width="150"
          class="d-inline-block align-text-top"
        />
    </div>
    <div class="center">
      <!-- 시작하기 버튼 -->
      <StartTest/>
    </div>
  </div>
</template>

<script>
import StartTest from '@/components/StartTest.vue';
import { useRouter } from 'vue-router';

export default {
  components: {
    StartTest
  },
  setup() {
    const router = useRouter();

    const goToPage = (path) => {
      router.push(path);
    };

    return {
      goToPage
    };
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
}

.center {
  display: flex;
  flex-direction: column;
  justify-content: center;
}


</style>
