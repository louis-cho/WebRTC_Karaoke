<template>
  <div style="display: flex; flex-direction: column">
    <q-btn
      v-if="!store.singing"
      @click="startSong()"
      color="positive"
      :label="pref.app.kor.karaoke.session.start"
    />
    <q-btn
      v-if="store.singing"
      @click="stopSong()"
      color="negative"
      :label="pref.app.kor.karaoke.session.stop"
    />
    <q-btn @click="finishSong()" color="primary" label="종료" />
    <q-btn
      @click="store.songMode = !store.songMode"
      color="primary"
      label="모드 바꾸기"
    />
  </div>

  <div>
    <normal-mode ref="normalModeRef" v-if="!store.songMode" :songData="song" />
    <perfect-score
      ref="perfectScoreRef"
      v-if="store.songMode"
      :songData="song"
    />
  </div>

  <q-dialog v-model="modalVisible" no-backdrop-dismiss>
    <q-card class="modal-card">
      <q-card-section class="modal-header">
        <div class="user-info">
          <!-- 프로필 이미지 가져오기... -->
          <q-avatar class="img-container" />
          <q-item-section>
            <!-- 닉네임 가져오기 -->
            <q-item-label>{{ store.userName }}</q-item-label>
          </q-item-section>
        </div>
      </q-card-section>
      <hr />

      <q-card-section class="display-flex-row">
        <div class="video-container">
          <!-- 영상 가져오기 -->
          <video controls width="100%" height="100%">
            <source :src="videoUrl" type="video/mp4" />
          </video>
        </div>

        <div class="display-flex-column">
          <div>
            <!-- <input class="caption-input" type="text" placeholder="문구 입력..."> -->
            <textarea
              v-model="postContent"
              class="caption-input"
              placeholder="문구 입력..."
            ></textarea>
          </div>
          <div>
            <!-- 공개범위 토글 -->
            <q-select
              v-model="selectedOption"
              :options="privacyOptions"
              emit-value
              map-options
            />
          </div>
        </div>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn label="작성" color="primary" @click="submitPost" />
        <q-btn label="취소" @click="closeModal" />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, watch } from "vue";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
import pref from "@/js/config/preference.js";
import axios from "axios";
import NormalMode from "@/components/karaoke/NormalMode.vue";
import PerfectScore from "@/components/karaoke/PerfectScore.vue";

const store = useKaraokeStore();

const fileUrl = ref(undefined);
const recordingId = ref(undefined);
const perfectScoreRef = ref(null);
const normalModeRef = ref(null);

const selectedOption = ref("공개범위");
const videoUrl = ref("");
const privacyOptions = ["전체공개", "친구공개", "비공개"];
const modalVisible = ref(false);
const postContent = ref("");
const singUser = ref(undefined);
const songId = ref(undefined);

function startSong() {
  removeReserve()
    .then(() => {
      singing();
      startRecording();
    })
    .catch((error) => {
      console.log("removeReserve 실패", error);
    });
}

function stopSong() {
  singing();

  stopRecording()
    .then(() => {
      removeRecording();
    })
    .catch((error) => {
      console.log("stopRecording 실패", error);
    });
}

function finishSong() {
  singing();

  stopRecording()
    .then(async () => {
      await uploadRecording();
      modalVisible.value = true;
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
      const parts = res.data.split("&");

      if (parts.length === 4) {
        const [userName, id, title, singer] = parts;
        singUser.value = userName;
        songId.value = id;
      }
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
    })
    .catch((error) => {
      console.error(error);
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
    })
    .catch((error) => {
      console.error(error);
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
      videoUrl.value = res.data;
      removeRecording();
    })
    .catch((error) => {
      console.error("uploadRecording 실패", error);
    });
}

function singing() {
  store.session.signal({
    data: JSON.stringify({
      singUser: singUser.value,
      singing: !store.singing,
      songMode: store.songMode,
    }), // 메시지 데이터를 문자열로 변환해서 전송
    type: "sing", // 신호 타입을 'chat'으로 설정
  });
}

watch(
  () => store.songMode,
  (newSongMode, oldSongMode) => {
    console.log("SongMode이 변경됨:", newSongMode);
  }
);

watch(
  () => store.singing,
  (newSinging, oldSinging) => {
    console.log("Singing이 변경됨:", newSinging);

    if (newSinging) {
      if (store.songMode) {
        perfectScoreRef.value.choose();
        perfectScoreRef.value.play();
      } else {
        normalModeRef.value.choose();
        normalModeRef.value.play();
      }
    } else {
      if (store.songMode) {
        perfectScoreRef.value.stop();
      } else {
        normalModeRef.value.stop();
      }
    }
  }
);

const submitPost = () => {
  axios
    .post(
      store.APPLICATION_SERVER_URL + "/feed/create",
      {
        content: postContent.value,
        songId: songId.value,
        videoUrl: videoUrl.value,
      },
      {
        headers: { "Content-Type": "application/json" },
      }
    )
    .then((res) => {
      console.log(res.data);
      recordingId.value = res.data.id;
    });

  console.log("게시글 작성 완료:", postContent.value);
  closeModal();
};

const closeModal = () => {
  modalVisible.value = false;
};

const song = {
  title: "응급실",
  singer: "Izi",
  author: "신동우",
  url: "https://a705.s3.ap-northeast-2.amazonaws.com/izif6b7b-3504-4ef4-ae79-3063301967d0.mp3",
  score: `t70 o3 l8
  <b-'후'>
  g4'회'r4b-'하'a-16'고\t'g16'있'&gf'어'&
  fd'요\n'&d4r4.<g'우'>
  g4'리\t'r4a-'다'g16'투'f16'던\t'&fe-'그\t'
  >cc'날\n'&c4r4.<b-'괜'
  >c4'한\t'rc'자'd'존'<b-'심\t'g'때'a-16'문'b-16'에\n'&
  b-4r>d-'끝'c'내'<g'자'f'고\t'g16'말'a-16'을\n'&
  a-4r4a-'해'b-16'버'>c16'린'&c<b-'거'&
  b-f'야\n'&f4r4.<b-'금'>

  g4'방\t'r4b-'볼\t'a-16'줄\t'g16'알'&gf'았'&
  fd'어\n'&d4r2
  g4'날\t'r4a-'찾'g16'길\t'f16'바'&fe-'랬'
  >cc'어\n'&c4r4.<b-'허'
  >c4'나\t'rc'며'd'칠'<b-'이\t'g'지'a-16'나'b-16'도\n'&
  b-4r>d-'아'c'무\t'<g'소'f'식'g'조'
  b-'차\t'a-rg16'없'f16f4'어\n'r4

  r e-'항'f'상\t'e-16'내'f16'게\n'&fe- r. <b-16'너'>
  a-'무\t'g'잘'f'해'e-'줘'&e-<b-'서\n'&b-4>
  r4 l16f'쉽'gf'게\t'f'생'& l8 f f'각'g'했'a-16'나'a-16'봐\n'&
  a-g&g4r2
  r e-'이'f'젠\t'e-16'알'f16'아\n'&fe- r. <b-16'내\t'>
  g'고'b-'집\t'b-'때'>c'문'&c<g'에\n'&g4
  b-'힘'a-'들'g'었'a-'던\t'&a-4 >g'너'g'를\n'&
  gf&f2r4

  <b-'이\t'>l16e-'바'e-'보'&e-8f'야\n'f'진'&f8g'짜\t'g'아'&g<b-b-'니'>c'야\n'&
  c<b-8.&b-4r2
  l8>c'아'e-'직'e-'도\t'f16'나'e-16'를\n'&e-4r a-'그'&
  a-g'렇'f16'게\t'e-16e-'몰'&e-<b-'라\n'&b-4

  >c'너'<b16'를\t'>c16'가'&ce-'진\t'f.'사'e-16e-4'랑\n'
  <b-'나'a16'밖'b-16'엔\t'&b->d'없'f.'는'e-16e-4'데\n'
  r2c'제'd16'발\t'e-16'나'&e-16f.'를\n'
  g.'떠'f16f4'나'e-.'가'd16d.'지'e-16e-2'마\n'r2r1

  r< e-'언'f'제'e-16'라'f16'도\n'&fe- r. <b-16'내\t'>
  a-'편'g'이\t'f'돼'e-'준\t'&e-<b-'너\n'&b-4>
  r4 l16f'고'gf'마'f'운\t'& l8 f f'줄\t'g'모'a-16'르'a-16'고\n'&
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
  g.'하'f16 f4'게\n'e-.'돌'd16 d4'아'
  f'와\n'e-&e-4`,
  length: 220,
  prelude: 13200,
};
</script>

<style scoped>
.modal-card {
  max-width: 400px;
  margin: 20px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
}

.upload-label {
  color: royalblue;
}

.display-flex-row {
  display: flex;
  flex-direction: row;
}

.display-flex-column {
  display: flex;
  flex-direction: column;
}

.video-container {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 0px;
}

.caption-input {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.privacy-dropdown {
  padding-left: 0;
  padding-right: 0;
}

.img-container {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-image: url("@/assets/img/capture.png");
  background-size: cover;
  background-position: center;
  margin-right: 10px;
}

.caption-input {
  width: 100%;
  padding: 8px;
  margin-bottom: 0px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: vertical; /* 세로로 길게 조절 가능하도록 함 */
  min-height: 80px; /* 최소 높이 설정 */
}
</style>
