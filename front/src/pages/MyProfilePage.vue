<template>
  <div>
    <TabItem/>
    <h3>내 피드 페이지</h3>

    <div>
      <!-- 첫번째 div -->
      <div class="first">
        <div >
          <img src="@/assets/icon/back.png" alt="뒤로가기">
        </div>
        <div>닉네임</div>
        <div class="first">
          <div>
            <img src="@/assets/icon/send.png" alt="dm">
          </div>
          <div>
            <img src="@/assets/icon/wallet.png" alt="지갑">
          </div>
          <div>
            <img src="@/assets/icon/setting.png" alt="설정">
          </div>
        </div>
      </div>

      <!-- 두번째 div -->
      <div></div>
    </div>

  </div>
</template>

<script setup>
import { ref } from "vue";
import TabItem from "@/layouts/TabItem.vue";
</script>

<style scoped>
.first {
  display:flex;
  flex-direction: row;
  justify-content: space-between;
}

.width_5 {
  width: 5%;
}
.width_10 {
  width: 10%;
}
.width_15 {
  width: 15%;
}
.width_20 {
  width: 20%;
}
.width_25 {
  width: 25%;
}
.height_10 {
  height:10%
}
</style>
