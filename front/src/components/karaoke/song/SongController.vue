<template>
  <div style="display: flex; flex-direction: row">
    <q-btn
      v-if="
        !store.singing &&
        store.reservedSongsLength > 0 &&
        store.singUser == store.userName
      "
      @click="startSong()"
      color="black"
      :label="pref.app.kor.karaoke.session.start"
    />
    <q-btn
      v-if="store.singing && store.singUser == store.userName"
      @click="stopSong()"
      color="negative"
      :label="pref.app.kor.karaoke.session.stop"
    />
    <q-btn
      v-if="store.singing && store.singUser == store.userName"
      @click="finishSong()"
      color="black"
      label="그만부르기"
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
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();

const store = useKaraokeStore();

const fileUrl = ref(undefined);
const recordingId = ref(undefined);
const selectedOption = ref("비공개");
const videoUrl = ref("");
const privacyOptions = ["전체공개", "친구공개", "비공개"];
const modalVisible = ref(false);
const postContent = ref("");
const singUser = ref(undefined);
const songId = ref(undefined);
const timerId = ref(undefined);

function startSong() {
  removeReserve()
    .then(() => {
      if (store.song == null) {
        alert("노래 데이터가 아직 없어요,,,");
        return;
      }
      singing();
      startRecording();
      timerId.value = setTimeout(finishSong, store.song.length * 1000);
    })
    .catch((error) => {});
}

function stopSong() {
  singing();
  clearTimeout(timerId.value);

  stopRecording()
    .then(() => {
      removeRecording();
    })
    .catch((error) => {});
}

function finishSong() {
  singing();
  clearTimeout(timerId.value);

  stopRecording()
    .then(async () => {
      await uploadRecording();
      modalVisible.value = true;
    })
    .catch((error) => {});
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
      recordingId.value = res.data.id;
    })
    .catch((error) => {});
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
    .catch((error) => {});
}

function uploadRecording() {
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
      videoUrl.value = res.data;
      removeRecording();
    })
    .catch((error) => {});
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
  (newSongMode) => {}
);

watch(
  () => store.singUserOut,
  () => {
    if (store.singUserOut) {
      stopSong();
      store.singUserOut = false;
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
      recordingId.value = res.data.id;
    });

  closeModal();
};

const closeModal = () => {
  modalVisible.value = false;
};
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
