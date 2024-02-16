import { defineStore } from "pinia";
import { OpenVidu } from "openvidu-browser";
import axios from "axios";
import pref from "@/js/config/preference.js";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
export const useKaraokeStore = defineStore("karaoke", {
  state: () => ({
    APPLICATION_SERVER_URL: pref.app.api.protocol + pref.app.api.host,

    createModal: false,
    updateModal: false,
    toggleModals: {
      "karaoke-chat": false,
      "input-controller": false,
      "reserve-song": false,
      "reserve-list": false,
    },
    singing: false,
    singUser: undefined,
    songMode: false,
    newReserve: false,
    singUserOut: false,
    song: null,

    sessionName: undefined,
    userName: "로그인 하세요",
    isModerator: false,
    kicked: true,

    // OpenVidu 객체
    OV: undefined,
    session: undefined,
    mainStreamManager: undefined,
    publisher: undefined,
    subscribers: [],
    token: undefined,

    // 방 설정을 위한 변수
    numberOfParticipants: undefined,
    isPrivate: undefined,
    password: undefined,

    // 채팅창을 위한 변수
    inputMessage: "",
    messages: [],

    // 카메라 및 오디오 설정을 위한 변수
    muted: false, // 기본은 음소거 비활성화
    camerOff: false, // 기본 카메라 활성화
    reservedSongs: [],
    reservedSongsLength: 0,
  }),
  actions: {
    async createSession(
      sessionName,
      numberOfParticipants,
      isPrivate,
      password
    ) {
      this.joinSession();

      this.numberOfParticipants = numberOfParticipants;
      this.isPrivate = isPrivate;
      this.password = password;

      await axios.post(
        this.APPLICATION_SERVER_URL + "/karaoke/sessions/createSession",
        {
          sessionName: sessionName,
          userName: this.userName,
          numberOfParticipants: numberOfParticipants,
          isPrivate: isPrivate,
          password: password,
        },
        {
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "application/json",
          },
        }
      );

      this.isModerator = true;
    },

    joinSession() {
      this.OV = new OpenVidu();
      this.session = this.OV.initSession();

      // --- 3) 세션에서 이벤트 발생 시 동작 지정 ---
      this.session.on("streamCreated", ({ stream }) => {
        const subscriber = this.session.subscribe(stream, undefined, {
          subscribeToAudio: true,
          subscribeToVideo: true,
        });

        this.subscribers.push(subscriber);

        this.session.signal({
          data: JSON.stringify({ songMode: this.songMode }),
          type: "songMode",
        });
      });

      this.session.on("streamDestroyed", ({ stream }) => {
        if (JSON.parse(stream.connection.data).clientData == this.singUser) {
          this.mainStreamManager = this.publisher;
          this.singUserOut = true;
        }

        const index = this.subscribers.indexOf(stream.streamManager, 0);
        if (index >= 0) {
          this.subscribers.splice(index, 1);
        }
      });

      this.session.on("sessionDisconnected", () => {
        console.log(this.kicked);
        this.singing = false;

        if (this.kicked == true) {
          alert("추방당했습니다.");
          window.location.href = "/";
        }
      });

      this.session.on("exception", ({ exception }) => {
        console.warn(exception);
      });

      this.session.on("signal:chat", (event) => {
        const messageData = JSON.parse(event.data);
        if (event.from.connectionId === this.session.connection.connectionId) {
          messageData["username"] = "나";
        }
        this.messages.push(messageData);
      });

      this.session.on("signal:songMode", (event) => {
        const songModeData = JSON.parse(event.data);
        this.songMode = songModeData.songMode;
      });

      this.session.on("signal:sing", (event) => {
        const singData = JSON.parse(event.data);
        this.singing = singData.singing;
        this.singUser = singData.singUser;

        if (singData.singUser == this.userName) {
          this.muted = false;
          this.mainStreamManager = this.publisher;
        } else {
          this.muted = true;
          for (const subscriber of this.subscribers) {
            if (
              singData.singUser ==
              JSON.parse(subscriber.stream.connection.data).clientData
            ) {
              this.mainStreamManager = subscriber;
            }
          }
        }
        this.publisher.publishAudio(!this.muted);
      });

      this.session.on("signal:reserve", () => {
        this.newReserve = true;
      });
    },

    async getToken(sessionName) {
      if (this.session == undefined) {
        this.joinSession();
      }

      // 녹화 확인
      const isRecording = await axios.post(
        this.APPLICATION_SERVER_URL + "/karaoke/sessions/checkRecording",
        { sessionName: sessionName },
        {
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "application/json",
          },
        }
      );

      if (isRecording.data === "") {
        alert("존재하지 않는 방입니다.");
        return false;
      }

      if (isRecording.data) {
        alert("노래 중에 입장이 불가능합니다.");
        return false;
      }

      // 인원수 확인
      const participants = await axios.post(
        this.APPLICATION_SERVER_URL + "/karaoke/sessions/checkNumber",
        { sessionName: sessionName },
        {
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "application/json",
          },
        }
      );
      if (!participants.data) {
        alert("인원이 초과했습니다!");
        return false;
      }

      // 비공개 확인
      const isPrivate = await axios.post(
        this.APPLICATION_SERVER_URL + "/karaoke/sessions/checkPrivate",
        { sessionName: sessionName },
        {
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "application/json",
          },
        }
      );

      // 비밀번호 확인
      if (isPrivate.data) {
        // 모달에서 값을 입력받는 함수 호출
        const userInput = await this.showModal();

        const response = await axios.post(
          this.APPLICATION_SERVER_URL + "/karaoke/sessions/checkPassword",
          { sessionName: sessionName, password: userInput },
          {
            headers: {
              Authorization: getCookie("Authorization"),
              refreshToken: getCookie("refreshToken"),
              "Content-Type": "application/json",
            },
          }
        );

        if (!response.data) {
          alert("비밀번호가 틀렸습니다!");
          return false;
        }
      }

      // 토큰 생성
      const token = await axios.post(
        this.APPLICATION_SERVER_URL + "/karaoke/sessions/getToken",
        {
          sessionName: sessionName,
          // filter 사용을 위해 create connection 시 body를 추가
          type: "WEBRTC",
          role: "PUBLISHER",
          kurentoOptions: {
            allowedFilters: ["GStreamerFilter", "FaceOverlayFilter"],
          },
        },
        {
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "application/json",
          },
        }
      );

      this.session
        .connect(token.data, { clientData: this.userName })
        .then(() => {
          // 토근을 저장한다.
          this.token = token.data;
          this.sessionName = sessionName;
          this.muted = false;
          this.camerOff = false;

          // 원하는 속성으로 초기화된 발행자를 만듭니다.
          let publisher_tmp = this.OV.initPublisher(undefined, {
            audioSource: undefined, // 오디오의 소스. 정의되지 않으면 기본 마이크
            videoSource: undefined, // 비디오의 소스. 정의되지 않으면 기본 웹캠
            publishAudio: !this.muted, // 마이크 음소거 여부를 시작할지 여부
            publishVideo: !this.camerOff, // 비디오 활성화 여부를 시작할지 여부
            // resolution: "1280x720", // 비디오의 해상도
            resolution: "320x240",
            frameRate: 30, // 비디오의 프레임 속도
            insertMode: "APPEND", // 비디오가 대상 요소 'video-container'에 어떻게 삽입되는지
            mirror: true, // 로컬 비디오를 반전할지 여부
          });

          console.log(publisher_tmp);

          // 페이지에서 주요 비디오를 설정하여 웹캠을 표시하고 발행자를 저장합니다.
          this.mainStreamManager = publisher_tmp;
          this.publisher = publisher_tmp;

          // --- 6) 스트림을 발행하고, 원격 스트림을 수신하려면 subscribeToRemote() 호출하기 ---
          this.publisher.subscribeToRemote();
          this.session.publish(this.publisher);
        })
        .catch((error) => {
          console.log(
            "세션에 연결하는 중 오류가 발생했습니다:",
            error.code,
            error.message
          );
        });

      window.addEventListener("beforeunload", this.leaveSession);
      return true;
    },

    async leaveSession() {
      this.kicked = false;

      // 방장이면 세션 닫기
      if (this.isModerator) {
        await axios.post(
          this.APPLICATION_SERVER_URL + "/karaoke/sessions/closeSession",
          {
            sessionName: this.sessionName,
            token: this.token,
          },
          {
            headers: {
              Authorization: getCookie("Authorization"),
              refreshToken: getCookie("refreshToken"),
              "Content-Type": "application/json",
            },
          }
        );
      } else {
        await axios.post(
          this.APPLICATION_SERVER_URL + "/karaoke/sessions/removeToken",
          {
            sessionName: this.sessionName,
            token: this.token,
          },
          {
            headers: {
              Authorization: getCookie("Authorization"),
              refreshToken: getCookie("refreshToken"),
              "Content-Type": "application/json",
            },
          }
        );
      }

      // 모든 속성 비우기...
      this.OV = undefined;
      this.session = undefined;
      this.mainStreamManager = undefined;
      this.publisher = undefined;
      this.subscribers = [];
      this.token = undefined;

      this.sessionName = undefined;
      this.isModerator = false;
      this.kicked = true;
      this.inputMessage = "";
      this.messages = [];
      this.createModal = false;
      this.updateModal = false;
      this.toggleModals = {
        "karaoke-chat": false,
        "input-controller": false,
        "reserve-song": false,
        "reserve-list": false,
      };
      this.singing = false;
      this.singUser = undefined;
      this.songMode = false;
      this.newReserve = false;
      this.reservedSongs = [];
      this.reservedSongsLength = 0;
      this.singUserOut = false;
      this.sing = null;

      // beforeunload 리스너 제거
      window.removeEventListener("beforeunload", this.leaveSession);
    },

    // 사용자에게 입력을 받는 모달을 띄우는 함수
    showModal() {
      return new Promise((resolve, reject) => {
        const userInput = prompt("비밀번호를 입력하세요:");

        resolve(userInput);
      });
    },
  },
  persist: true,
});
