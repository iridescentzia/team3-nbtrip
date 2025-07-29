<script setup>
import tripApi from "@/api/tripApi.js";
import {computed, ref} from 'vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
import { travelCreateStore } from "@/stores/tripStore.js"
const date = ref({});
const disableDates = ref();
const load = async () => {
  disableDates.value=await tripApi.getDiabledDates();
}
load();
const store = travelCreateStore()

const tripName = computed(()=> store.travelName);
const startDate = computed(()=> store.startDate);
const endDate = computed(()=> store.endDate);
const budget = computed(()=> store.budget);

</script>

<template>
  <label for="trip_name">어떤 여행인가요?</label><br>
  <input type="text" name="trip_name" id="trip_name" placeholder="예) 서울 우정 여행" v-mode>
  <VueDatePicker
      v-model="date"
      :range="{ noDisabledRange: true }"
      :enable-time-picker="false"
      :disabled-dates="disableDates"
      locale="ko"
      cancelText="취소"
      selectText="선택"
  ></VueDatePicker>
  <label for="budget">예산을 입력해주세요</label><br>
  <div class="input-wrapper">
    <input type="number" class="input-with-unit" name="budget" id="budget">
    <span class="unit-text">원</span>
  </div>

</template>

<style scoped>
  input{
    padding-right: 40px;
    height: 32px;
    box-sizing: border-box;
  }

  input::-webkit-inner-spin-button {
    appearance: none;
    -moz-appearance: none;
    -webkit-appearance: none;
  }
  .input-wrapper {
    position: relative;
    display: inline-flex; /* inline-block -> inline-flex */
    align-items: center;  /* 수직 가운데 정렬 */
  }
  .input-with-unit {
    padding-right: 40px;
    height: 32px;
    box-sizing: border-box;
  }
  .unit-text {
    position: absolute;
    right: 10px;
    color: #666;
    pointer-events: none;
    font-size: 14px;
    top: auto;   /* 기존 top 삭제 */
    transform: none; /* 기존 transform 삭제 */
  }
</style>