import { defineStore } from "pinia";

export const useKaraokeStore = defineStore("karaoke", {
  state: () => ({
    // OpenVidu 객체
    OV: undefined,
    session: undefined,
    mainStreamManager: undefined,
    publisher: undefined,
    subscribers: [],

    // Join form
    mySessionId: "55555",
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
});
