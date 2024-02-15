import { defineStore } from "pinia";
import pref from "@/js/config/preference.js";
import axios from "axios";
import useCookie from "@/js/cookie.js";
import { EventSourcePolyfill } from 'event-source-polyfill';

export const useNotificationStore = defineStore("notification", {
  state: () => ({
    inviteModal : false,
    sse : undefined,
    bellCount : 0,
    notificationList : [
    {
      id : 1,
      message : "친구가 요청을 보냈습니다"
    },
    {
      id : 2,
      message : "친구가 노래초대를 했습니다"
    }],

  }),
  actions: {
    async setSse() {
      const { setCookie, getCookie, removeCookie } = useCookie();
      this.sse = new EventSourcePolyfill(pref.app.api.protocol + pref.app.api.host + "/notifications/subscribe",{
        headers: {
          Authorization : getCookie("Authorization"),
          refreshToken : getCookie("refreshToken"),
          heartbeatTimeout: 120000,
          "Content-Type": "application/json",
        },
      });

      this.sse.addEventListener('connect', (response) => {
        console.log('event data: ',response.data);  // "connected!"
      });

      this.bellCount = await axios.get(`${pref.app.api.protocol}${pref.app.api.host}/notifications/count`,{
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        }
      })
      .then((response) => response.data)
      .catch((err) => {
        console.log(err)
      });

      console.log(this.bellCount);
      // axios.post(`/notification/sedNotification${param}`,)


      this.sse.addEventListener('message', (message) => {
        // const { data: receivedConnectData } = e;
        console.log(' \'message\' event data shoud be notificationID: ', message.data);  // "connected!"
        this.bellCount++;
        //알림 모달 열려있을경우
        //추가로 api 날려서 알림객체리스트에 추가. 요청은 e.data로 날라온 알림아이디이다.
        axios.get(`${pref.app.api.protocol}${pref.app.api.host}/notifications/${message.data}`,{
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "application/json",
          }
        })
        .then((response)=>{
          const notification = response.data;
          this.notificationList.splice(0,0,notification); //알림객체 맨앞에 추가.
          console.log("알림객체리스트에 알림추가요~",this.notificationList);
        })
      });
    },
    sendNotification(body){
      const { setCookie, getCookie, removeCookie } = useCookie();
      axios.post(`${pref.app.api.protocol}${pref.app.api.host}/notifications/sendNotification`,JSON.stringify(body),{
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
          console.log("알림 전송 완료.");
      })
      .catch((error) => {
        console.log("메시지 보내는데 문제 생김:",error.message)
      })
    }
  },
  persist : true
});
