<template>
  <div class="filter-bar">
    <!-- 리셋 아이콘 -->
    <RotateCcw class="reset-icon" @click="resetFilter"/>

    <!-- 날짜 필터 버튼 -->
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
  <div v-if="isDateModalOpen" class="overlay" @click.self="closeDateModal">
    <transition name="slide-up" appear>
      <div class="bottom-modal"> <!-- CHANGED: 고정 높이 컨테이너 -->
        <h3 class="modal-title">날짜 선택</h3>
        <!-- 컨텐츠 영역은 가운데, flex:1 -->
        <VueDatePicker
          v-model="pickerRange"
          :range="true"
          format="yyyy-MM-dd"
          :clearable="true"
          :teleport="false" 
          class="date-picker"
        />
        <Button @click="applyDateFilter" label="조회하기"></Button>
      </div>
    </transition>
  </div>

  <!-- 카테고리 선택 모달 -->
  <div v-if="isCategoryModalOpen" class="overlay" @click.self="closeCategoryModal">
    <transition name="slide-up" appear>
      <div class="bottom-modal"> <!-- CHANGED: 고정 높이 컨테이너 -->
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

  <!-- 결제 참여자 모달 -->
  <div v-if="isParticipantModalOpen" class="overlay" @click.self="closeParticipantModal">
    <Transition name="slide-up" appear>
      <div class="bottom-modal"> <!-- CHANGED: 고정 높이 컨테이너 -->
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
import { usePaymentListStore } from "@/stores/tripStore.js";
import Button from '@/components/common/Button.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'

const props = defineProps({
  startDate: { type: String, default: '' },
  members: { type: Array, default: () => [] }
});

const emit = defineEmits(['date-filtered', 'participant-filtered', 'category-filtered']);

const tripStore = usePaymentListStore();

const selectedMembers = ref([]);
const selectedCategories = ref([]);

const today = new Date();
const pickerRange = ref([props.startDate, today]);

// 모달 열림/닫힘 상태
const isDateModalOpen = ref(false);
const hasApplied = ref(false);
const isParticipantModalOpen = ref(false);
const isCategoryModalOpen = ref(false);

// 공통: 스크롤 잠금 / 해제 (content-container 기준)
const lockScroll = () => {
  const container = document.querySelector('.content-container');
  if (container) {
    container.scrollTop = 0;
    container.style.overflow = 'hidden';
  }
};
const unlockScroll = () => {
  const container = document.querySelector('.content-container');
  if (container) container.style.overflow = 'auto';
};

// 버튼 active 여부
const isDateFiltered = computed(() => {
  return hasApplied.value && (pickerRange.value[0] !== null || pickerRange.value[1] !== null);
});
// NOTE: 길이 체크가 맞음
const isCategoryFiltered = computed(() => selectedCategories.value.length > 0); // CHANGED
const isParticipantsFiltered = computed(() => selectedMembers.value.length > 0);

// 날짜 모달 토글
const toggleDateModal = () => {
  if (!isDateModalOpen.value) {
    lockScroll();                    // CHANGED: 먼저 잠그고
    isDateModalOpen.value = true;
  } else {
    isDateModalOpen.value = false;   // 해제는 트랜지션 후가 더 자연스럽지만 여기선 간단히
    unlockScroll();
  }
};
const closeDateModal = () => { isDateModalOpen.value = false; unlockScroll(); };

// “YYYY-MM-DD”
const formatDateHyphen = (timestamp) => {
  const date = new Date(timestamp);
  return date
    .toLocaleDateString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', timeZone: 'Asia/Seoul' })
    .replace(/\.\s?/g, '-')
    .replace(/-$/, '');
};

const applyDateFilter = () => {
  hasApplied.value = true;
  const [s, e] = pickerRange.value;
  const startStr = s ? formatDateHyphen(s) : '';
  const endStr   = e ? formatDateHyphen(e) : '';
  emit('date-filtered', { start: startStr, end: endStr });
  closeDateModal();
};

// 카테고리 모달 토글
const toggleCategoryModal = () => {
  if (!isCategoryModalOpen.value) {
    lockScroll();                    // CHANGED
    isCategoryModalOpen.value = true;
  } else {
    isCategoryModalOpen.value = false;
    unlockScroll();
  }
};
const closeCategoryModal = () => { isCategoryModalOpen.value = false; unlockScroll(); };

const applyCategoryFilter = () => {
  emit('category-filtered', selectedCategories.value);
  closeCategoryModal();
};

// 참여자 모달 토글
const toggleParticipantModal = () => {
  if (!isParticipantModalOpen.value) {
    lockScroll();                    // CHANGED
    isParticipantModalOpen.value = true;
  } else {
    isParticipantModalOpen.value = false;
    unlockScroll();
  }
};
const closeParticipantModal = () => { isParticipantModalOpen.value = false; unlockScroll(); };

const applyParticipantFilter = () => {
  emit('participant-filtered', selectedMembers.value);
  closeParticipantModal();
};

onMounted(async () => {
  await tripStore.fetchMerchantCategories();
});
</script>

<style scoped>
/* 공통 버튼/툴바 */
.filter-bar { margin-top: 10px; }
.filter-button {
  all: unset;
  border: 1px solid #d9d9d9;
  border-radius: 10px;
  cursor: pointer;
  color: #333;
  padding: 3px 15px;
  margin-left: 7px;
}
.filter-button.active { border: 1px solid #383838; }
.filter-button.active .filter-text { color:black; }
.filter-button.active .down-icon { color:black; }
.filter-row { display:flex; align-items:center; justify-content:space-between; }
.filter-text { font-size:12px; color:#b0adad; font-weight:500; }
.reset-icon { color:#b0adad; stroke-width:2.3; padding-top:5px; width:20px; transform: translateY(7px); }
.reset-icon:hover { color:black; }
.down-icon { color:#b0adad; stroke-width:1; }

/* 오버레이 */
.overlay {
  position: absolute;
  inset: 0;
  background-color: rgba(0,0,0,0.3);
  z-index: 2000;
}

/* ── 모달 컨테이너: 고정 높이 통일 ── */
:root { --modal-height: clamp(420px, 50svh, 480px); } /* CHANGED: 통일 높이 토큰 */

.bottom-modal {
  position: absolute;     /* content-container 기준 유지 */
  bottom: 0;
  left: 0; right: 0;      /* 가운데 정렬 (폭 변동에도 흔들림 최소화) */
  margin: 0 auto;
  transform: none;

  width: 100%;
  max-width: 384px;
  background: #fff;

  /* CHANGED: 고정 높이 적용 */
  height: clamp(320px, 40svh, 380px) !important; /* CHANGED: 한 줄로 통일, 강제 적용 */
  max-height: none !important; 
  
  display: flex;
  flex-direction: column;

  padding: 16px 0;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  box-shadow: 0 -4px 16px rgba(0,0,0,0.15);
  --dp-z-index: 3001;
  animation: modalUp 0.25s ease;
}

/* ── 가운데 컨텐츠 레이어: 모달 유형별 정책 ── */
.member-list {
  /* CHANGED: 컨텐츠 영역이 가운데를 채우도록 */
                          /* ▼ 가운데 영역 차지 */
  padding: 0 20px;
  margin-bottom: 16px;            /* 버튼과 간격 (통일) */

  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px 20px; 
}

/* 카테고리: 스크롤 없음 (내용 적음) */
.member-list--no-scroll {
  overflow: hidden;               /* CHANGED */
}

/* 참여자: 내부 스크롤 */
.member-list--scroll {
  overflow-y: auto;               /* CHANGED: flex:1로 충분, max-height 불필요 */
  overscroll-behavior: contain;

  /* Firefox */
  scrollbar-width: thin;
  scrollbar-color: #bbb #f0f0f0;
}

/* WebKit 스크롤바 (scoped에선 :deep 필요) */
:deep(.member-list--scroll::-webkit-scrollbar) { width: 8px; }                 /* CHANGED */
:deep(.member-list--scroll::-webkit-scrollbar-track) { background:#f0f0f0; border-radius:50px; }  /* CHANGED */
:deep(.member-list--scroll::-webkit-scrollbar-thumb) {
  background:#bbb; border-radius:50px; border:2px solid transparent; background-clip:padding-box; /* CHANGED */
}
:deep(.member-list--scroll::-webkit-scrollbar-thumb:hover) { background:#888; } /* CHANGED */

/* 날짜 피커를 가운데 컨텐츠로 동작시키기 */
.date-picker {
  /* CHANGED: 모달 내부 가운데 영역을 채움 */
  flex: 1;               /* ▼ 높이 통일시 가운데 공간 채우기 */
  width: calc(100% - 2rem);
  margin: 0 1rem;
  overflow: auto;        /* 달력이 커질 때 내부 스크롤 */
}

/* 타이틀 */
.modal-title { margin-left: 20px; }

/* 체크박스 행 */
.member-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 17px;
  color: #333333;
  user-select: none;
  line-height: 1.2;
  cursor: pointer;
  padding: 0px;
  margin: 0;
}

/* 커스텀 체크박스 */
.member-item input[type='checkbox']{
  appearance:none; 
  -webkit-appearance:none; 
  -moz-appearance:none;
  width:18px; 
  height:18px;
  border:2px solid #ffd166; border-radius:4px;
  background:#fff; cursor:pointer; position:relative; margin:0;
  transition: background-color .2s, border-color .2s, box-shadow .2s;
}
.member-item input[type='checkbox']:hover { box-shadow: 0 0 0 3px rgba(255,209,102,.25); }
.member-item input[type='checkbox']:focus-visible { outline:none; box-shadow: 0 0 0 3px rgba(255,209,102,.45); }
.member-item input[type='checkbox']:checked { background:#ffd166; border-color:#ffd166; }
.member-item input[type='checkbox']:checked::after{
  content:''; position:absolute; top:50%; left:50%; width:5px; height:9px;
  border: solid #fff; border-width:0 2px 2px 0; transform: translate(-50%,-60%) rotate(45deg);
}

/* 애니메이션 */
@keyframes modalUp { from { bottom:-300px; opacity:0; } to { bottom:0; opacity:1; } }
.slide-up-enter-active, .slide-up-leave-active { transition: transform .5s ease, opacity .5s ease; }
.slide-up-enter-from, .slide-up-leave-to { transform: translateY(100%); opacity:0; }
.slide-up-enter-to, .slide-up-leave-from { transform: translateY(0); opacity:1; }
</style>
