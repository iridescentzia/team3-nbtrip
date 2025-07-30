<script setup>
import tripApi from "@/api/tripApi.js";
import {computed, ref} from 'vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
import { useTravelCreateStore } from "@/stores/tripStore.js"
import Button from "@/components/common/Button.vue";
import {useRouter} from "vue-router";
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
  <label for="trip_name">어떤 여행인가요?</label><br>
  <input type="text" name="trip_name" id="trip_name" placeholder="예) 서울 우정 여행" v-model="store.tripName" />
  <p>언제 떠나시나요?</p>
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
  <label for="budget">예산을 입력해주세요</label><br>
  <div class="input-wrapper">
    <input type="number" class="input-with-unit" name="budget" id="budget" v-model="store.budget">
    <span class="unit-text">원</span>
  </div>
  <br>
  <Button @click="toNextPage">다음</Button>
</template>

<style scoped>
  input{
    padding-right: 40px;
    height: 40px;
    box-sizing: border-box;
    border-radius: 5px;
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
  }
  .input-with-unit {
    padding-right: 40px;
    height: 40px;
    box-sizing: border-box;
  }
  .unit-text {
    position: absolute;
    right: 10px;
    color: #666;
    pointer-events: none;
    font-size: 14px;
    top: auto;
    transform: none;
  }
</style>