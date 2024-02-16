<template>
  <q-dialog v-model="store.toggleModals['input-controller']">
    <q-card>
      <q-card-section style="max-height: 400px; overflow-y: hidden">
        <q-layout style="min-width: 256px">
          <q-header
            class="bg-transparent"
            style="border-bottom: 1px solid #ddd"
          >
            <q-card-section
              style="display: flex; justify-content: space-between"
            >
              <q-toolbar-title style="color: black">입력 설정</q-toolbar-title>
              <q-btn @click="closeModal" color="negative" label="닫기" />
            </q-card-section>
          </q-header>

          <q-page-container>
            <q-page>
              <q-card-section>
                <div>
                  에코 : {{ selectedIndex + 1 }}
                  <q-slider
                    v-model="selectedIndex"
                    :min="0"
                    :max="sliders.length - 1"
                    :step="1"
                  ></q-slider>
                </div>

                <div style="display: flex; justify-content: flex-end">
                  <q-btn
                    v-if="!filterApplied"
                    @click="applyFilter"
                    color="primary"
                    class="q-mt-md"
                    :label="pref.app.kor.karaoke.session.applyEcho"
                  />
                  <q-btn
                    v-if="filterApplied"
                    @click="removeFilter"
                    color="negative"
                    class="q-mt-md"
                    :label="pref.app.kor.karaoke.session.removeEcho"
                  />
                </div>
              </q-card-section>

              <q-card-section
                style="display: flex; justify-content: space-between"
              >
                <!-- 캠활성화, 음소거 버튼 -->
                <q-btn
                  id="camera-activate"
                  @click="handleCameraBtn"
                  :color="store.camerOff ? 'primary' : 'negative'"
                  :label="
                    store.camerOff
                      ? pref.app.kor.karaoke.session.cameraOn
                      : pref.app.kor.karaoke.session.cameraOff
                  "
                />
                <q-btn
                  id="mute-activate"
                  @click="handleMuteBtn"
                  :color="store.muted ? 'primary' : 'negative'"
                  :label="
                    store.muted
                      ? pref.app.kor.karaoke.session.unmute
                      : pref.app.kor.karaoke.session.mute
                  "
                />
              </q-card-section>
            </q-page>
          </q-page-container>
        </q-layout>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import pref from "@/js/config/preference.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

const store = useKaraokeStore();

// 필터를 위한 변수
const filterApplied = ref(false);

const selectedIndex = ref(2);
const sliders = [
  { delay: 10000000, intensity: 0.2, feedback: 0.3 },
  { delay: 15000000, intensity: 0.4, feedback: 0.4 },
  { delay: 20000000, intensity: 0.6, feedback: 0.5 },
  { delay: 25000000, intensity: 0.8, feedback: 0.6 },
  { delay: 30000000, intensity: 1.0, feedback: 0.7 },
];

// 필터를 적용해주는 함수
function applyFilter() {
  const filter = {
    type: "GStreamerFilter",
    options: {
      command: `audioecho delay=${
        sliders[selectedIndex.value].delay
      } intensity=${sliders[selectedIndex.value].intensity} feedback=${
        sliders[selectedIndex.value].feedback
      }`,
    },
  };

  // 필터를 적용해주는 부분
  store.publisher.stream.applyFilter(filter.type, filter.options);
  filterApplied.value = true;
}

// 필터를 지우는 함수
const removeFilter = () => {
  store.publisher.stream.removeFilter();
  filterApplied.value = false;
};
// 필터 함수 종료

// 음소거, 캠 활성화 버튼 작동
function handleCameraBtn() {
  if (!store.publisher) return;
  store.camerOff = !store.camerOff;
  store.publisher.publishVideo(!store.camerOff);
}

function handleMuteBtn() {
  if (!store.publisher) return;
  store.muted = !store.muted;
  store.publisher.publishAudio(!store.muted);
}

function closeModal() {
  store.toggleModals["input-controller"] = false;
}
</script>

<style scoped></style>
