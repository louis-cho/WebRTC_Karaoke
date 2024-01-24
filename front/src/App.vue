<template>
  <router-view />
  <!-- <signIn/> -->

  <div id="main-container">
    <!-- session이 false일때! 즉, 방에 들어가지 않았을때 -->
    <div id="join" v-if="!session">
      <div id="img-div">
        <!-- <img src="resources/images/openvidu_grey_bg_transp_cropped.png" alt="오픈비두 로고 이미지임"> --><!-- 이미지부분 -->
      </div>
      <div id="join-dialog">
        <h1>Join a video session</h1>
        <div>
          <p>
            <label>Participant</label>
            <input v-model="myUserName" type="text" required />
          </p>
          <p>
            <label>Session</label>
            <input v-model="mySessionId" type="text" required />
          </p>
          <p>
            <button @click="joinSession">
              Join!
            </button>
          </p>
        </div>
      </div>
    </div>
    <!-- session이 true일때! 즉, 방에 들어갔을 때 -->
    <div id="session" v-if="session">
      <div id="session-header">
        <h1 id="session-title">{{ mySessionId }}</h1>
        <input
          type="button"
          id="buttonLeaveSession"
          @click="leaveSession"
          value="Leave session"
        />
      </div>
      <!-- 내 캠 -->
      <div id="main-video">
        <UserVideo :stream-manager="mainStreamManagerComputed" />
      </div>
      <!-- 모든 캠 -->
      <div id="video-container">
        <UserVideo :stream-manager="publisherComputed" @click="updateMainVideoStreamManager(publisher)" />
        <UserVideo
          v-for="sub in subscribersComputed"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          @click="updateMainVideoStreamManager(sub)"
        />
      </div>
      <!-- 방에 들어갔을 때 같이 보이게 될 채팅창 -->
      <!-- 나중에 <chat-winow />로 넘길수 있도록 해보자. -->
      <div id="chat-container">
        <div id="chat-window">
          <ul id="chat-history">
            <li v-for="(message, index) in messages" :key="index">
              <strong>{{message.username}}:</strong> {{message.message}}
            </li>
          </ul>
        </div>
        <form id="chat-write">
          <input type="text" placeholder="전달할 내용을 입력하세요." v-model="inputMessage">
          <button @click="sendMessage">전송</button>
        </form>
      </div>
      <!-- 캠활성화, 음소거 버튼 -->
      <button id="camera-activate" @click="handleCameraBtn">캠 비활성화</button>
      <button id="mute-activate" @click="handleMuteBtn">음소거 활성화</button>
      <!-- 캠,오디오 선택 옵션 -->
      <div>
        <select name="cameras" @change="handleCameraChange">
          <option disabled>사용할 카메라를 선택하세요</option>
        </select>
        <select name="audios" @change="handleAudioChange">
          <option disabled>사용할 마이크를 선택하세요</option>
        </select>
      </div>
    </div>
  </div>

</template>

<script setup>
  import { ref, computed } from 'vue'
  import axios from 'axios'
  import { OpenVidu } from "openvidu-browser";
  import UserVideo from "@/components/UserVideo.vue";

  axios.defaults.headers.post["Content-Type"] = "application/json";

  const APPLICATION_SERVER_URL = process.env.NODE_ENV === 'production' ? '' : 'http://localhost:8081/';

  // OpenVidu objects
  const OV = ref(undefined)
  const session = ref(undefined)
  let mainStreamManager = ref(undefined)
  const publisher = ref(undefined)
  const subscribers = ref([])

  // Join form
  const mySessionId = ref("SessionCrome")
  const myUserName = ref("Participant" + Math.floor(Math.random() * 100))


  /////////////////////채팅창을 위한 부분임.
  const inputMessage = ref("")
  const messages = ref([])
  ///////////////////
  ///////////////////카메라 및 오디오 설정을 위한 부분임
  const muted = ref(false)       // 기본은 음소거 비활성화
  const camerOff = ref(false)    // 기본 카메라 활성화
  const selectedCamera = ref("")  // 카메라 변경시 사용할 변수
  const selectedAudio  = ref("")  // 오디오 변경시 사용할 변수
  ////다시그려내기 위해 computed 작성
  const mainStreamManagerComputed = computed(() => mainStreamManager.value);
  const publisherComputed = computed(() => publisher.value);
  // const subscribersComputed = computed(() => subscribers);
  const subscribersComputed = computed(() => subscribers.value);
  ////
  ///////////////////




  // vue2에서의 methods 부분을 vue3화 시키기
  function joinSession() {
    // --- 1) Get an OpenVidu object ---
    OV.value = new OpenVidu()

    // --- 2) Init a session ---
    session.value = OV.value.initSession()

    // --- 3) Specify the actions when events take place in the session ---
    // On every new Stream received...
    session.value.on("streamCreated", ( {stream} )=> {
      // const subscriber = session.subscribe(stream)
      const subscriber = session.value.subscribe(stream)
      subscribers.value.push(subscriber)
    })

    // On every Stream destroyed...
    session.value.on("streamDestroyed", ( {stream} ) => {
      const index = subscribers.value.indexOf(stream.streamManager, 0)
      if(index >= 0){
        subscribers.value.splice(index, 1)
      }
    })

    // On every asynchronous exception...
    session.value.on("exception", ({ exception }) => {
      console.warn(exception);
    });

    // 채팅 이벤트 수신 처리 함. session.on이 addEventListenr 역할인듯.
    session.value.on('signal:chat', (event) => { // event.from.connectionId === session.value.connection.connectionId 이건 나와 보낸이가 같으면임
      const messageData = JSON.parse(event.data);
      if(event.from.connectionId === session.value.connection.connectionId){
        messageData['username'] = '나'
      }
      messages.value.push(messageData);
    });


    // --- 4) Connect to the session with a valid user token ---
    // Get a token from the OpenVidu deployment
    // getToken(mySessionId).then((token) => {
    getToken(mySessionId.value).then((token) => {
      // First param is the token. Second param can be retrieved by every user on event
      // 'streamCreated' (property Stream.connection.data), and will be appended to DOM as the user's nickname
      // session.value.connect(token, {clientData: myUserName})
      session.value.connect(token, {clientData: myUserName.value})
      .then(() => {
          // --- 5) Get your own camera stream with the desired properties ---

          // const cameraSelect = document.querySelector('select[name="cameras"]');
          // const audioSelect = document.querySelector('select[name="audios"]');

          // Init a publisher passing undefined as targetElement (we don't want OpenVidu to insert a video
          // element: we will manage it on our own) and with the desired properties
          let publisher_tmp = OV.value.initPublisher(undefined, {
            audioSource: undefined, // The source of audio. If undefined default microphone
            videoSource: undefined, // The source of video. If undefined default webcam
            // audioSource: audioSelect.value, // The source of audio. If undefined default microphone
            // videoSource: cameraSelect.value, // The source of video. If undefined default webcam
            // publishAudio: true, // Whether you want to start publishing with your audio unmuted or not
            // publishVideo: true, // Whether you want to start publishing with your video enabled or not
            publishAudio: !muted.value, // Whether you want to start publishing with your audio unmuted or not
            publishVideo: !camerOff.value, // Whether you want to start publishing with your video enabled or not
            resolution: "640x480", // The resolution of your video
            frameRate: 30, // The frame rate of your video
            insertMode: "APPEND", // How the video is inserted in the target element 'video-container'
            mirror: false, // Whether to mirror your local video or not
          });

          // Set the main video in the page to display our webcam and store our Publisher
          mainStreamManager.value = publisher_tmp
          publisher.value = publisher_tmp

          // --- 6) Publish your stream ---
          // session.publish(publisher)
          session.value.publish(publisher.value)
          getMedia()  // 세션이 만들어졌을때 미디어 불러옴
        })
        .catch((error) => {
          console.log("There was an error connecting to the session:", error.code, error.message);
        })
    })

    window.addEventListener("beforeunload", leaveSession)
  }

  function leaveSession(){
    // --- 7) Leave the session by calling 'disconnect' method over the Session object ---
    if(session.value) session.value.disconnect()

    // Empty all properties...
    session.value = undefined;
    mainStreamManager.value = undefined;
    publisher.value = undefined;
    subscribers.value = [];
    OV.value = undefined;

    // Remove beforeunload listener
    window.removeEventListener("beforeunload", leaveSession)

  }

  function updateMainVideoStreamManager(stream) {
    if (mainStreamManager.value === stream) return
    mainStreamManager.value = stream
  }

  /**
  * --------------------------------------------
  * GETTING A TOKEN FROM YOUR APPLICATION SERVER
  * --------------------------------------------
  */
  async function getToken(mySessionId) {
    const sessionId = await createSession(mySessionId);
    return await createToken(sessionId);
  }

  async function createSession(sessionId) {
    const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions', { customSessionId: sessionId, userNo: 53, endHour: 1, endMinute: 30, quota: 16, isPrivacy: false}, {
      headers: { 'Content-Type': 'application/json', },
    });
    return response.data; // The sessionId
  }

  async function createToken(sessionId) {
    const response = await axios.post(APPLICATION_SERVER_URL + 'api/sessions/' + sessionId + '/connections', {}, {
      headers: { 'Content-Type': 'application/json', },
    });
    return response.data; // The token
  }

  // 채팅창 구현을 위한 함수 제작
  ///////////////////////////
  function sendMessage(event) {
    event.preventDefault();
    if(inputMessage.value.trim()){
      // messages.value.push({username : myUserName.value, message : inputMessage.value})
      // 다른 참가원에게 메시지 전송하기
      session.value.signal({
        data: JSON.stringify({username: myUserName.value, message: inputMessage.value}), // 메시지 데이터를 문자열로 변환해서 전송
        type: 'chat' // 신호 타입을 'chat'으로 설정
      });
      inputMessage.value = '';
    }
  }


  // 캠, 오디오 등 기기와 관련된 함수
  // 카메라와 오디오를 가져옴.
  async function getMedia() {
    try {
      const devices = await navigator.mediaDevices.enumerateDevices();
      const cameras = devices.filter((device) => device.kind === 'videoinput');
      const audios = devices.filter((device) => device.kind === 'audioinput');
      // const audios = undefined

      const cameraSelect = document.querySelector('select[name="cameras"]');
      const audioSelect = document.querySelector('select[name="audios"]');

      // 카메라 및 오디오 선택기 요소가 존재하는지 확인
      // if (cameraSelect && audioSelect) {
      if (cameras) {
        cameras.forEach((camera) => {
          const option = document.createElement('option');
          option.value = camera.deviceId;
          option.text = camera.label;
          cameraSelect.appendChild(option);
        });
      } else {
        const notCamera = cameraSelect.querySelector('option:disabled');
        notCamera.innerText = '사용 가능한 카메라가 없습니다.'
        // console.error('Camera selector not found');
      }
      if(audios){
        audios.forEach((audio) => {
          const option = document.createElement('option');
          option.value = audio.deviceId;
          option.text = audio.label;
          audioSelect.appendChild(option);
        });
      } else {
        const notAudio = audioSelect.querySelector('option:disabled');
        notAudio.innerText = '사용 가능한 마이크가 없습니다.'
        // console.error('Audio selector not found');
      }
    } catch (error) {
      console.error('Error getting media devices:', error);
    }
  }


  // 음소거, 캠 활성화 버튼 작동
  function handleCameraBtn() {
    if (!publisher.value) return;
    // 카메라 상태 토글
    camerOff.value = !camerOff.value;
    const cameraActivate = document.getElementById('camera-activate')
    if(camerOff.value){   //카메라 비활성화상태
      cameraActivate.innerText = '카메라 활성화'
    }else{                //카메라 활성화상태
      cameraActivate.innerText = '카메라 비활성화'
    }

    // 카메라 작동 상태를 적용
    publisher.value.publishVideo(!camerOff.value);
  }

  function handleMuteBtn() {
    if (!publisher.value) return;

    // 음소거 상태 토글
    muted.value = !muted.value;
    const muteActivate = document.getElementById('mute-activate')
    if(muted.value){   //음소거 활성화상태
      muteActivate.innerText = '음소거 비활성화'
    }else{                //음소거 비활성화상태
      muteActivate.innerText = '음소거 활성화'
    }
    // 음소거 설정을 적용
    publisher.value.publishAudio(!muted.value);
  }

  // select태그에서 사용할 기기를 선택했을때
  async function handleCameraChange(event) {
    selectedCamera.value = event.target.value;
    await replaceCameraTrack(selectedCamera.value);
  }

  async function handleAudioChange(event) {
    selectedAudio.value = event.target.value;
    await replaceAudioTrack(selectedAudio.value);
  }

  async function replaceCameraTrack(deviceId) {
    if (!publisher.value) return;

    const newConstraints = {
        audio: false,
        video: {
            deviceId: { exact: deviceId },
        },
    };

    try {
        const newStream = await navigator.mediaDevices.getUserMedia(newConstraints);
        const newVideoTrack = newStream.getVideoTracks()[0];
        await publisher.value.replaceTrack(newVideoTrack);
    } catch (error) {
        console.error("Error replacing video track:", error);
    }
  }

  async function replaceAudioTrack(deviceId) {
    if (!publisher.value) return;

    const newConstraints = {
        audio: {
            deviceId: { exact: deviceId },
        },
        video: false,
    };

    try {
        const newStream = await navigator.mediaDevices.getUserMedia(newConstraints);
        const newAudioTrack = newStream.getAudioTracks()[0];
        await publisher.value.replaceTrack(newAudioTrack);
    } catch (error) {
        console.error("Error replacing audio track:", error);
    }
  }


</script>
