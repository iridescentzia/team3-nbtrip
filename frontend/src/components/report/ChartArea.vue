<template>
  <div class="travel-report p-4">
    <h2 class="text-xl font-semibold mb-4">여행 리포트</h2>
    <div class="tabs flex border-b mb-4">
      <button
        :class="[
          'tab px-4 py-2 -mb-px',
          activeTab === 'category'
            ? 'border-b-2 border-blue-500 text-blue-500'
            : 'text-gray-600',
        ]"
        @click="switchTab('category')"
      >
        카테고리별
      </button>
      <button
        :class="[
          'tab px-4 py-2 -mb-px',
          activeTab === 'date'
            ? 'border-b-2 border-blue-500 text-blue-500'
            : 'text-gray-600',
        ]"
        @click="switchTab('date')"
      >
        날짜별
      </button>
    </div>

    <div class="chart-container mb-6">
      <Doughnut
        v-if="activeTab === 'category'"
        :data="categoryData"
        :options="categoryOptions"
      />
      <Line v-else :data="dateData" :options="dateOptions" />
    </div>

    <div class="ai-report bg-white shadow rounded p-4">
      <h3 class="font-medium mb-2">AI 분석 리포트</h3>
      <p v-if="aiReport" class="text-gray-700">{{ aiReport }}</p>
      <p v-else class="text-gray-500">분석 결과를 불러오는 중입니다...</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue';
import { Doughnut, Line } from 'vue-chartjs';
import {
  Chart,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
} from 'chart.js';
import axios from 'axios';

Chart.register(
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
  setup() {
    const activeTab = ref('category');

    // 더미 데이터
    const categoryData = {
      labels: ['교통', '숙박', '식비', '관광', '쇼핑', '기타'],
      datasets: [
        {
          data: [300, 150, 200, 100, 80, 70],
          backgroundColor: [
            '#4CAF50',
            '#FFC107',
            '#2196F3',
            '#FF5722',
            '#9C27B0',
            '#607D8B',
          ],
        },
      ],
    };

    const categoryOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'right' },
      },
    };

    const dateLabels = Array.from({ length: 10 }, (_, i) => `${i + 1}일`);
    const dateData = {
      labels: dateLabels,
      datasets: [
        {
          label: '지출',
          data: [5, 3, 8, 2, 7, 4, 9, 6, 1, 10],
          fill: false,
          tension: 0.4,
        },
        {
          label: '수입',
          data: [2, 6, 4, 7, 1, 5, 3, 8, 9, 2],
          fill: false,
          tension: 0.4,
        },
      ],
    };

    const dateOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'top' },
      },
      scales: {
        y: { beginAtZero: true },
      },
    };

    const aiReport = ref('');

    const fetchAIReport = async () => {
      try {
        const payload = {
          tab: activeTab.value,
          data:
            activeTab.value === 'category'
              ? // 카테고리별: labels와 단일 값 배열로 매핑
                categoryData.labels.map((label, i) => ({
                  label,
                  values: [categoryData.datasets[0].data[i]],
                }))
              : // 날짜별: 각 날짜마다 지출/수입 값을 묶어서 매핑
                dateData.labels.map((label, i) => ({
                  label,
                  values: dateData.datasets.map((ds) => ds.data[i]),
                })),
        };
        const res = await axios.post('/api/ai-analysis', payload);
        aiReport.value = res.data.report;
      } catch (err) {
        aiReport.value = 'AI 분석을 불러오는 중.....';
      }
    };

    // 탭 변경 시 다시 분석
    watch(activeTab, () => {
      aiReport.value = '';
      fetchAIReport();
    });

    onMounted(() => {
      fetchAIReport();
    });

    const switchTab = (tab) => {
      activeTab.value = tab;
    };

    return {
      activeTab,
      switchTab,
      categoryData,
      categoryOptions,
      dateData,
      dateOptions,
      aiReport,
    };
  },
};
</script>

<style scoped>
.travel-report {
  max-width: 400px;
  margin: auto;
}
.chart-container {
  height: 250px;
}
.tabs .tab {
  cursor: pointer;
}
.ai-report {
  min-height: 100px;
}
</style>
