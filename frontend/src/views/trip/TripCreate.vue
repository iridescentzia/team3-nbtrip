<script setup>
import tripApi from "@/api/tripApi.js";
import {onMounted, ref} from 'vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
import { useTravelCreateStore } from "@/stores/tripStore.js"
import Button from "@/components/common/Button.vue";
import {useRouter} from "vue-router";
import Header from "@/components/layout/Header.vue";
const router=useRouter();
const rawDate = ref(null);
const disableDates = ref();
const store = useTravelCreateStore();
const load = async () => {
  try {
    disableDates.value = await tripApi.getDisabledDates();
  } catch (e) {
    console.error('비활성화 날짜 불러오기 실패:', e);
  }
}

const toNextPage = () => {
  console.log('입력한 이름:', store.tripName);
  router.push('/trip/invite');
}

const handleDate = (modelData) => {
  rawDate.value = modelData;
  // modelData가 이제 ['2025-08-08', '2025-08-09'] 같은 문자열 배열입니다.
  // new Date()를 사용하지 않고 그대로 저장합니다.
  store.startDate = modelData[0];
  store.endDate = modelData[1];
  console.log('저장된 날짜:', store.startDate, store.endDate);
}

onMounted(() => {
  load(); // 비활성화 날짜 불러오기
  store.tripName = '';
  store.startDate = null;
  store.endDate = null;
  store.budget = null;
});

</script>

<template>
  <Header title="새로운 여행 만들기" @back="router.back"/>
  <div class="content-container">
    <div class="input-area">
      <p class="label-text">어떤 여행인가요?</p>
      <input
          type="text"
          name="trip_name"
          id="trip_name"
          placeholder="예) 서울 우정 여행"
          v-model="store.tripName"
          class="input-box"
      />
    </div>
    <div class="input-area">
      <p class="label-text">언제 떠나시나요?</p>
      <div class="datepicker-wrapper">
        <VueDatePicker
            inline auto-apply
            v-model="rawDate"
            model-type="yyyy-MM-dd"
            :min-date="new Date()"
            :range="{ noDisabledRange: true }"
            :enable-time-picker="false"
            :disabled-dates="disableDates"
            locale="ko"
            cancelText="취소"
            selectText="선택"
            @update:model-value="handleDate"
        ></VueDatePicker>
      </div>
    </div>
    <p class="label-text">예산을 입력해주세요.</p>
    <div class="input-area">
      <div class="input-wrapper">
        <input type="number" class="input-box" name="budget" id="budget" v-model="store.budget">
        <span class="unit-text">원</span>
      </div>
    </div>
    <Button class="next-btn" @click="toNextPage" label="다음"></Button>
  </div>
</template>

<style scoped>

/* 메인 콘텐츠 */
.content-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: calc(56px) 1.25rem 1.25rem;
}
  /* input:number에 있는 스핀 버튼 제거 */
  input::-webkit-inner-spin-button {
    appearance: none;
    -moz-appearance: none;
    -webkit-appearance: none;
  }
  .input-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  width: 100%;
  }

.input-box {
  width: 100%;
  padding-left: 10px;
  padding-right: 50px;
  height: 40px;
  box-sizing: border-box;
  border-radius: 5px;
  border: 1px solid var(--theme-text-light)
}

.unit-text {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--theme-text-light);
  pointer-events: none;
  font-size: 14px;
}

.label-text{
  margin: 0.5rem 0;
  color: var(--theme-text);
}

.next-btn {
  width: 90%;
  height: 50px;
  position: absolute;
  bottom : 0;
  left: 50%;
  transform: translateX(-50%);
}

.datepicker-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

.input-area{
  margin-bottom: 25px;
}


:deep(.dp__calendar) {
  width: 324px;
}

</style>
<!--scoped에선 datepicker에 대한 커스터마이징이 먹히지 않아서 관련 내용 style에 정의-->
<style>
.dp__theme_light {
  --dp-primary-color: var(--theme-primary);
}
</style>