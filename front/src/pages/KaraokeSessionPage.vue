<template>
  <q-layout view="lHh Lpr lFf" v-if="store.session">
    <!-- 상단 AppBar -->
    <q-header elevated class="custom-header">
      <div
        id="session-header"
        class="q-mb-md"
        style="
          display: flex;
          align-items: center;
          justify-content: space-between;
        "
      >
        <q-toolbar-title style="font-size: 40px"
          >{{ pref.app.kor.karaoke.list.sessionId }} :
          {{ store.sessionName }}</q-toolbar-title
        >
        <div style="display: flex">
          <q-btn
            @click="openInviteModal"
            color="black"
            :label="pref.app.kor.karaoke.session.invite"
          />
          <q-btn
            @click="openModal"
            color="positive"
            :label="pref.app.kor.karaoke.session.setting"
          />
          <q-btn
            @click="leaveSession"
            color="negative"
            :label="pref.app.kor.karaoke.session.leave"
          />
        </div>
      </div>
    </q-header>

    <!-- 메인 컨텐츠 영역 -->
    <q-page-container class="q-pa-md">
      <!-- 메인 캠 -->
      <div
        id="main-video"
        style="display: flex; flex-direction: row; overflow-x: auto"
      >
        <UserVideo :stream-manager="mainStreamManagerComputed" />
        <div style="display: flex; flex-direction: column">
          <q-btn
            @click="startSong()"
            color="positive"
            :label="pref.app.kor.karaoke.session.start"
          />
          <q-btn
            @click="stopSong()"
            color="negative"
            :label="pref.app.kor.karaoke.session.stop"
          />
          <q-btn @click="finishSong()" color="primary" label="종료" />
        </div>
        <div>
          <normal-mode ref="normalModeRef" v-if="!songMode" :songData="song"/>
          <perfect-score ref="perfectScoreRef" v-if="songMode" :songData="song"/>
        </div>
      </div>

      <!-- 모든 캠 -->
      <div id="video-container" class="responsive-container">
        <UserVideo
          :stream-manager="publisherComputed"
          class="user-video"
          @click="updateMainVideoStreamManager(store.publisher)"
        />
        <UserVideo
          v-for="sub in subscribersComputed"
          :key="sub.stream.connection.connectionId"
          :stream-manager="sub"
          class="user-video"
          @click="updateMainVideoStreamManager(sub)"
        />
      </div>
    </q-page-container>

    <!-- 하단 메뉴바 -->
    <q-footer>
      <q-tabs align="justify" active-color="positive" indicator-color="primary">
        <q-tab
          name="karaoke-chat"
          :label="pref.app.kor.karaoke.session.chatting"
          @click="toggleModal('karaoke-chat')"
        />

        <q-tab
          name="input-controller"
          :label="pref.app.kor.karaoke.session.input"
          @click="toggleModal('input-controller')"
        />

        <q-tab
          name="reserve-song"
          :label="pref.app.kor.karaoke.session.reserve"
          @click="toggleModal('reserve-song')"
        />

        <q-tab
          name="reserve-list"
          :label="pref.app.kor.karaoke.session.reserveList"
          @click="toggleModal('reserve-list')"
        />
      </q-tabs>
    </q-footer>
  </q-layout>

  <karaoke-chat />
  <input-controller />
  <recording-video />
  <reserve-modal />
  <reserve-list />

  <update-modal />
  <invite-modal  :sessionName="store.sessionName"/>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import { useNotificationStore } from "@/stores/notificationStore.js";
import pref from "@/js/config/preference.js";
import axios from "axios";

import UserVideo from "@/components/karaoke/video/UserVideo.vue";
import KaraokeChat from "@/components/karaoke/session/KaraokeChat.vue";
import InputController from "@/components/karaoke/session/InputController.vue";
import UpdateModal from "@/components/karaoke/session/UpdateModal.vue";
import InviteModal from "@/components/karaoke/session/InviteModal.vue";
import ReserveModal from "@/components/karaoke/song/ReserveModal.vue";
import ReserveList from "@/components/karaoke/song/ReserveList.vue";
import NormalMode from "@/components/karaoke/NormalMode.vue";
import PerfectScore from "@/components/karaoke/PerfectScore.vue";

// store 사용
const store = useKaraokeStore();
const router = useRouter();
const songMode = ref(true);

const fileUrl = ref(undefined);
const recordingId = ref(undefined);
const perfectScoreRef = ref(null);
const normalModeRef = ref(null);

// 다시그려내기 위해 computed 작성
const mainStreamManagerComputed = computed(() => store.mainStreamManager);
const publisherComputed = computed(() => store.publisher);
const subscribersComputed = computed(() => store.subscribers);

//notificationStore 사용
const notificationStore = useNotificationStore();

const openModal = () => {
  store.updateModal = true;
};

const openInviteModal = () => {
  notificationStore.inviteModal = true;
  console.log("click : notificationStore.inviteModal : ", notificationStore.inviteModal)
}

async function leaveSession() {
  await store.leaveSession();
  router.push("/karaoke");
}

function updateMainVideoStreamManager(stream) {
  // 주요 비디오 스트림 매니저 업데이트
  if (store.mainStreamManager === stream) return;
  store.mainStreamManager = stream;
}

const toggleModal = (modalName) => {
  store.toggleModals[modalName] = true;
};

function startSong() {
  if(songMode.value) {
    perfectScoreRef.value.choose();
    perfectScoreRef.value.play();
  } else {
    normalModeRef.value.choose();
    normalModeRef.value.play();
  }

  removeReserve()
    .then(() => {
      startRecording();
    })
    .catch((error) => {
      console.log("removeReserve 실패", error);
    });
}

function stopSong() {
  if(songMode.value) {
    perfectScoreRef.value.stop();
  } else {
    normalModeRef.value.stop();
  }

  stopRecording()
    .then(() => {
      removeRecording();
    })
    .catch((error) => {
      console.log("stopRecording 실패", error);
    });
}

function finishSong() {
  stopRecording()
    .then(() => {
      uploadRecording();
    })
    .catch((error) => {
      console.log("stopRecording 실패", error);
    });
}

function removeReserve() {
  return axios
    .post(
      store.APPLICATION_SERVER_URL + "/song/start",
      {
        sessionName: store.sessionName,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then((res) => {
      console.log(res.data);
    });
}

function startRecording() {
  axios
    .post(
      store.APPLICATION_SERVER_URL + "/karaoke/recording/start",
      {
        sessionName: store.sessionName,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then((res) => {
      console.log(res.data);
      recordingId.value = res.data.id;
    });
}

function stopRecording() {
  return axios
    .post(
      store.APPLICATION_SERVER_URL + "/karaoke/recording/stop",
      {
        recordingId: recordingId.value,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then((res) => {
      console.log(res.data);
      fileUrl.value = res.data.url;
    });
}

function removeRecording() {
  axios
    .post(
      store.APPLICATION_SERVER_URL + "/karaoke/recording/delete",
      {
        recordingId: recordingId.value,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then(() => {
      fileUrl.value = undefined;
      recordingId.value = undefined;
    });
}

function uploadRecording() {
  console.log(fileUrl.value);
  axios
    .post(store.APPLICATION_SERVER_URL + "/karaoke/file/upload", {
      fileUrl: fileUrl.value,
    })
    .then((res) => {
      console.log(res.data);
      removeRecording();
    })
    .catch((error) => {
      console.error("uploadRecording 실패", error);
    });
}

const song = {
      title: "응급실",
      singer: "Izi",
      author: "nobody",
      url: "https://a705.s3.ap-northeast-2.amazonaws.com/izif6b7b-3504-4ef4-ae79-3063301967d0.mp3",
      score: `t68.7 o3 l8
  <b-'후'>
  g4'회'r4 b-'하'a-16'고\t'g16'있'&gf'어'&
  fd'요\n'&d4r4. <g'우'>
  g'리\t'r4a-'다'g16'투'f16'던\t'&fe-'그'
  >cc'날\n'&c4r4.<b-'괜'
  >c4'한\t'r c'자'd'존'<b-'심'g'때'a-16'문'b-16'에\n'&
  b-4r>d-'끝'c'내'<g'자'f'고\t'g16'말'a-16'을\n'&
  a-4r4a-'해'b-16'버'>c16'린'&c<b-'거'&
  b-f'야\n'&f4r4.<b-'금'>

  g4'방\t'r4b-'볼'a-16'줄\t'g16'알'&gf'았'&
  fd'어\n'&d4r2
  g4'날\t'r4a-'찾'g16'길\t'f16'바'&fe-'랬'
  >cc'어\n'&c4r4.<b-'허'
  >c4'나\t'r c'며'd'칠'<b-'이\t'g'지'a-16'나'b-16'도\n'&
  b-4 r>d-'아'c'무\t'<g'소'f'식'g'조'
  b-'차\t'a-r g16'없'f16 f4'어\n' r4

  r e-'항'f'상\t'e-16'내'f16'게\n'&fe- r. <b-16'너'>
  a-'무\t'g'잘'f'해'e-'줘'&e-<b-'서\n'&b-4>
  r4 l16f'쉽'gf'게\t'f'생'& l8 f f'각'g'했'a-16'나'a-16'봐\n'&
  a-g&g4r2
  r e-'이'f'젠\t'e-16'알'f16'아\n'&fe- r. <b-16'내\t'>
  g'고'b-'집'b-'때'>c'문'&c<g'에\n'&g4
  b-'힘'a-'들'g'었'a-'던\t'&a-4 >g'너'g'를\n'&
  gf&f2r4

  <b-'이\t'>l16e-'바'e-'보'&e-8f'야\n'f'진'&f8g'짜\t'g'아'&g<b-b-'니'>c'야\n'&
  c<b-8.&b-4r2
  l8>c'아'e-'직'e-'도\t'f16'나'e-16'를\n'&e-4r a-'그'&
  a-g'렇'f16'게\t'e-16e-'몰'&e-<b-'라\n'&b-4

  >c'너'<b16'를\t'>c16'가'&ce-'진\t'f.'사'e-16e-4'랑\n'
  <b-'나'a16'밖'b-16'엔\t'&b->d'없'f.'는'e-16e-4'데\n'
  r2c'제'd16'발\t'e-16'나'&e-16f.'를\n'
  g.'떠'f16f4'나'e-.'가'd16d.'지'e-16e-2'마\n'r2

  r< e-'언'f'제'e-16'라'f16'도\n'&fe- r. <b-16'내'>
  a-'편'g'이\t'f'돼'e-'준\t'&e-<b-'너\n'&b-4>
  r4 l16f'고'gf'마'f'운'& l8 f f'줄\t'g'모'a-16'르'a-16'고\n'&
  a-g&g4r2
  r e-'철'f'없'e-16'이\t'f16'나\n'&fe- r. r16
  g'멋'b-'대'b-'로\t'>c'한'&c<g'거\n'&g4
  b-'용'a-'서'g'할\t'a-'수\t'&a-4 >g'없'g'니\n'&
  gf&f2r4

  <b-'이\t'>l16e-'바'e-'보'&e-8f'야\n'f'진'&f8g'짜\t'g'아'&g<b-b-'니'>c'야\n'&
  c<b-8.&b-4r2
  l8>c'아'e-'직'e-'도\t'f16'나'e-16'를\n'&e-4r a-'그'&
  a-g'렇'f16'게\t'e-16e-'몰'&e-<b-'라\n'&b-4

  >c'너'<b-16'를\t'>c16'가'&ce-'진\t'f.'사'e-16e-4'랑\n'
  <b-'나'a-16'밖'b-16'엔\t'&b->d'없'f.'는'e-16e-4'데\n'
  r2c'제'd16'발\t'e-16'떠'&e-f'나'

  g4'가'a-'지'f'마\n'&f4r4
  <b-'너\t'>e-16'하'e-16'나'&e- f16'만\n'f16'사'&fg16'랑'g16'하'&g16<b-16b-16'는'>c16'데\n'&
  c16<b-.&b-4r2
  >c'이'e-'대'e-'로\t'f16'나'e-16'를\n'&e-4r a-'두'&
  l16a-gg'고\t'f f'가'e-e-'지'g'마\n'&gf+g8&g4
  l8 c'나'<b16'를\t'>c16'버'&cd'리'f.'지'e-16e-4'마\n'
  <b-'그'a16'냥\t'b-16'날\t'&b-16>b-'안'a-16 a-'아'g16g16'줘\n'&g4
  r2 c'다'd16'시\t'e-16'사'&e-16f.'랑'
  g.'하'f16 f4'게\n' e-.'돌'd16 d4'아'
  f'와\n'e-&e-4`,
  length: 220,
  prelude: 13200,
};
</script>

<style scoped>
.custom-header {
  height: 50px;
}
.user-video {
  cursor: pointer;
}

#video-container {
  display: flex;
  flex-direction: row;
  overflow-x: auto;
}

/* 추가한 클래스로 반응형 스타일을 지정합니다. */
.responsive-container {
  flex-wrap: nowrap; /* 자식 요소들이 한 줄에 나오도록 설정 */
}

/* 미디어 쿼리를 사용하여 페이지 크기에 따라 스타일을 동적으로 조절합니다. */
@media (max-width: 768px) {
  .responsive-container {
    flex-wrap: wrap; /* 페이지 크기가 작을 때는 요소들이 여러 줄에 나오도록 설정 */
  }
}
</style>
