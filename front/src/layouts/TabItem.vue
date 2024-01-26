<template>
  <!-- <div class="q-pa-md"> -->
    <div class="parent">

      <div @click="goToPage('/')" style="cursor: pointer;">
        <img
          src="src/assets/로고1-removebg-preview.png"
          alt="Logo"
          width="150"
          class="d-inline-block align-text-top"
        />
      </div>

      <div>
        <q-tabs
          v-model="tab"
        >
          <q-tab name="feed" icon="feed" label="피드" />
          <q-tab name="karaoke" icon="library_music" label="노래방" @click="goToPage('/karaoke')" />
          <q-tab name="message" icon="send" label="메시지" />
        </q-tabs>
      </div>

      <div class="container">
        <q-tabs
          v-model="tab"
        >
          <q-tab name="person" icon="person"  />
          <q-tab name="notifications" icon="notifications" />
          <q-tab name="menu" icon="menu" />
        </q-tabs>
      </div>

    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const tab = ref('mails')
const router = useRouter()

// 이벤트 핸들러 추가
const goToPage = (path) => {
  router.push(path);
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

/* .full-width {
  width: 100%;
  flex-grow: 1;
} */

/* .container {
  display: flex;
  justify-content: center;
  gap: 150x;

} */


</style>
