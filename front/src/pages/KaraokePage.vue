<template>
  <NavBar />
  <div id="main-container">
    <!-- session이 false일때! 즉, 방에 들어가지 않았을때 -->
    <div id="join" v-if="!session" class="q-pa-md q-gutter-md">
      <div id="img-div">
        <q-img
          src="src/assets/icon/logo1-removebg-preview.png"
          alt="로고 이미지"
          class="q-ma-md"
          :style="{ width: '40%' }"
        />
      </div>
      <div id="join-dialog" class="q-pa-md">
        <h1 class="q-mb-md text-h6">바로 지금 노래해방!</h1>
        <div>
          <p>
            <label class="q-mb-md">방 번호</label>
            <q-input v-model="mySessionId" outlined dense />
          </p>
          <p>
            <label class="q-mb-md">참가자 이름</label>
            <q-input v-model="myUserName" outlined dense />
          </p>
          <p class="q-mb-md">
            <q-btn @click="joinSession" color="primary" label="Join!" />
          </p>
        </div>
      </div>
    </div>

    <!-- session이 true일때! 즉, 방에 들어갔을 때 -->
    <div id="session" v-if="session" class="q-pa-md">
      <div id="session-header" class="q-mb-md">
        <q-toolbar-title style="font-size: 40px">{{
          mySessionId
        }}</q-toolbar-title>
        <q-btn @click="leaveSession" color="primary" label="Leave session" />
        <q-btn
          v-if="!filterApplied"
          @click="applyFilter"
          color="primary"
          label="Apply filter"
        />
        <q-btn
          v-if="filterApplied"
          @click="removeFilter"
          color="negative"
          label="Remove filter"
        />
        <div class="q-mb-md">
          <q-radio
            v-for="(filterLabel, filterValue) in filterOptions"
            :key="filterValue"
            v-model="selectedFilter"
            :val="filterValue"
            :label="filterLabel"
          />
        </div>

        <div>
          <div style="max-width: 300px; overflow-y: auto; max-height: 200px">
            <div
              class="q-mb-md"
              v-for="(slider, index) in sliders"
              :key="index"
            >
              <label>
                {{ slider.label }}:
                <q-slider
                  v-model="slider.value"
                  :min="slider.min"
                  :max="slider.max"
                  :step="slider.step"
                />
                {{ slider.value }}
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- 내 캠 -->
      <div id="main-video">
        <UserVideo :stream-manager="mainStreamManagerComputed" />
      </div>
      <!-- 모든 캠 -->
      <div id="video-container">
        <UserVideo
          :stream-manager="publisherComputed"
          @click="updateMainVideoStreamManager(publisher)"
        />
        <UserVideo
          v-for="sub in subscribersComputed"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          @click="updateMainVideoStreamManager(sub)"
        />
      </div>
      <!-- 방에 들어갔을 때 같이 보이게 될 채팅창 -->
      <!-- 나중에 <chat-winow />로 넘길수 있도록 해보자. -->
      <div id="chat-container" class="q-pa-md">
        <div id="chat-window" class="q-pa-md dark-bg">
          <ul id="chat-history" class="q-mb-md">
            <li
              v-for="(message, index) in messages"
              :key="index"
              class="message-item q-py-md dark-item-bg"
            >
              <strong class="message-username dark-text"
                >{{ message.username }}:</strong
              >
              {{ message.message }}
            </li>
          </ul>
        </div>
        <form id="chat-write" class="q-mt-md">
          <q-input
            type="text"
            placeholder="전달할 내용을 입력하세요."
            v-model="inputMessage"
            outlined
            dense
          />
          <q-btn @click="sendMessage" color="primary" label="전송" />
        </form>
      </div>

      <!-- 캠활성화, 음소거 버튼 -->
      <div class="q-mt-md">
        <q-btn
          id="camera-activate"
          @click="handleCameraBtn"
          color="primary"
          label="캠 비활성화"
        />
        <q-btn
          id="mute-activate"
          @click="handleMuteBtn"
          color="primary"
          label="음소거 활성화"
        />
      </div>

      <!-- 캠,오디오 선택 옵션 -->
      <div>
        <q-select
          v-model="selectedCamera"
          @update:model-value="handleCameraChange"
          label="사용할 카메라를 선택하세요"
          outlined
          :options="cameras"
          emit-value
          map-options
          option-value="deviceId"
          option-label="label"
        />
        <q-select
          v-model="selectedAudio"
          @update:model-value="handleAudioChange"
          label="사용할 마이크를 선택하세요"
          outlined
          :options="audios"
          emit-value
          map-options
          option-value="deviceId"
          option-label="label"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import axios from "axios";
import { OpenVidu } from "openvidu-browser";
import UserVideo from "@/components/karaoke/UserVideo.vue";
import NavBar from "@/layouts/NavBar.vue";

axios.defaults.headers.post["Content-Type"] = "application/json";

const APPLICATION_SERVER_URL =
  process.env.NODE_ENV === "production" ? "" : "http://localhost:8081/";

// OpenVidu 객체
const OV = ref(undefined);
const session = ref(undefined);
let mainStreamManager = ref(undefined);
const publisher = ref(undefined);
const subscribers = ref([]);

// Join form
const mySessionId = ref("SessionCrome");
const myUserName = ref("Participant" + Math.floor(Math.random() * 100));

// 필터를 위한 변수
const selectedFilter = ref("Audioecho");
const filterApplied = ref(false);
const filterOptions = {
  Audioecho: "Audio echo",
  Amplify: "Audio amplify",
  Pitch: "Pitch",
};

// 에코 관련 변수
const sliders = ref([
  { label: "Delay", value: 300, min: 100, max: 500, step: 10 },
  { label: "Intensity", value: 0.5, min: 0.1, max: 1, step: 0.1 },
  { label: "Feedback", value: 0.2, min: 0, max: 0.5, step: 0.01 },
]);

// 채팅창을 위한 변수
const inputMessage = ref("");
const messages = ref([]);

// 카메라 및 오디오 설정을 위한 변수
const muted = ref(false); // 기본은 음소거 비활성화
const camerOff = ref(false); // 기본 카메라 활성화
const selectedCamera = ref(""); // 카메라 변경시 사용할 변수
const selectedAudio = ref(""); // 오디오 변경시 사용할 변수
const cameras = ref([]);
const audios = ref([]);

// 다시그려내기 위해 computed 작성
const mainStreamManagerComputed = computed(() => mainStreamManager.value);
const publisherComputed = computed(() => publisher.value);
const subscribersComputed = computed(() => subscribers.value);

////////////////////////////////////////////////

// 역할을 정하는 라디오버튼 이벤트
const handleRadioBtnClick = (role) => {
  roles.forEach((r) => (r.checked = r === role));
};

// vue2에서의 methods 부분을 vue3화 시키기
function joinSession() {
  // --- 1) OpenVidu 객체 생성 ---
  OV.value = new OpenVidu();

  // --- 2) 세션 초기화 ---
  session.value = OV.value.initSession();

  // --- 3) 세션에서 이벤트 발생 시 동작 지정 ---
  // 새로운 스트림이 생성될 때마다...
  session.value.on("streamCreated", ({ stream }) => {
    const subscriber = session.value.subscribe(stream, undefined, {
      subscribeToAudio: true,
      subscribeToVideo: true,
    });
    subscribers.value.push(subscriber);
  });

  // 스트림이 파괴될 때마다...
  session.value.on("streamDestroyed", ({ stream }) => {
    const index = subscribers.value.indexOf(stream.streamManager, 0);
    if (index >= 0) {
      subscribers.value.splice(index, 1);
    }
  });

  // 비동기 예외가 발생할 때마다...
  session.value.on("exception", ({ exception }) => {
    console.warn(exception);
  });

  // 채팅 이벤트 수신 처리 함. session.on이 addEventListener 역할인 듯합니다.
  session.value.on("signal:chat", (event) => {
    // event.from.connectionId === session.value.connection.connectionId 이건 나와 보낸이가 같으면임
    const messageData = JSON.parse(event.data);
    if (event.from.connectionId === session.value.connection.connectionId) {
      messageData["username"] = "나";
    }
    messages.value.push(messageData);
  });

  // --- 4) 유효한 사용자 토큰으로 세션에 연결 ---
  // OpenVidu 배포에서 토큰 가져오기
  getToken(mySessionId.value).then((token) => {
    // 첫 번째 매개변수는 토큰입니다. 두 번째 매개변수는 모든 사용자가 'streamCreated' 이벤트에서 가져올 수 있는 것입니다.
    // 'streamCreated' (속성 Stream.connection.data) 및 닉네임으로 DOM에 추가됩니다.
    session.value
      .connect(token, { clientData: myUserName.value })
      .then(() => {
        // --- 5) 원하는 속성으로 자신의 카메라 스트림 가져오기 ---

        // 원하는 속성으로 초기화된 발행자를 만듭니다 (video-container'에 비디오가 삽입되지 않도록 OpenVidu에게 처리를 맡기지 않음).
        let publisher_tmp = OV.value.initPublisher(undefined, {
          audioSource: undefined, // 오디오의 소스. 정의되지 않으면 기본 마이크
          videoSource: undefined, // 비디오의 소스. 정의되지 않으면 기본 웹캠
          publishAudio: !muted.value, // 마이크 음소거 여부를 시작할지 여부
          publishVideo: !camerOff.value, // 비디오 활성화 여부를 시작할지 여부
          resolution: "1280x720", // 비디오의 해상도
          frameRate: 30, // 비디오의 프레임 속도
          insertMode: "APPEND", // 비디오가 대상 요소 'video-container'에 어떻게 삽입되는지
          mirror: false, // 로컬 비디오를 반전할지 여부
          isSubscribeToRemote: true,
        });

        // 페이지에서 주요 비디오를 설정하여 웹캠을 표시하고 발행자를 저장합니다.
        mainStreamManager.value = publisher_tmp;
        publisher.value = publisher_tmp;

        // --- 6) 스트림을 발행하고, 원격 스트림을 수신하려면 subscribeToRemote() 호출하기 ---
        publisher.value.subscribeToRemote();
        session.value.publish(publisher.value);
        getMedia(); // 세션이 만들어졌을 때 미디어를 불러옵니다.
      })
      .catch((error) => {
        console.log(
          "세션에 연결하는 중 오류가 발생했습니다:",
          error.code,
          error.message
        );
      });
  });

  window.addEventListener("beforeunload", leaveSession);
}

function leaveSession() {
  // --- 7) 'disconnect' 메서드를 세션 객체에서 호출하여 세션을 나갑니다. ---
  if (session.value) session.value.disconnect();

  // 모든 속성 비우기...
  session.value = undefined;
  mainStreamManager.value = undefined;
  publisher.value = undefined;
  subscribers.value = [];
  OV.value = undefined;

  // beforeunload 리스너 제거
  window.removeEventListener("beforeunload", leaveSession);
}

function updateMainVideoStreamManager(stream) {
  // 주요 비디오 스트림 매니저 업데이트
  if (mainStreamManager.value === stream) return;
  mainStreamManager.value = stream;
}

/**
 * --------------------------------------------
 * 애플리케이션 서버에서 토큰 가져오기
 * --------------------------------------------
 */
async function getToken(mySessionId) {
  const sessionId = await createSession(mySessionId);
  return await createToken(sessionId);
}

async function createSession(sessionId) {
  // 세션 생성
  const response = await axios.post(
    APPLICATION_SERVER_URL + "api/sessions",
    {
      customSessionId: sessionId,
      userNo: 53,
      endHour: 1,
      endMinute: 30,
      quota: 16,
      isPrivacy: false,
    },
    {
      headers: { "Content-Type": "application/json" },
    }
  );
  return response.data; // 세션 ID 반환
}

async function createToken(sessionId) {
  // 토큰 생성
  const response = await axios.post(
    APPLICATION_SERVER_URL + "api/sessions/" + sessionId + "/connections",
    {
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
}

// 채팅창 구현을 위한 함수 제작
///////////////////////////
function sendMessage(event) {
  event.preventDefault();
  if (inputMessage.value.trim()) {
    // 다른 참가원에게 메시지 전송하기
    session.value.signal({
      data: JSON.stringify({
        username: myUserName.value,
        message: inputMessage.value,
      }), // 메시지 데이터를 문자열로 변환해서 전송
      type: "chat", // 신호 타입을 'chat'으로 설정
    });
    inputMessage.value = "";
  }
}

// 캠, 오디오 등 기기와 관련된 함수
// 카메라와 오디오를 가져옴.
async function getMedia() {
  try {
    const devices = await navigator.mediaDevices.enumerateDevices();
    cameras.value = devices.filter((device) => device.kind === "videoinput");
    audios.value = devices.filter((device) => device.kind === "audioinput");

    // 첫번째 카메라와 오디오를 선택
    selectedCamera.value = cameras.value[0];
    selectedAudio.value = audios.value[0];
  } catch (error) {
    console.error("Error getting media devices:", error);
  }
}

// 음소거, 캠 활성화 버튼 작동
function handleCameraBtn() {
  if (!publisher.value) return;
  // 카메라 상태 토글
  camerOff.value = !camerOff.value;
  const cameraActivate = document.getElementById("camera-activate");
  if (camerOff.value) {
    //카메라 비활성화상태
    cameraActivate.innerText = "카메라 활성화";
  } else {
    //카메라 활성화상태
    cameraActivate.innerText = "카메라 비활성화";
  }

  // 카메라 작동 상태를 적용
  publisher.value.publishVideo(!camerOff.value);
}

function handleMuteBtn() {
  if (!publisher.value) return;

  // 음소거 상태 토글
  muted.value = !muted.value;
  const muteActivate = document.getElementById("mute-activate");
  if (muted.value) {
    //음소거 활성화상태
    muteActivate.innerText = "음소거 비활성화";
  } else {
    //음소거 비활성화상태
    muteActivate.innerText = "음소거 활성화";
  }
  // 음소거 설정을 적용
  publisher.value.publishAudio(!muted.value);
}

// select태그에서 사용할 기기를 선택했을때
async function handleCameraChange(event) {
  selectedCamera.value = event;
  await replaceCameraTrack(selectedCamera.value);
}

async function handleAudioChange(event) {
  selectedAudio.value = event;
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

// 필터를 적용해주는 함수
function applyFilter() {
  const filter = { type: "GStreamerFilter", options: {} };
  // const type = ref(document.querySelector("input[name=filter]:checked").value);
  console.log(selectedFilter.value);

  switch (selectedFilter.value) {
    case "Audioecho":
      filter.type = "GStreamerFilter";
      filter.options = {
        command: `audioecho delay=${sliders.value[0].value} intensity=${sliders.value[1].value} feedback=${sliders.value[2].value}`,
      };
      break;
    case "Amplify":
      filter.type = "GStreamerFilter";
      filter.options = { command: "audioamplify amplification=1.7" };
      break;
    case "Pitch":
      filter.type = "GStreamerFilter";
      filter.options = { command: "pitch pitch=1.2" };
      break;
  }

  // 필터를 적용해주는 부분
  publisher.value.stream.applyFilter(filter.type, filter.options);
  filterApplied.value = true;
}

// 필터를 지우는 함수
const removeFilter = () => {
  publisher.value.stream.removeFilter();
  filterApplied.value = false;
};
// 필터 함수 종료
</script>

<style scoped>
#chat-window {
  background-color: #2c3e50; /* 어두운 배경색 */
  border-radius: 8px;
  color: #ecf0f1; /* 전체 텍스트 색상 */
}

.dark-bg {
  background-color: #2c3e50 !important; /* !important로 우선순위 부여 */
}

#chat-history {
  list-style-type: none;
  padding: 0;
}

.message-item {
  background-color: #34495e; /* 어두운 메시지 배경색 */
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 12px;
  margin-bottom: 10px;
}

.dark-item-bg {
  background-color: #34495e !important; /* !important로 우선순위 부여 */
}

.message-username {
  font-weight: bold;
  color: #3498db; /* 사용자 이름 색상 */
}

.dark-text {
  color: #3498db !important; /* !important로 우선순위 부여 */
}
</style>
