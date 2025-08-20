<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import tripApi from "@/api/tripApi.js";
import {useTravelCreateStore} from "@/stores/tripStore.js";
import {useRouter} from "vue-router";
import { MoveRight, Search } from 'lucide-vue-next';
import Header from "@/components/layout/Header.vue";
import { Lightbulb } from 'lucide-vue-next';

const store = useTravelCreateStore();
const userId = ref();
const inputValue = ref('');
const showList = ref(false);
const options = ref([]);
const addedItems = ref([]);
const router = useRouter();
var timer;
const debouncedFetch = value => {
  clearTimeout(timer);
  timer = setTimeout(() => getOptions(value), 300);
};

async function getOptions(query) {
  if (!query) {
    options.value = [];
    return;
  }
  try {
    console.log("검색 요청:", query);
    const results = await tripApi.searchNickname(query);
    options.value = results.filter(
        (item) =>
            !addedItems.value.some((added) => added.userId === item.userId) &&
            item.userId !== userId.value
    );
  } catch (error) {
    console.error("검색어 불러오기 실패:", error);
  }
}

async function fetchUserId() {
  userId.value = await tripApi.getUserId();
}


function onInput(e) {
  inputValue.value = e.target.value;
  showList.value = true;
  debouncedFetch(inputValue.value);
}

function onButtonClick(option) {
  console.log(option);
  addedItems.value.push(option);
  inputValue.value = '';
  showList.value = false;
  options.value = [];
}

function removeItem(idx) {
  addedItems.value.splice(idx, 1);
}

const wrapper = ref(null);

function handleClickOutside(e) {
  if (wrapper.value && !wrapper.value.contains(e.target)) {
    showList.value = false;
  }
}

async function createTrip() {
  const createButton = document.querySelector('.round-next-btn');
  try {
    console.log(store.tripName);
    const payload = {
      tripName: store.tripName,
      startDate: store.startDate,
      endDate: store.endDate,
      budget: store.budget,
      tripStatus: 'READY',
      members: addedItems.value     // [{id, nickname}, ...] 배열 형태로 전달
    };

    createButton.disabled = true;
    const response = await tripApi.createTrip(payload);
    console.log('여행 생성 성공:', response);
    store.tripName = '';
    store.startDate = null;
    store.endDate = null;
    store.budget = null;
    alert('여행이 생성되었습니다!');
    router.replace('/');
  } catch (error) {
    console.error('여행 생성 실패:', error);
    createButton.disabled = false;
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  fetchUserId();
});
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

</script>

<template>
  <Header title="멤버 초대하기" @back="router.back"/>
  <div class="content-container">
    <div class="info-box">
      <p class="trip_title">{{store.tripName}}</p>
      <p class="guide">친구들을 초대하고</p>
      <p class="guide">함께 여행을 계획하세요!</p>
      <div class="tip-box">
        <p class="tip-text"><Lightbulb :size="12" />모든 것을 완벽하게 하려고 하기보다, </p>
        <p class="tip-text">예기치 못한 상황을 즐겨보세요!</p>
      </div>
    </div>
    <div class="autocomplete" ref="wrapper" style="position: relative">
      <p class="menu-label">회원 검색</p>
      <div class="input-wrapper">
            <span class="unit-text">
              <Search size="20" />
            </span>
        <input
            class="input-box"
            v-bind:value="inputValue"
            :disabled = "addedItems.length >= 10"
            @input="onInput"
            @focus="showList = true"
            type="text"
            placeholder="닉네임으로 검색"
        />
      </div>
      <div
          v-if="showList && options.length"
          class="autocomplete-list"
      >
        <div
            class="autocomplete-item"
            v-for="(option, index) in options"
            :key="index"
        >
          <div class = "avatar avatar-sm">{{ option.nickname.charAt(0) }}</div>
          <div class = "autocomplete-name">
            {{ option.nickname }}
            <button class="autocomplete-btn" @click.stop="onButtonClick(option)">초대하기</button>
          </div>
        </div>
      </div>
      <p class="menu-label">초대 목록</p>
      <div v-if="addedItems.length" class="added-list">
        <ul>
          <li v-for="(item, idx) in addedItems" :key="idx">
            <div class="added-item">
              <div class="avatar avatar-lg">{{ item.nickname.charAt(0)}}</div>
              <div class="list-item">
                {{ item.nickname }}
                <button @click="removeItem(idx)">삭제</button>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
    <button
        class="round-next-btn"
        @click="createTrip"
    >
      <MoveRight/>
    </button>
  </div>
</template>

<style scoped>

p{
  margin: 0;
}

.content-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: calc(56px) 1.25rem 1.25rem;
}

.autocomplete-list {
  border: 1px solid #ccc;
  position: absolute;
  background: white;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
  border-bottom-left-radius: 10px;
  border-bottom-right-radius: 10px;
}

.autocomplete-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  cursor: pointer;
}

.autocomplete-item:hover {
  background-color: var(--theme-bg);
}

.autocomplete-btn{
  background-color: var(--theme-primary);
  color: var(--theme-text);
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.autocomplete-btn:hover {
  background-color: #ffd166;
}

.autocomplete-name{
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex:1;
}

.added-list {
  margin-top: 6px;
}

.added-list ul {
  list-style: none;
  padding: 0;
}

.added-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}

.menu-label{
  color: var(--theme-text);
  margin: 5px 5px;
  padding-top: 20px;
}

.added-list button {
  background: rgba(255, 29, 12, 0.20);
  color: white;
  border-radius: 5px;
  border: none;
  padding: 4px 4px;
  cursor: pointer;
}

.added-list button:hover {
  background: rgb(240, 99, 99);
}

.info-box{
  margin: 8px 0;
  background-color: #ffffff;
  padding: 16px 0;
  border: 1px solid rgba(136, 136, 136, 0.2);
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.trip_title{
  margin: 2px 0;
  color: var(--theme-text-light);
  font-size: 14px;
}
.guide{
  font-size: 22px;
  font-weight: bold;
  color: var(--theme-text);
}

.tip-box{
  margin: 4px;
  background: rgba(192, 191, 191, 0.3);
  border-radius: 8px;
  padding: 3px 7px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.tip-text{
  margin: 4px;
  font-size: 12px;
  color: var(--theme-text-light);
}

.added-item{
  width: 100%;
  padding: 8px;
  background: white;
  display: flex;
  align-items: center;
  border-radius: 10px;
}
.list-item{
  display: flex;
  justify-content: space-between;
  flex: 1;
}
.avatar{
  border-radius: 50%;
  background: var(--theme-primary);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-sm{
  height : 25px;
  width : 25px;
  font-size : 14px;
  font-weight: 600;
  margin-right: 10px;
}

.avatar-lg{
  height : 30px;
  width : 30px;
  font-size : 14px;
  font-weight: 600;
  margin-right: 10px;
}
.round-next-btn{
  background-color: var(--theme-primary);
  border-radius: 50%;
  border: none;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50px;
  width: 50px;
  position: absolute;
  bottom : 8%;
  left: 50%;
  transform: translateX(-50%);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
}

.round-next-btn:hover{
  background-color: #ffd166;
}

.round-next-btn:active {
  transform: translateX(-50%) scale(0.95);
}



.input-wrapper {
  position: relative;
  display: inline-flex;
  align-items: center;
  width: 100%;
}

.input-box {
  width: 100%;
  padding-left: 40px;
  height: 40px;
  box-sizing: border-box;
  border-radius: 5px;
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.unit-text {
  position: absolute;
  left: 12px;
  top: 50%;
  height: 20px;
  transform: translateY(-50%);
  color: var(--theme-text-light);
  pointer-events: none;
  font-size: 14px;
}

</style>
