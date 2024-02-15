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
    <q-btn @click="changeSongMode()" color="primary" label="모드 바꾸기" />
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
          <div style="width: 300px">
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
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();

const store = useKaraokeStore();

const fileUrl = ref(undefined);
const recordingId = ref(undefined);
const perfectScoreRef = ref(null);
const normalModeRef = ref(null);

const selectedOption = ref("비공개");
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
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    )
    .then((res) => {
      console.log(res.data);
      store.session.signal({ type: "reserve" });
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
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
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
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
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
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
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
    .post(
      store.APPLICATION_SERVER_URL + "/karaoke/file/upload",
      {
        fileUrl: fileUrl.value,
      },
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      }
    )
    .then((res) => {
      console.log(res.data);
      videoUrl.value = res.data;
      removeRecording();
    })
    .catch((error) => {
      console.error("uploadRecording 실패", error);
    });
}

function changeSongMode() {
  store.session.signal({
    data: JSON.stringify({
      songMode: !store.songMode,
    }),
    type: "songMode",
  });
}

function singing() {
  store.session.signal({
    data: JSON.stringify({
      singUser: singUser.value,
      singing: !store.singing,
    }),
    type: "sing",
  });
}

watch(
  () => store.songMode,
  (newSongMode) => {
    console.log("SongMode이 변경됨:", newSongMode);
  }
);

watch(
  () => store.singing,
  (newSinging) => {
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
        status: privacyOptions.indexOf(selectedOption.value),
        videoUrl: videoUrl.value,
      },
      {
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          uuid: getCookie("uuid"),
          "Content-Type": "application/json",
        },
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

watch(
  () => store.reservedSongsLength,
  () => {
    if(store.reservedSongs.length == 0) {
    } else {
      axios
        .get(
        store.APPLICATION_SERVER_URL + "/song/songInfo/" + store.reservedSongs[0].songId,
        {
          headers: {
            Authorization: getCookie("Authorization"),
            refreshToken: getCookie("refreshToken"),
            "Content-Type": "application/json",
          },
        }
      )
      .then((res) => {
        if(JSON.parse(JSON.stringify(res.data)) == "") {
          console.log("데이터 없는 노래 예약")
        } else {
          song.value =  JSON.parse(JSON.stringify(res.data))
        }
      })
      .catch((error) => {
        console.error("songInfo 불러오기 실패"+error);
      });
    }

  }
);

const song = ref({});

</script>

<style scoped>
.modal-card {
  max-width: 768px;
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
