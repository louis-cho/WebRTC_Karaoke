<template>
  <div>

    <TabItem/>

    여기 접속하는 순간.. 바로 구독요청 날라가유
    
    {{ notificationMessages }}
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import TabItem from "@/layouts/TabItem.vue";
import Home from "@/pages/HomePage.vue";
const notificationMessages = ref("");

onMounted( ()=>{


  const sse = new EventSource("http://localhost:8081/api/v1/notifications/subscribe");

  console.log(sse);


   sse.addEventListener('connect', (response) => {
  	console.log('event data: ',response.data);  // "connected!"
  });

  axios.post(`/notification/sedNotification${param}`,)

  sse.addEventListener('notification', (e) => {
  	// const { data: receivedConnectData } = e;
   notificationMessages.value = notificationMessages.value + e.data;
  	console.log(' \'notification\' event data: ',e.data);  // "connected!"
  });
})

</script>

<style>

</style>
