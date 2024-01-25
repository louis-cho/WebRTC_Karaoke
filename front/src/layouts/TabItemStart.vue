<template>
  <!-- div class="q-pa-md" -->
  <div>
    <div class="parent">
      <div>
        <img
          src="src/assets/로고1-removebg-preview.png"
          alt="Logo"
          width="150"
          class="d-inline-block align-text-top"
        />
      </div>
      <div>
        <StartTest/>
      </div>


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
  /* border : 3px solid red; */
}

/* .child {
  flex: 1;
} */
</style>
