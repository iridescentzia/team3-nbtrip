<template>
  <!-- 헤더 -->
  <div class="header-content">
    <h1 class="title">
      {{ tripName }}
      <path d="M3 3h4v18H3V3zm6 6h4v12h-4V9zm6-3h4v15h-4V6z" />
    </h1>
    <p class="dates">{{ startDate }} &nbsp;~&nbsp; {{ endDate }}</p>
  </div>

  <!-- 탭 & 차트 -->
  <div class="travel-report p-4">
    <!-- 탭 -->
    <div class="tabs flex border-b mb-4">
      <button
        class="tab px-4 py-2 -mb-px"
        :class="
          activeTab === 'category'
            ? 'border-b-2 border-blue-500 text-blue-500'
            : 'text-gray-600'
        "
        @click="switchTab('category')"
      >
        카테고리별
      </button>
      <button
        class="tab px-4 py-2 -mb-px"
        :class="
          activeTab === 'date'
            ? 'border-b-2 border-blue-500 text-blue-500'
            : 'text-gray-600'
        "
        @click="switchTab('date')"
      >
        날짜별
      </button>
    </div>

    <!-- 차트 -->
    <div class="chart-container mb-6">
      <Doughnut
        v-if="activeTab === 'category' && status === 'CLOSED'"
        :data="categoryData"
        :options="categoryOptions"
      />
      <Line
        v-else-if="activeTab === 'date' && status === 'CLOSED'"
        :data="dateData"
        :options="dateOptions"
      />
      <div v-else class="chart-placeholder">차트가 준비 중입니다.</div>
    </div>

    <!-- AI 리포트 -->
    <div class="ai-report bg-white shadow rounded p-4">
      <h3 class="font-medium mb-2">AI 분석 리포트</h3>
      <p v-if="aiReport" class="text-gray-700">{{ aiReport }}</p>
      <p v-else class="text-gray-500">분석 결과를 불러오는 중입니다...</p>
    </div>
  </div>
</template>

<script>
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
import chartApi from '@/api/chart';

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement
);

export default {
  name: 'ChartArea',
  components: { Doughnut, Line },
  props: {
    tripId: {
      type: [String, Number],
      required: false,
    },
  },
  setup(props) {
    // props.tripId 또는 query 로드
    const route = useRoute();
    const id = Number(props.tripId ?? route.query.tripId ?? 1);

    // 화면용 정보 (props 또는 API 로딩 시 채워도 됩니다)
    const tripName = ref('서울 우정 여행');
    const startDate = ref('2025.07.10');
    const endDate = ref('2025.07.12');

    // API 상태 & 탭
    const status = ref('');
    const activeTab = ref('category');

    // Chart.js 데이터 refs
    const categoryData = ref({ labels: [], datasets: [] });
    const dateData = ref({ labels: [], datasets: [] });
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
      plugins: { legend: { position: 'top' } },
      scales: { y: { beginAtZero: true } },
    };

    onMounted(async () => {
      try {
        // 1) 백엔드에서 status, donutData, lineData를 한 번에 가져옴
        const resp = await chartApi.getDonut(tripId);
        const response = await chartApi.getLine(tripId);
        status.value = resp.status;

        // 2) 도넛 데이터 세팅
        categoryData.value = {
          labels: resp.donutData.map((d) => d.category),
          datasets: [{ data: resp.donutData.map((d) => d.totalAmount) }],
        };
        // 3) 라인 데이터 세팅
        dateData.value = {
          labels: response.lineData.map((d) => d.date),
          datasets: [
            {
              label: '건수',
              data: response.lineData.map((d) => d.count),
              fill: false,
              tension: 0.4,
            },
            {
              label: '총 지출',
              data: response.lineData.map((d) => d.totalAmount),
              fill: false,
              tension: 0.4,
            },
          ],
        };
        // 4) AI 리포트 (예시)
        aiReport.value =
          '전체 지출 중 교통비가 40%로 가장 큰 비중을 차지합니다. 다음 여행에서는 교통비 절감을 위해 대중교통 패스를 고려해보세요.';
      } catch (err) {
        console.error('차트 데이터 로드 실패', err);
        status.value = 'ERROR';
      }
    });

    function switchTab(tab) {
      activeTab.value = tab;
    }

    return {
      tripName,
      startDate,
      endDate,
      status,
      activeTab,
      categoryData,
      dateData,
      categoryOptions,
      dateOptions,
      aiReport,
      switchTab,
    };
  },
};
</script>

<style scoped>
/* 기존 CSS 그대로 유지 + chart-placeholder 추가 */
.travel-report {
  max-width: 400px;
  margin: 0 auto;
  padding: 16px;
}
.tabs {
  display: flex;
  margin-bottom: 1rem;
  border-bottom: none;
}
.tab {
  flex: 1;
  text-align: center;
  padding: 0.5rem 1rem;
  cursor: pointer;
  color: #4b5563;
  border: none;
}
.tab.border-b-2 {
  border-bottom: 2px solid #3b82f6;
}
.tab.text-blue-500 {
  color: #3b82f6;
}
.chart-container {
  height: 250px;
  margin-bottom: 1.5rem;
}
.chart-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 16px;
}
.ai-report {
  background: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border-radius: 0.375rem;
  padding: 16px;
  text-align: center;
}
</style>
