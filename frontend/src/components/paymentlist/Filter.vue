<template>
  <div class="filter-bar">
    <RotateCcw class="reset-icon" />

    <button
      class="filter-button"
      :class="{ active: isDateModalOpen }"
      @click="toggleDateModal"
    >
      <div class="filter-row">
        <div class="filter-text">날짜</div>
        <ChevronDown class="down-icon" />
      </div>
    </button>

    <button class="filter-button">
      <div class="filter-row">
        <div class="filter-text">카테고리</div>
        <ChevronDown class="down-icon" />
      </div>
    </button>

    <button
      class="filter-button"
      :class="{ active: isParticipantModalOpen }"
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
        <h3>날짜 선택</h3>
        <input type="date" v-model="dateRange.start" />
        <input type="date" v-model="dateRange.end" />
        <button @click="applyDateFilter">적용</button>
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
        <h3>결제 참여자</h3>
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
        <button @click="applyParticipantFilter">적용</button>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ChevronDown, RotateCcw } from 'lucide-vue-next';
import { useTripStore } from '@/stores/trip';

const tripStore = useTripStore();

const selectedMembers = ref([]);

const isDateModalOpen = ref(false);
const isParticipantModalOpen = ref(false);
const dateRange = ref({ start: '', end: '' });

const toggleDateModal = () => {
  isDateModalOpen.value = !isDateModalOpen.value;
};

const closeDateModal = () => {
  isDateModalOpen.value = false;
};

const applyDateFilter = () => {
  console.log(dateRange.value);
  // 선택된 날짜 필터링 로직 추가
  closeDateModal();
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
  background-color: rgb(71, 60, 60);
  color: white;
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
  padding: 20px;
  border-top-left-radius: 20px;
  border-top-right-radius: 20px;
  z-index: 20;
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
