<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue';
import tripApi from "@/api/tripApi.js";
import {useTravelCreateStore} from "@/stores/tripStore.js";
import Button from "@/components/common/Button.vue";

const store = useTravelCreateStore();
const inputValue = ref('');
const showList = ref(false);
const options = ref([]);
const addedItems = ref([]);

function debounce(fn, delay) {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => fn(...args), delay);
  };
}

async function getOptions(query) {
  if (!query) {
    options.value = [];
    return;
  }
  try {
    console.log("검색 요청:", query);
    options.value = await tripApi.searchNickname(query);
  } catch (error) {
    console.error("검색어 불러오기 실패:", error);
  }
}

const debouncedFetch = debounce((value) => {
  getOptions(value);
}, 300);

function onInput(e) {
  inputValue.value = e.target.value;
  showList.value = true;
  debouncedFetch(inputValue.value);
}

function onButtonClick(option) {
  if (!addedItems.value.includes(option)) {
    addedItems.value.push(option);
  }
  inputValue.value = ''; // ✅ 추가 후 입력창 초기화
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
  try {
    console.log(store.tripName);
    const payload = {
      ownerId: 1,
      tripName: store.tripName,
      startDate: store.startDate,   // 'YYYY-MM-DD' 형태
      endDate: store.endDate,
      budget: store.budget,
      tripStatus: 'READY',
      members: addedItems.value     // [{id, nickname}, ...]
    };

    const response = await tripApi.createTrip(payload);
    console.log("여행 생성 성공:", response);
    alert("여행이 생성되었습니다!");
  } catch (error) {
    console.error("여행 생성 실패:", error);
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});

</script>

<template>
  <h1>{{store.tripName}}</h1>
  <div class="autocomplete" ref="wrapper" style="position: relative;">
    <input
        v-bind:value="inputValue"
        @input="onInput"
        @focus="showList = true"
        type="text"
        placeholder="이름 입력..."
    />
    <div
        v-if="showList && options.length"
        class="autocomplete-list"
    >
      <div
          class="autocomplete-item"
          v-for="(option, index) in options"
          :key="index"
      >
        {{ option }}
        <button @click.stop="onButtonClick(option)">+</button>
      </div>
    </div>

    <div v-if="addedItems.length" class="added-list">
      <h4>추가된 사용자</h4>
      <ul>
        <li v-for="(item, idx) in addedItems" :key="idx">
          {{ item }}
          <button @click="removeItem(idx)">x</button>
        </li>
      </ul>
    </div>
    <Button @click="createTrip"/>
  </div>
</template>

<style scoped>
.autocomplete-list {
  border: 1px solid #ccc;
  position: absolute;
  background: white;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
}

.autocomplete-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  cursor: pointer;
}

.autocomplete-item:hover {
  background-color: #f0f0f0;
}

.autocomplete-item button {
  padding: 2px 6px;
  font-size: 12px;
}

.added-list {
  margin-top: 12px;
  border-top: 1px solid #ccc;
  padding-top: 8px;
}

.added-list ul {
  list-style: none;
  padding: 0;
}

.added-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.added-list button {
  background: red;
  color: white;
  border: none;
  padding: 2px 6px;
  cursor: pointer;
}
</style>
