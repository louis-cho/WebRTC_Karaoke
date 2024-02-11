<template>
  <q-dialog v-model="store.toggleModals['input-controller']">
    <q-card>
      <q-card-section style="max-height: 400px; overflow-y: auto">
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
                <div
                  class="q-mb-md"
                  v-for="(slider, index) in filteredSliders"
                  :key="index"
                >
                  <label
                    style="
                      white-space: nowrap;
                      display: flex;
                      justify-content: space-between;
                    "
                  >
                    {{ slider.label }}: {{ slider.value }}
                    <q-slider
                      v-model="slider.value"
                      :min="slider.min"
                      :max="slider.max"
                      :step="slider.step"
                      style="width: 60%"
                    />
                  </label>
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
const selectedFilter = ref({ label: "에코", value: "Audioecho" });
const filterApplied = ref(false);

// 에코 관련 변수
const sliders = ref([
  { label: "딜레이", value: 300, min: 100, max: 500, step: 10 },
  { label: "강도", value: 0.5, min: 0.1, max: 1, step: 0.1 },
  { label: "피드백", value: 0.2, min: 0, max: 0.5, step: 0.01 },
]);

// 필터를 적용해주는 함수
function applyFilter() {
  const filter = {
    type: "GStreamerFilter",
    options: {
      command: `audioecho delay=${sliders.value[0].value} intensity=${sliders.value[1].value} feedback=${sliders.value[2].value}`,
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

// 선택된 라디오 버튼에 따라 동적으로 보여지는 슬라이더 목록
const filteredSliders = computed(() => {
  return selectedFilter.value.value === "Audioecho"
    ? sliders.value.filter((slider) =>
        ["딜레이", "강도", "피드백"].includes(slider.label)
      )
    : null;
});

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
