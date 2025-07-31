<template>
  <div class="filter-bar">
    <RotateCcw class="reset-icon" />

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

  <div v-if="isDateModalOpen" class="overlay" @click.self="closeDateModal">
    <transition name="slide-up" appear>
      <div class="bottom-modal">
        <h3 class="modal-title">날짜 선택</h3>
        <input type="date" v-model="dateRange.start" />
        <input type="date" v-model="dateRange.end" />
        <Button @click="applyDateFilter" label="조회하기"></Button>
      </div>
    </transition>
  </div>

  <div
    v-if="isCategoryModalOpen"
    class="overlay"
    @click.self="closeCategoryModal"
  >
    <transition name="slide-up" appear>
      <div class="bottom-modal">
        <h3 class="modal-title">카테고리</h3>
        <select v-model="selectedCategory">
        <option disabled value="">카테고리 선택</option>
        <option value="식비">식비</option>
        <option value="교통">교통</option>
        <option value="숙박">숙박</option>
        <option value="관광">관광</option>
        <option value="기타">기타</option>
      </select>
        <Button @click="applyCategoryFilter" label="조회하기"></Button>
      </div>
    </transition>
  </div>

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
            v-for="member in tripStore.currentTripMemberNicknames"
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
import { useTripStore } from '@/stores/trip';
import Button from '@/components/common/Button.vue';

const tripStore = useTripStore();

const selectedMembers = ref([]);


// 필터 적용 여부 상태 관리
const isDateModalOpen = ref(false);
const isParticipantModalOpen = ref(false);
const isCategoryModalOpen = ref(false);

const dateRange = ref({ start: '', end: '' });
const categoryFilter = ref(null);
const selectedParticipants = ref([]); // 복수 선택 가능



const isDateFiltered = computed(() => {
  return dateRange.value.start !== '' || dateRange.value.end !== '';
});

// const isCategoryFiltered = computed(() => categoryFilter.value !== null);
// const isParticipantsFiltered = computed(() => participantsFilter.value.length > 0);


const toggleDateModal = () => {
  isDateModalOpen.value = !isDateModalOpen.value;
};

const closeDateModal = () => {
  isDateModalOpen.value = false;
};

const payments = ref([]); // 전체 결제 내역
const filteredPayments = ref([]); // 필터링된 결과


// 선택된 날짜 필터링
const applyDateFilter = () => {
  console.log("dateRange:", dateRange.value);

  const start = dateRange.value.start;
  const end = dateRange.value.end;

  if(!start && !end){
    filteredPayments.value = payments.value; // 필터 안 함
    closeDateModal();
    return;
  }

  filteredPayments.value = payments.value.filter(payment => {
    const payAtDate = payment.pay_at.spl
  })
  closeDateModal();
};


const toggleCategoryModal = async () => {
  isCategoryModalOpen.value = !isCategoryModalOpen.value;
};

const closeCategoryModal = () => {
  isCategoryModalOpen.value = false;
};


const applyCategoryFilter = () => {
  // 선택된 결제 참여자 필터링 로직 추가
  closeCategoryModal();
};

const toggleParticipantModal = async () => {
  isParticipantModalOpen.value = !isParticipantModalOpen.value;
};

const closeParticipantModal = () => {
  isParticipantModalOpen.value = false;
};

const applyParticipantFilter = () => {
  // 선택된 결제 참여자 필터링 로직 추가
  closeParticipantModal();
};

onMounted(async () => {
  await tripStore.fetchTrips();
  await tripStore.fetchCurrentTripMembers();
  await tripStore.fetchCurrentTripMemberNicknames();
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
</style>
