<template>
  <Header />
  <div class="travel-report-page">
    <!-- 헤더 -->
    <div class="report-info-box">
      <div class="report-header">
        <h1 class="trip-name">{{ tripName }}</h1>
        <p class="trip-date">{{ startDate }} ~ {{ endDate }}</p>
      </div>

      <div class="tab-menu">
        <button
          :class="['tab-button', activeTab === 'category' ? 'active' : '']"
          @click="switchTab('category')"
        >
          카테고리별
        </button>
        <button
          :class="['tab-button', activeTab === 'date' ? 'active' : '']"
          @click="switchTab('date')"
        >
          날짜별
        </button>
      </div>
    </div>

    <!-- 차트 영역 -->
    <div class="chart-area">
      <Doughnut
        v-if="activeTab === 'category'"
        :data="categoryData"
        :options="categoryOptions"
      />
      <Line
        v-else-if="activeTab === 'date'"
        :data="dateData"
        :options="dateOptions"
      />
    </div>

    <!-- AI 분석 -->
    <AIReport :tripId="tripId" :activeTab="activeTab" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Doughnut, Line } from 'vue-chartjs';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
} from 'chart.js';
import { useRoute } from 'vue-router';
import chartApi from '@/api/chartApi';
import Header from '@/components/layout/Header.vue';
import AIReport from './AiReport.vue';

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement
);

// 경로에서 tripId 추출
const route = useRoute();
const tripId = ref(Number(route.params.tripId));

// 여행 정보
const tripName = ref('');
const startDate = ref('');
const endDate = ref('');

// 탭 상태
const activeTab = ref('category');

// 차트 데이터
const categoryData = ref({ labels: [], datasets: [] });
const dateData = ref({ labels: [], datasets: [] });

// AI 분석 리포트
const aiReport = ref('');

// 차트 옵션
const categoryOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { position: 'right' } },
};
const dateOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'top' },
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        // ✅ 눈금을 1씩 증가하도록 설정
        stepSize: 1,
        // ✅ 소수점 없이 정수만 표시
        callback: function (value) {
          return Number.isInteger(value) ? value : null;
        },
      },
    },
  },
};

// 탭 전환 함수
function switchTab(tab) {
  activeTab.value = tab;
}

// 데이터 로딩
onMounted(async () => {
  try {
    const resp = await chartApi.getChart(tripId.value);
    console.log('api 불러오는중');

    // 여행 정보 표시용
    tripName.value = resp.tripData.tripName;
    startDate.value = resp.tripData.startDate;
    endDate.value = resp.tripData.endDate;

    // 도넛 차트 데이터 세팅
    categoryData.value = {
      labels: resp.donutData.map((d) => d.category),
      datasets: [
        {
          label: '카테고리별 지출',
          data: resp.donutData.map((d) => d.total_amount),
          backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56',
            '#4BC0C0',
            '#9966FF',
            '#FF9F40',
          ],
        },
      ],
    };

    // 라인 차트 데이터 세팅
    dateData.value = {
      labels: resp.lineData.map((d) => d.date),
      datasets: [
        {
          label: '건수',
          data: resp.lineData.map((d) => d.count),
          fill: false,
          tension: 0.4,
        },
        {
          label: '총 지출',
          data: resp.lineData.map((d) => d.totalAmount),
          fill: false,
          tension: 0.4,
        },
      ],
    };
  } catch (err) {
    console.error('차트 데이터 로드 실패', err);
  }
});
</script>

<style scoped>
.report-info-box {
  background: #fff;
}
.travel-report-page {
  max-width: 414px;
  margin: 0 auto;
  margin-top: 56px;
  background-color: #f9fafb;
  min-height: 100vh;
  box-sizing: border-box;
}

.report-header {
  text-align: center;
  margin-bottom: 16px;
}

.trip-name {
  font-size: 22px;
  font-weight: bold;
  margin: 0;
}

.trip-date {
  font-size: 14px;
  color: #888;
  margin-top: 4px;
}

.tab-menu {
  display: flex;
  justify-content: space-around;
  margin-bottom: 0; /* 여백 제거 */
  padding-bottom: 0; /* 패딩 제거 */
  border-bottom: none; /* ❌ border 제거 */
}

.tab-button {
  flex: 1;
  padding: 10px 0;
  font-size: 15px;
  border: none;
  background: none;
  color: #666;
  font-weight: 500;
  border-bottom: 2px solid transparent; /* 초기엔 투명 border로 정렬 유지 */
  border-top: 1px;
  cursor: pointer;
}

.tab-button.active {
  color: #f59e0b;
  border-bottom-color: #f59e0b;
}

.chart-area {
  height: 300px;
  margin-bottom: 24px;
  margin-top: 30px;
}

.ai-report {
  background: #fff;
  border-radius: 12px;
  margin: 20px 16px;
  padding: 20px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  font-size: 14px;
  color: #333;
}

.ai-report h3 {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 8px;
}
</style>
