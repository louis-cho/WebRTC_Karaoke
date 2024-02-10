<template>
  <q-dialog v-model="store.toggleModals['audio-filter']">
    <q-card>
      <q-card-section>
        <!-- 필터 버튼 목록 -->
        <div class="q-mb-md">
          <q-select
            v-model="selectedFilter"
            :options="filterOptions"
            :label="pref.app.kor.karaokePage.filterList"
            filled
          />
          <!-- 슬라이더 목록을 보여주는 부분 -->
          <div>
            <!-- 선택된 필터에 따라 동적으로 보여지는 슬라이더 목록 -->
            <div
              class="q-mb-md"
              v-for="(slider, index) in filteredSliders"
              :key="index"
            >
              <label style="white-space: nowrap">
                {{ slider.label }}:
                <!-- 슬라이더의 현재 값 표시 -->
                {{ slider.value }}
                <!-- 슬라이더 컴포넌트 -->
                <q-slider
                  v-model="slider.value"
                  :min="slider.min"
                  :max="slider.max"
                  :step="slider.step"
                  style="width: 50%"
                />
              </label>
            </div>
          </div>
        </div>
      </q-card-section>

      <q-card-section>
        <q-btn
          v-if="!filterApplied"
          @click="applyFilter"
          color="primary"
          class="q-mt-md"
          :label="pref.app.kor.karaokePage.applyFilter"
        />
        <q-btn
          v-if="filterApplied"
          @click="removeFilter"
          color="negative"
          class="q-mt-md"
          :label="pref.app.kor.karaokePage.removeFilter"
        />
        <q-btn
          @click="closeModal"
          color="negative"
          class="q-mt-md"
          label="닫기"
        />
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import pref from "@/js/config/preference.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";

const props = defineProps({
  toggle: Boolean,
});

const store = useKaraokeStore();

// 필터를 위한 변수
const selectedFilter = ref("");
const filterApplied = ref(false);
const filterOptions = [
  { label: "에코", value: "Audioecho" },
  { label: "증폭", value: "Amplify" },
  { label: "피치", value: "Pitch" },
];

// 에코 관련 변수
const sliders = ref([
  { label: "딜레이", value: 300, min: 100, max: 500, step: 10 },
  { label: "강도", value: 0.5, min: 0.1, max: 1, step: 0.1 },
  { label: "피드백", value: 0.2, min: 0, max: 0.5, step: 0.01 },
  { label: "증폭", value: 1.0, min: 0.5, max: 1.5, step: 0.1 },
  { label: "피치", value: 1.0, min: 0.5, max: 1.5, step: 0.1 },
]);

// 필터를 적용해주는 함수
function applyFilter() {
  const filter = { type: "GStreamerFilter", options: {} };

  switch (selectedFilter.value.value) {
    case "Audioecho":
      filter.type = "GStreamerFilter";
      filter.options = {
        command: `audioecho delay=${sliders.value[0].value} intensity=${sliders.value[1].value} feedback=${sliders.value[2].value}`,
      };
      break;
    case "Amplify":
      filter.type = "GStreamerFilter";
      filter.options = {
        command: `audioamplify amplification=${sliders.value[3].value}`,
      };
      break;
    case "Pitch":
      filter.type = "GStreamerFilter";
      filter.options = { command: `pitch pitch=${sliders.value[4].value}` };
      break;
  }

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
  if (selectedFilter.value.value === "Audioecho") {
    return sliders.value.filter((slider) =>
      ["딜레이", "강도", "피드백"].includes(slider.label)
    );
  } else if (selectedFilter.value.value === "Amplify") {
    return sliders.value.filter((slider) => ["증폭"].includes(slider.label));
  } else if (selectedFilter.value.value === "Pitch") {
    return sliders.value.filter((slider) => ["피치"].includes(slider.label));
  } else {
    return null;
  }
});
// 필터 함수 종료

function closeModal() {
  store.toggleModals["audio-filter"] = false;
}
</script>

<style scoped></style>
