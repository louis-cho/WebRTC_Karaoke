import { defineStore } from "pinia";
import { OpenVidu } from "openvidu-browser";
import axios from "axios";
import { useRouter } from "vue-router";

axios.defaults.headers.post["Content-Type"] = "application/json";

export const useKaraokeStore = defineStore("karaoke", {
  state: () => ({
    APPLICATION_SERVER_URL:
      process.env.NODE_ENV === "production" ? "https://i10a705.p.ssafy.io/" : "http://localhost:8081/",

    // OpenVidu 객체
    OV: undefined,
    session: undefined,
    mainStreamManager: undefined,
    publisher: undefined,
    subscribers: [],
    token: undefined,

    // Join form
    mySessionId: "1234",
    myUserName: "참가자" + Math.floor(Math.random() * 100),

    // 채팅창을 위한 변수
    inputMessage: "",
    messages: [],

    // 카메라 및 오디오 설정을 위한 변수
    muted: false, // 기본은 음소거 비활성화
    camerOff: false, // 기본 카메라 활성화
    selectedCamera: "", // 카메라 변경시 사용할 변수
    selectedAudio: "", // 오디오 변경시 사용할 변수
    cameras: [],
    audios: [],
  }),
  actions: {
    async joinSession(sessionId) {
      this.mySessionId = sessionId;

      // --- 1) OpenVidu 객체 생성 ---
      this.OV = new OpenVidu();

      // --- 2) 세션 초기화 ---
      this.session = this.OV.initSession();

      // --- 3) 세션에서 이벤트 발생 시 동작 지정 ---
      // 새로운 스트림이 생성될 때마다...
      this.session.on("streamCreated", ({ stream }) => {
        const subscriber = this.session.subscribe(stream, undefined, {
          subscribeToAudio: true,
          subscribeToVideo: true,
        });
        this.subscribers.push(subscriber);
      });

      // 스트림이 파괴될 때마다...
      this.session.on("streamDestroyed", ({ stream }) => {
        const index = this.subscribers.indexOf(stream.streamManager, 0);
        if (index >= 0) {
          this.subscribers.splice(index, 1);
        }
      });

      // 비동기 예외가 발생할 때마다...
      this.session.on("exception", ({ exception }) => {
        console.warn(exception);
      });

      // 채팅 이벤트 수신 처리 함. session.on이 addEventListener 역할인 듯합니다.
      this.session.on("signal:chat", (event) => {
        const messageData = JSON.parse(event.data);
        if (event.from.connectionId === this.session.connection.connectionId) {
          // 나와 보낸이가 같으면
          messageData["username"] = "나";
        }
        this.messages.push(messageData);
      });

      // --- 4) 유효한 사용자 토큰으로 세션에 연결 ---
      // OpenVidu 배포에서 토큰 가져오기
      await this.getToken(this.mySessionId).then((token) => {
        // 첫 번째 매개변수는 토큰입니다. 두 번째 매개변수는 모든 사용자가 'streamCreated' 이벤트에서 가져올 수 있는 것입니다.
        // 'streamCreated' (속성 Stream.connection.data) 및 닉네임으로 DOM에 추가됩니다.
        this.session
          .connect(token, { clientData: this.myUserName })
          .then(() => {
            // 토근을 저장한다.
            this.token = token;
            // --- 5) 원하는 속성으로 자신의 카메라 스트림 가져오기 ---

            // 원하는 속성으로 초기화된 발행자를 만듭니다 (video-container'에 비디오가 삽입되지 않도록 OpenVidu에게 처리를 맡기지 않음).
            let publisher_tmp = this.OV.initPublisher(undefined, {
              audioSource: undefined, // 오디오의 소스. 정의되지 않으면 기본 마이크
              videoSource: undefined, // 비디오의 소스. 정의되지 않으면 기본 웹캠
              publishAudio: !this.muted, // 마이크 음소거 여부를 시작할지 여부
              publishVideo: !this.camerOff, // 비디오 활성화 여부를 시작할지 여부
              resolution: "1280x720", // 비디오의 해상도
              frameRate: 30, // 비디오의 프레임 속도
              insertMode: "APPEND", // 비디오가 대상 요소 'video-container'에 어떻게 삽입되는지
              mirror: false, // 로컬 비디오를 반전할지 여부
            });

            // 페이지에서 주요 비디오를 설정하여 웹캠을 표시하고 발행자를 저장합니다.
            this.mainStreamManager = publisher_tmp;
            this.publisher = publisher_tmp;

            // --- 6) 스트림을 발행하고, 원격 스트림을 수신하려면 subscribeToRemote() 호출하기 ---
            this.publisher.subscribeToRemote();
            this.session.publish(this.publisher);
            this.getMedia(); // 세션이 만들어졌을 때 미디어를 불러옵니다.
          })
          .catch((error) => {
            console.log(
              "세션에 연결하는 중 오류가 발생했습니다:",
              error.code,
              error.message
            );
          });
      });

      window.addEventListener("beforeunload", this.leaveSession);
    },

    async leaveSession() {
      // --- 7) 'removeToken' 메서드를 호출하여 세션을 나갑니다. ---
      await axios.post(
        this.APPLICATION_SERVER_URL + "api/v1/karaoke/sessions/removeToken",
        {
          sessionName: this.mySessionId,
          token: this.token,
        },
        {
          headers: { "Content-Type": "application/json" },
        }
      );

      // 모든 속성 비우기...
      this.session = undefined;
      this.mainStreamManager = undefined;
      this.publisher = undefined;
      this.subscribers = [];
      this.OV = undefined;

      // beforeunload 리스너 제거
      window.removeEventListener("beforeunload", this.leaveSession);
    },

    async getToken(mySessionId) {
      // 토큰 생성
      const response = await axios.post(
        this.APPLICATION_SERVER_URL + "api/v1/karaoke/sessions/getToken",
        {
          sessionName: mySessionId,
          // filter 사용을 위해 create connection 시 body를 추가
          type: "WEBRTC",
          role: "PUBLISHER",
          kurentoOptions: {
            allowedFilters: ["GStreamerFilter", "FaceOverlayFilter"],
          },
        },
        {
          headers: { "Content-Type": "application/json" },
        }
      );
      return response.data; // 토큰 반환
    },

    // 캠, 오디오 등 기기와 관련된 함수
    // 카메라와 오디오를 가져옴.
    async getMedia() {
      try {
        const devices = await navigator.mediaDevices.enumerateDevices();
        this.cameras = devices.filter((device) => device.kind === "videoinput");
        this.audios = devices.filter((device) => device.kind === "audioinput");

        // 첫번째 카메라와 오디오를 선택
        this.selectedCamera = this.cameras[0];
        this.selectedAudio = this.audios[0];
      } catch (error) {
        console.error("Error getting media devices:", error);
      }
    },
  },
});
