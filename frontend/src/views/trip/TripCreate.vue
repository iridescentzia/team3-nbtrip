<script setup>
import tripApi from "@/api/tripApi.js";
import {computed, ref} from 'vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
import { useTravelCreateStore } from "@/stores/tripStore.js"
import Button from "@/components/common/Button.vue";
import {useRouter} from "vue-router";
import Header from "@/components/layout/Header.vue";
const router=useRouter();
const rawDate = ref({});
const disableDates = ref();
const load = async () => {
  disableDates.value=await tripApi.getDisabledDates();
}
load();
const toNextPage = () => {
  console.log('입력한 이름:', store.tripName);
  router.push('/trip/invite');
}

const handleDate = (modelData) => {
  rawDate.value = modelData;
  store.startDate = new Date(modelData[0]);
  store.endDate = new Date(modelData[1]);
  // do something else with the data
  console.log(rawDate.value);
}

const store = useTravelCreateStore();

</script>

<template>
  <div class="view-wrapper">
    <div class="tripcreate-view">
      <Header title="새로운 여행 만들기"/>
      <div class="content-container">
        <label for="trip_name" class="label-text">어떤 여행인가요?</label><br>
        <input
            type="text"
            name="trip_name"
            id="trip_name"
            placeholder="예) 서울 우정 여행"
            v-model="store.tripName"
            class="input-box"
        />
        <p class="label-text">언제 떠나시나요?</p>
        <div class="datepicker-wrapper">
          <VueDatePicker
              inline auto-apply
              v-model="rawDate"
              :range="{ noDisabledRange: true }"
              :enable-time-picker="false"
              :disabled-dates="disableDates"
              locale="ko"
              cancelText="취소"
              selectText="선택"
              @update:model-value="handleDate"
          ></VueDatePicker>
        </div>
        <label for="budget" class="label-text">예산을 입력해주세요</label><br>
        <div class="input-wrapper">
          <input type="number" class="input-box" name="budget" id="budget" v-model="store.budget">
          <span class="unit-text">원</span>
        </div>
        <br>
        <Button class="next-btn" @click="toNextPage" label="다음"></Button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tripcreate-view {
  --theme-primary: rgba(255, 209, 102, 0.65);
  --theme-primary-dark: #e2c05e;
  --theme-bg: #f8f9fa;
  --theme-text: #333333;
  --theme-text-light: #888888;
}

/* 화면 중앙 정렬을 위한 wrapper 스타일 */
.view-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh; /* 화면 전체 높이를 차지하도록 */
  background-color: #ffffff;
  padding: 2rem 0; /* 위아래 여백 추가 */
}

/* 전체 레이아웃 */
.tripcreate-view {
  z-index: 1;
  width: 100%;
  max-width: 24rem; /* 384px */
  background-color: var(--theme-bg);
  display: flex;
  flex-direction: column;
  border-radius: 1.5rem; /* 둥근 모서리 */
  box-shadow: 0 25px 50px -12px rgb(0 0 0 / 0.25); /* 그림자 효과 */
  overflow: hidden; /* 둥근 모서리 적용을 위해 */
  position: relative; /* Header 컴포넌트의 fixed 포지션 기준점 */
  height: 844px; /* 특정 스마트폰 높이를 기준으로 고정 */
  max-height: 90vh; /* 화면 높이의 90%를 넘지 않도록 설정 */
}

/* 메인 콘텐츠 */
.content-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: calc(56px) 1.25rem 1.25rem;
}

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
  width: 100%;
  height: 50px;
  margin: 32px auto 12px auto;
}
</style>
<!--scoped에선 datepicker에 대한 커스터마이징이 먹히지 않아서 관련 내용 style에 정의-->
<style>
.dp__theme_light {
  --dp-primary-color: var(--theme-primary);
}
</style>