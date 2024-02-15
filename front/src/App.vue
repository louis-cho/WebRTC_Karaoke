<template>
  <div class="background">
    <router-view />
  </div>
</template>

<script setup></script>

<!-- 폰트 추가 -->
<style>
@font-face {
  font-family: "YCloverBold"; /* 폰트 이름 지정 */
  src: url("@/assets/font/YCloverBold.ttf") format("truetype"); /* 폰트 파일 경로와 형식 지정 */
}

body {
  font-family: "YCloverBold", sans-serif; /* 지정한 폰트 사용 */
}

.background {
  background-image: url("@/assets/img/mikebackground.jpg");
}
</style>
