<template>
 <div>
    <q-btn label="유저검색" color="black" @click="icon = true" />

    <q-dialog v-model="icon">
      <q-card>
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6" >유저검색</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <!-- card-section 부분 수정해야함 -->
        <q-card-section class="q-pt-none">
          <q-input rounded outlined v-model="password" label="비밀번호" />
        </q-card-section>

      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const icon = ref(false)

</script>

<style scoped>

</style>
