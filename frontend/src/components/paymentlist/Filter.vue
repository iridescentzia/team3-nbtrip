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
        <div class="member-list member-list--no-scroll">
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
          </div>
        <Button @click="applyCategoryFilter" label="조회하기"></Button>
        
      </div>
    </transition>
  </div>

  <!-- 결제 참여자 모달 창 -->
  <div
    v-if="isParticipantModalOpen"
    class="overlay"
    @click.self="closeParticipantModal"
  >
    <Transition name="slide-up" appear>
      <div class="bottom-modal">
        <h3 class="modal-title">결제 참여자</h3>
        <div class="member-list member-list--no-scroll">
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
    </Transition>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, Transition } from 'vue';
import { ChevronDown, RotateCcw } from 'lucide-vue-next';
import { usePaymentlistStore } from "@/stores/tripStore.js";
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

const tripStore = usePaymentlistStore();

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

// 공통: 스크롤 잠금 / 해제
const lockScroll = () => {
  const container = document.querySelector('.content-container');
  if (container) {
    container.scrollTop = 0;      // 스크롤 맨 위로
    container.style.overflow = 'hidden';
  }
};
const unlockScroll = () => {
  const container = document.querySelector('.content-container');
  if (container) {
    container.style.overflow = 'auto';
  }
};

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
  
  // 모달창 열려 있으면 스크롤 잠금
  if (isDateModalOpen.value) {
    lockScroll();
  } else {
    unlockScroll();
  }
};

const closeDateModal = () => {
  isDateModalOpen.value = false;
  unlockScroll();
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

    // 모달창 열려 있으면 스크롤 잠금
  if (isCategoryModalOpen.value) {
    lockScroll();
  } else {
    unlockScroll();
  }
};

const closeCategoryModal = () => {
  isCategoryModalOpen.value = false;
  unlockScroll();
};

// 선택된 카테고리 필터링
const applyCategoryFilter = () => {
  emit('category-filtered', selectedCategories.value) // 부모로 전달
  closeCategoryModal();
};

const toggleParticipantModal = async () => {
  isParticipantModalOpen.value = !isParticipantModalOpen.value;

    // 모달창 열려 있으면 스크롤 잠금
  if (isParticipantModalOpen.value) {
    lockScroll();
  } else {
    unlockScroll();
  }
};

const closeParticipantModal = () => {
  isParticipantModalOpen.value = false;
  unlockScroll();
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
  z-index: 2000;
}

.bottom-modal {
  position: absolute;   /* content-container 기준 유지 */
  bottom: 0;
  /* ✅ 수평 정렬 방식 교체 */
  left: 0;
  right: 0;
  margin: 0 auto;
  transform: none;

  width: 100%;
  max-width: 384px;
  background: #fff;

  /* 높이: 내용기반 + 상한 */
  height: auto;
  /* max-height: none;   */
  display: flex;
  flex-direction: column;

  padding: 16px 0;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  box-shadow: 0 -4px 16px rgba(0,0,0,0.15);
  z-index: 2001;
  animation: modalUp 0.25s ease;
}
/*@supports not (height: 100svh) {
  .bottom-modal { max-height: 60vh; }
}*/

@keyframes modalUp {
  from {
    bottom: -300px;
    opacity: 0;
  }
  to {
    bottom: 0;
    opacity: 1;
  }
}

.modal-title{
  margin-left: 20px;
}

/* vue3datepicker 입력 필드 글씨 크기 변경 */
:deep(.dp__input) {
  font-size: 18px; /* 원하는 크기 */
}


/* 내부 스크롤 영역 */
.member-list {
  padding: 0 30px;
  padding-bottom: 40px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px 16px;
}

/* ✅ 카테고리: 스크롤 없음 */
.member-list--no-scroll {
  overflow: hidden;                   /* 스크롤 막기 */
  max-height: none;                    /* 높이 제한 없음 (카테고리 개수 적다는 가정) */
}

/* ✅ 결제참여자: 내부 스크롤 + 깔끔한 스크롤바 */
.member-list--scroll {
  max-height: 40svh;                   /* 모달 내에서 적당히만 보이게 */
  overflow-y: auto;
  overscroll-behavior: contain;        /* 스크롤 체인 방지(바깥으로 튐 방지) */
  
  /* Firefox 스타일 */
  scrollbar-width: thin;
  scrollbar-color: #bbb #f0f0f0;
}

/* ✅ WebKit(Chrome/Edge/Safari)은 :deep() 필요 */
.member-list--scroll::-webkit-scrollbar {
  width: 8px;
}
.member-list--scroll::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 50px;
}
.member-list--scroll::-webkit-scrollbar-thumb {
  background-color: #bbb;
  border-radius: 50px;          /* tripdetail과 동일하게 둥글게 */
  border: 2px solid transparent;
  background-clip: padding-box;
}
.member-list--scroll::-webkit-scrollbar-thumb:hover {
  background-color: #888;
}

/* ── 체크박스 아이템 한 줄 레이아웃 ── */
.member-item {
  display: flex;
  align-items: center;
  gap: 9px;               /* 체크박스와 텍스트 간격 */
  font-size: 18px;
  color: #333333;
  user-select: none;      /* 텍스트 드래그 방지 */
  line-height: 1.2;
  cursor:pointer;
  padding-bottom:2px;
}

/* ── 커스텀 체크박스 ── */
.member-item input[type='checkbox'] {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;

  width: 18px;
  height: 18px;
  border: 2px solid #ffd166;   
  border-radius: 4px;
  background-color: #ffffff;
  cursor: pointer;
  position: relative;
  margin: 0;                   /* 라벨 내부이므로 margin 제거 */
  transition: background-color 0.2s, border-color 0.2s, box-shadow 0.2s;
}

/* hover/focus 상태 (선택) */
.member-item input[type='checkbox']:hover {
  box-shadow: 0 0 0 3px rgba(255, 209, 102, 0.25);
}
.member-item input[type='checkbox']:focus-visible {
  outline: none;
  box-shadow: 0 0 0 3px rgba(255, 209, 102, 0.45);
}

/* 체크됨 */
.member-item input[type='checkbox']:checked {
  background-color: #ffd166;
  border-color: #ffd166;
}

/* 체크 표시 (하얀색 V) */
.member-item input[type='checkbox']:checked::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 9px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: translate(-50%, -60%) rotate(45deg);
  display: block;
}

/* 비활성화 상태 (필요 시) */
.member-item input[type='checkbox']:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
  padding-bottom: 40px;
}
</style>
