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
          <q-input rounded outlined v-model="search" label="비밀번호" >
          <template v-slot:append>
            <q-icon name="search" />
          </template>
          </q-input>
        </q-card-section>
      <div>
        <!-- 친구들 목록 뜨게 -->
      </div>
      </q-card>
    </q-dialog>

  </div>
</template>

<script setup>
import { ref } from 'vue'

const icon = ref(false)

</script>

<style scoped>
.width-5 {
  width: 5%;
}
.width-10 {
  width: 10%;
}
.width-15 {
  width: 15%;
}
.width-20 {
  width: 20%;
}
.width-25 {
  width: 25%;
}
.width-30 {
  width: 30%;
}
.width-35 {
  width: 35%;
}
.width-40 {
  width: 40%;
}
.width-45 {
  width: 45%;
}
.width-50 {
  width: 50%;
}
.width-55 {
  width: 55%;
}
.width-60 {
  width: 60%;
}.width-65 {
  width: 65%;
}.width-70 {
  width: 70%;
}.width-75 {
  width: 75%;
}.width-80 {
  width: 80%;
}.width-85 {
  width: 85%;
}.width-90 {
  width: 90%;
}.width-95 {
  width: 95%;
}.width-100 {
  width: 100%;
}



</style>
