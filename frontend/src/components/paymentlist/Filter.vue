<template>
  <div class="filter-bar">
    <!-- 리셋 아이콘 -->
    <RotateCcw class="reset-icon" @click="resetFilter"/>

    <!-- 날짜 필터 버튼 -->
    <!-- 버튼 active 조건: 필터 열려 있거나 적용되어 있을 때 -->
    <button
        class="filter-button"
        :class="{ active: isDateModalOpen || isDateFiltered }"
        @click="toggleDateModal"
    >
      <div class="filter-row">
        <div class="filter-text">날짜</div>
        <ChevronDown class="down-icon" />
      </div>
    </button>

    <button
      class="filter-button"
      :class="{ active: isCategoryModalOpen || isCategoryFiltered }"
      @click="toggleCategoryModal"
      >
      <div class="filter-row">
        <div class="filter-text">카테고리</div>
        <ChevronDown class="down-icon" />
      </div>
    </button>

    <button
      class="filter-button"
      :class="{ active: isParticipantModalOpen || isParticipantsFiltered }"
      @click="toggleParticipantModal"
      >
      <div class="filter-row">
        <div class="filter-text">결제 참여자</div>
        <ChevronDown class="down-icon" />
      </div>
    </button>
  </div>

  <!-- 날짜 선택 모달 -->
  <div 
  v-if="isDateModalOpen" 
  class="overlay" 
  @click.self="closeDateModal">
    <transition name="slide-up" appear>
      <div class="bottom-modal">
        <h3 class="modal-title">날짜 선택</h3>

        <!-- @vuepic/vue-datepicker Range 모드 -->
        <VueDatePicker
          v-model="pickerRange"
          :range="true"
          format="yyyy-MM-dd"
          :clearable="true"
          class="date-picker"
        />
        <Button @click="applyDateFilter" label="조회하기"></Button>
      </div>
    </transition>
  </div>

  <!-- 카테고리 선택 모달 창 -->
  <div
    v-if="isCategoryModalOpen"
    class="overlay"
    @click.self="closeCategoryModal"
  >
    <transition name="slide-up" appear>
      <div class="bottom-modal">
        <h3 class="modal-title">카테고리</h3>
        <div class="member-list">
          <label
            v-for="category in tripStore.merchantCategories"
            :key="category.categoryId"
            class="member-item"
          >
          <input
            type="checkbox"
            v-model="selectedCategories"
            :value="category.categoryId"
          />
          {{ category.categoryName }}
          </label>
        <Button @click="applyCategoryFilter" label="조회하기"></Button>
        </div>
      </div>
    </transition>
  </div>

  <!-- 결제 참여자 모달 창 -->
  <div
    v-if="isParticipantModalOpen"
    class="overlay"
    @click.self="closeParticipantModal"
  >
    <transition name="slide-up" appear>
      <div class="bottom-modal">
        <h3 class="modal-title">결제 참여자</h3>
        <div class="member-list">
          <label
            v-for="member in props.members"
            :key="member.userId"
            class="member-item"
          >
            <input
              type="checkbox"
              v-model="selectedMembers"
              :value="member.userId"
            />
            {{ member.nickname }}
          </label>
        </div>
        <Button @click="applyParticipantFilter" label="조회하기"></Button>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ChevronDown, RotateCcw } from 'lucide-vue-next';
import {usePaymentListStore} from "@/stores/tripStore.js";
import Button from '@/components/common/Button.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'

const props = defineProps({
  startDate: {
    type: String,
    default: ''
  },
  members: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['date-filtered', 'participant-filtered', 'category-filtered'])

const tripStore = usePaymentListStore();

const selectedMembers = ref([]);
const selectedCategories = ref([])

const today = new Date()
const pickerRange = ref([props.startDate, today])


// 모달 열림/닫힘 상태
const isDateModalOpen = ref(false)
const hasApplied = ref(false)

const isParticipantModalOpen = ref(false);
const isCategoryModalOpen = ref(false);

const dateRange = ref({ start: '', end: '' });
const categoryFilter = ref(null);
const selectedParticipants = ref([]); // 복수 선택 가능

// 버튼 active 여부 계산
const isDateFiltered = computed(() => {
 return hasApplied.value && (
    pickerRange.value[0] !== null ||
    pickerRange.value[1] !== null  )
})

const isCategoryFiltered = computed(() => selectedCategories.value > 0);
const isParticipantsFiltered = computed(() => selectedMembers.value.length > 0);

// 날짜 모달 토글 함수
const toggleDateModal = () => {
  isDateModalOpen.value = !isDateModalOpen.value;
};

const closeDateModal = () => {
  isDateModalOpen.value = false;
};


// "2025-07-30" 형식으로 변환
const formatDateHyphen = (timestamp) => {
  const date = new Date(timestamp)
  // ko-KR + replace 로 yyyy-MM-dd 만들기
  return date
    .toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      timeZone: 'Asia/Seoul'
    })
    // ". " → "-" 로, e.g. "2025.07.30" → "2025-07-30"
    .replace(/\.\s?/g, '-')
    // 이제 끝에 남은 "-" 도 삭제
    .replace(/-$/, '')
}


// “조회하기” 클릭 시 호출: 부모에게 선택된 날짜 범위를 emit
const applyDateFilter = () => {
  hasApplied.value = true
  const [s, e] = pickerRange.value

  // local 기준 “YYYY-MM-DD” 얻기
  const startStr = s ? formatDateHyphen(s) : ''
  const endStr   = e ? formatDateHyphen(e) : ''

  emit('date-filtered', { start: startStr, end: endStr })
  closeDateModal()
}

const toggleCategoryModal = async () => {
  isCategoryModalOpen.value = !isCategoryModalOpen.value;
};

const closeCategoryModal = () => {
  isCategoryModalOpen.value = false;
};

// 선택된 카테고리 필터링
const applyCategoryFilter = () => {
  emit('category-filtered', selectedCategories.value) // 부모로 전달
  closeCategoryModal();
};

const toggleParticipantModal = async () => {
  isParticipantModalOpen.value = !isParticipantModalOpen.value;
};

const closeParticipantModal = () => {
  isParticipantModalOpen.value = false;
};

// 선택된 결제 참여자 필터링
const applyParticipantFilter = () => {
  emit('participant-filtered', selectedMembers.value); // 선택된 결제 참여자 전달
  closeParticipantModal();
};

// 리셋 클릭: 초기 상태로
const resetFilter = () => {
  console.log("click resetFilter")

  // 날짜 필터링 초기화
  hasApplied.value = false
  pickerRange.value = [ props.startDate, today ]
  console.log("reset date: ", pickerRange.value[0], pickerRange.value[1])
  emit('date-filtered', { start: '', end: '' })

  // 결제 참여자 필터링 초기화
  selectedMembers.value = [];
  emit('participant-filtered', []); // 빈 배열로 초기화 알림

  // 카테고리 필터링 초기화
  selectedCategories.value = []
  emit('category-filtered', [])
}

onMounted(async () => {
  // await tripStore.fetchTrip();
  await tripStore.fetchMerchantCategories();
});
</script>

<style scoped>
.filter-bar {
  margin-top: 10px;
}

.filter-button {
  /* background-color: red !important; */
  all: unset;
  border: 1px solid #d9d9d9;
  border-radius: 10px;
  cursor: pointer;
  color: #333;
  padding: 3px 15px;
  margin-left: 7px;
}

.filter-button.active {
  border: 1px solid #383838;
  /* color: white; */
}

.filter-button.active .filter-text{
  color:black;
}

.filter-button.active .down-icon{
  color:black;
}

.filter-row {
  display: flex;
  align-items: center;
  /*gap: 8px;*/
  justify-content: space-between;
}

.filter-text {
  font-size: 12px;
  color: #b0adad;
  font-weight: 500;
}

.reset-icon {
  color: #b0adad;
  stroke-width: 2.3;
  padding-top: 5px;
  width: 20px;
  transform: translateY(7px);
}

.reset-icon:hover{
    color:black;
}

.down-icon {
  color: #b0adad;
  stroke-width: 1;
}

.overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 1500;
}

.bottom-modal {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  max-width: 384px;
  /* max-width: 24rem; */
  margin: 0;
  background-color: white;

  padding: 20px 0 ;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  z-index: 20;
  height: 25vh; /* viewport height: 18% */
   max-height: 30vh; /* 최대 높이 제한 */  
}

.modal-title{
  margin-left: 20px;
}

.member-list{
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 2열로 구성 */
  gap: 10px 16px; /* 세로 10px, 가로 16px 간격 */
  padding: 0 20px;
  overflow-y: auto; /* 내용이 넘치면 스크롤 */
}

/* 슬라이드 애니메이션 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.5s ease, opacity 0.5s ease;
}

.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}

.slide-up-enter-to,
.slide-up-leave-from {
  transform: translateY(0);
  opacity: 1;
}

.date-picker {
  width: calc(100% - 2rem);
  margin: 0 1rem;
}
</style>
