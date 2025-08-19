<script setup>
import { ref, watch, onMounted } from 'vue';
import { getReportComments } from '@/api/openaiApi';
import { useRoute } from 'vue-router';

const route = useRoute();
const tripId = Number(route.params.tripId);
const activeTab = defineModel('activeTab'); // 'category' | 'date'

const comments = ref({ category: '', date: '' });
const loading = ref(true);
const error = ref('');

const loadComments = async () => {
  loading.value = true;
  error.value = '';
  try {
    const res = await getReportComments(tripId); // { category, date }
    // 혹시 문자열로 온 경우 대비해 방어 코드
    comments.value =
      typeof res === 'string'
        ? JSON.parse(res)
        : { category: res.category ?? '', date: res.date ?? '' };

    // 기본 탭 보정
    if (activeTab.value !== 'date' && activeTab.value !== 'category') {
      activeTab.value = 'category';
    }
  } catch (e) {
    console.error(e);
    error.value = 'AI 분석을 불러오는 데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

onMounted(loadComments);
</script>

<template>
  <div class="ai-report">
    <h3>AI 소비 분석</h3>

    <p v-if="loading">AI 분석 불러오는 중...</p>
    <p v-else-if="error">{{ error }}</p>
    <p v-else>
      {{ activeTab === 'date' ? comments.date : comments.category }}
    </p>
  </div>
</template>

<style scoped>
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
