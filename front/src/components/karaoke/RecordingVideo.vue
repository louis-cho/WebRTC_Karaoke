<template>
  <div id="recording-btns">
    <!-- Start Recording Section -->
    <div v-if="!checkBtnsRecordings">
      <q-btn
        class="q-mb-md"
        @click="startRecording"
        color="primary"
        label="Start Recording"
      />

      <div class="q-gutter-md">
        <q-form>
          <!-- Radio Options -->
          <q-option-group
            v-model="outputMode"
            type="radio"
            inline
            :options="[
              { label: 'COMPOSED', value: 'COMPOSED' },
              { label: 'INDIVIDUAL', value: 'INDIVIDUAL' },
            ]"
          />
        </q-form>

        <q-form>
          <!-- Checkbox Options -->
          <q-checkbox v-model="hasAudio" label="Has Audio" />
          <q-checkbox v-model="hasVideo" label="Has Video" />
        </q-form>
      </div>
    </div>

    <!-- Stop Recording Section -->
    <div v-if="checkBtnsRecordings">
      <q-btn
        class="btn-md"
        @click="stopRecording"
        label="Stop Recording"
        color="primary"
        :disable="!checkBtnsRecordings"
      />
    </div>

    <!-- Other Buttons Section -->
    <div v-if="forceRecordingId">
      <q-btn
        class="btn-md"
        @click="getRecording"
        label="Get Recording"
        color="primary"
      />

      <q-btn
        class="btn-md"
        @click="deleteRecording"
        label="Delete Recording"
        color="primary"
      />
    </div>

    <!-- List Recordings Section -->
    <div>
      <div class="btns">
        <q-btn
          class="btn-md"
          @click="listRecordings"
          label="List Recordings"
          color="primary"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import axios from "axios";
import pref from "@/js/config/preference.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

const store = useKaraokeStore();

const outputMode = ref("COMPOSED");
const hasAudio = ref(true);
const hasVideo = ref(true);
const forceRecordingId = ref(undefined);
const checkBtnsRecordings = ref(false);

function startRecording() {
  axios
    .post(store.APPLICATION_SERVER_URL + "api/v1/karaoke/recording/start", {
      sessionId: store.session.sessionId,
      outputMode: outputMode.value,
      hasAudio: hasAudio.value,
      hasVideo: hasVideo.value,
    })
    .then((res) => {
      console.log(res.data);
      forceRecordingId.value = res.data.id;
      checkBtnsRecordings.value = true;
    })
    .catch((error) => {
      console.error("Start recording WRONG", error);
    });
}

function stopRecording() {
  axios
    .post(store.APPLICATION_SERVER_URL + "api/v1/karaoke/recording/stop", {
      recordingId: forceRecordingId.value,
    })
    .then((res) => {
      console.log(res.data);
      checkBtnsRecordings.value = false;
    })
    .catch((error) => {
      console.error("Stop recording WRONG", error);
    });
}

function deleteRecording() {
  axios
    .post(store.APPLICATION_SERVER_URL + "api/v1/karaoke/recording/delete", {
      recordingId: forceRecordingId.value,
    })
    .then(() => {
      console.log("DELETE ok");
    })
    .catch((error) => {
      console.error("Delete recording WRONG\n", error);
    });
}

function getRecording() {
  axios
    .get(
      store.APPLICATION_SERVER_URL +
        "api/v1/karaoke/recording/get/" +
        forceRecordingId.value,
      {}
    )
    .then((res) => {
      console.log(res.data);
    })
    .catch((error) => {
      console.error("Get recording WRONG", error);
    });
}

function listRecordings() {
  axios
    .get(store.APPLICATION_SERVER_URL + "api/v1/karaoke/recording/list", {})
    .then((res) => {
      console.log(res.data);
    })
    .catch((error) => {
      console.error("List recordings WRONG", error);
    });
}
</script>

<style scoped></style>
