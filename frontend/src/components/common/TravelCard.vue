<script setup>
import { ref, computed } from 'vue';
import { FileChartColumn, Trash2 } from 'lucide-vue-next';
import { useRouter } from 'vue-router';

const props = defineProps({
  tripName: String,
  startDate: String,
  endDate: String,
});

const formattedDate = computed(() => {
  const start = props.startDate?.replaceAll('-', '.') ?? '';
  const end = props.endDate?.replaceAll('-', '.') ?? '';
  return start && end ? `${start} ~ ${end}` : '';
});

const tabs = ['그룹 지출 내역', '선결제 내역', '그룹 관리'];
const activeTab = ref(tabs[0]);

function selectTab(tab) {
  activeTab.value = tab;
}

// 아이콘 클릭 시 차트 페이지로 이동
function goToChart() {
  router.push({
    name: 'chart',
    params: { tripId: 1 },
  });
}
</script>

<template>
  <div class="travel-card">
    <!-- 카드 헤더 -->
    <div class="card-header">
      <!-- 왼쪽: 여행 제목 + 날짜 -->
      <div class="title-date">
        <div class="title-row">
          <div class="trip-name">{{ props.tripName }}</div>
          <FileChartColumn class="icon" @click="goToChart" />
        </div>
        <div class="trip-date">{{ formattedDate }}</div>
      </div>

      <!-- 오른쪽: 휴지통 아이콘 -->
      <Trash2 class="icon trash-icon" />
    </div>

    <!-- 카드 구분선 -->
    <div class="divider"></div>

    <!-- 탭 목록 -->
    <div class="tab-list">
      <div
        v-for="tab in tabs"
        :key="tab"
        class="tab-item"
        :class="{ active: activeTab === tab }"
        @click="selectTab(tab)"
      >
        {{ tab }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.travel-card {
  width: 100%;
  max-width: 343px;
  margin: 0 auto 1rem auto;
  padding: 0;
  box-sizing: border-box;
  border-radius: 16px;
  font-family: 'IBM Plex Sans KR', sans-serif;
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  background: white;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);

}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
  padding: 16px;
}

.title-date {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title-row {
  display: flex;
  align-items: center;
  /*gap: 8px;*/
  /* justify-content: space-between; */
}

.trip-name {
  color: #000;
  font-size: 24px;
  font-weight: 1000;
  margin: 0;
  margin-right:5px;
}

.trip-date {
  color: #6D6D6D;
  font-size: 15px;
  opacity: 0.8;
}

.icon {
  width: 20px;
  height: 20px;
  color: #666;
  cursor: pointer;
  transition: color 0.2s;
}

.icon:hover {
  color: #333;
}

.trash-icon {
  margin-top: 8px;
}

.divider {
  width: 100%;
  height: 1px;
  background-color: #666;
  margin: 8px 0;
  opacity: 0.1;
}

.tab-list {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  padding: 0 16px 8px 16px;
}

.tab-item {
  flex: 1;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
  white-space: nowrap;
  cursor: pointer;
  transition: background-color 0.2s;
  color: #575757;
}

.tab-item:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.tab-item.active {
  color: #FDB100;
}
</style>
