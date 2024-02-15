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
  text-shadow: 1px 1px 1px #0ff;
  color: #000;
  font-size: 250%;
  font-family: "YCloverBold", sans-serif; /* 지정한 폰트 사용 */
}

.background {
  background-image: url("@/assets/img/mic.jpg");
  background-size: 100% auto; /* 가로로 맞추고 아래로는 100% 반복 */
  background-position: center; /* 이미지를 가운데 정렬 */
  background-repeat: repeat-y; /* 아래로 반복 */
  background-attachment: fixed; /* 배경 이미지 고정 */
}
</style>
