<template>
  <!-- div class="q-pa-md" -->
  <div class="parent">
    <div>
      <img
        src="@/assets/icon/logo1-removebg-preview.png"
        alt="Logo"
        width="150"
        class="d-inline-block align-text-top"
      />
    </div>
    <div>
      <!-- 시작하기 버튼 -->
      <StartTest/>
    </div>
  </div>
</template>

<script>
import StartTest from '@/components/StartTest.vue';
export default {
  components: {
    StartTest
  },
  // ...
}
</script>

<style scoped>
.parent {
  /* width : 300px; */
  /* height : 100px; */
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}



/* .child {
  flex: 1;
} */
</style>
