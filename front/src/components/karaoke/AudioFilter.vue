<template>
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
  <!-- 필터 버튼 목록 -->
  <div class="q-mb-md">
    <q-select
      v-model="selectedFilter"
      :options="filterOptions"
      label="필터 적용"
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
</template>

<script setup>
import { ref, computed } from "vue";

const props = defineProps({
  publisher: Object,
});

// 필터를 위한 변수
const selectedFilter = ref("");
const filterApplied = ref(false);
const filterOptions = [
  { label: "Audio echo", value: "Audioecho" },
  { label: "Audio amplify", value: "Amplify" },
  { label: "Pitch", value: "Pitch" },
];

// 에코 관련 변수
const sliders = ref([
  { label: "delay", value: 300, min: 100, max: 500, step: 10 },
  { label: "intensity", value: 0.5, min: 0.1, max: 1, step: 0.1 },
  { label: "feedback", value: 0.2, min: 0, max: 0.5, step: 0.01 },
  { label: "amplification", value: 1.0, min: 0.5, max: 1.5, step: 0.1 },
  { label: "pitch", value: 1.0, min: 0.5, max: 1.5, step: 0.1 },
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
  console.log(props.publisher);
  props.publisher.stream.applyFilter(filter.type, filter.options);
  filterApplied.value = true;
}

// 필터를 지우는 함수
const removeFilter = () => {
  props.publisher.stream.removeFilter();
  filterApplied.value = false;
};

// 선택된 라디오 버튼에 따라 동적으로 보여지는 슬라이더 목록
const filteredSliders = computed(() => {
  if (selectedFilter.value.value === "Audioecho") {
    return sliders.value.filter((slider) =>
      ["delay", "intensity", "feedback"].includes(slider.label)
    );
  } else if (selectedFilter.value.value === "Amplify") {
    return sliders.value.filter((slider) =>
      ["amplification"].includes(slider.label)
    );
  } else if (selectedFilter.value.value === "Pitch") {
    return sliders.value.filter((slider) => ["pitch"].includes(slider.label));
  } else {
    return null;
  }
});
// 필터 함수 종료
</script>

<style scoped></style>
