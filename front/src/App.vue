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
  text-shadow:1px 1px 1px #0ff;
  color:#000;
  font-size: 250%;
  font-family: "YCloverBold", sans-serif; /* 지정한 폰트 사용 */
}

.background {
background: rgb(238,174,202);
background: radial-gradient(circle, rgba(238,174,202,1) 0%, rgba(148,187,233,1) 100%);
}
</style>
