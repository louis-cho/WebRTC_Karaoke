<template>
PointPage
<div>
    <button @click="requestPay">결제하기</button>
  </div>
</template>

<script setup>
import {ref, onMounted} from "vue";

const requestPay = () => {
 // 아임포트 로직을 이곳에 넣으세요
  // 아래는 예시이며, 아임포트 관련 정보를 채워주셔야 합니다.
  IMP.init("imp64622106");

  IMP.request_pay({
    pg: "html5_inicis",
    pay_method: "card",
    merchant_uid: "test_ls8osg6o",
    amount: 3500,
    buyer_tel: "010-1111-1111",
  });

};

onMounted(() => {
  // 외부 스크립트 로드 로직
  const script = document.createElement("script");
  script.src = "https://cdn.iamport.kr/v1/iamport.js";
  document.head.appendChild(script);

    script.onload = () => {
    // 스크립트 로드 후 로직을 실행할 수 있습니다.
    console.log("iamport.js가 로드되었습니다.");
  };
});

</script>

<style>

</style>
