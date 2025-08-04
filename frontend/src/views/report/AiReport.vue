<script setup>
import { ref, watch, onMounted } from 'vue';
import { getAIComment } from '@/api/openaiApi';
import { useRoute } from 'vue-router';

const route = useRoute();
const tripId = Number(route.params.tripId);
const activeTab = defineModel('activeTab'); // category 또는 date

const comment = ref('AI 분석 중입니다...');

const loadComment = async () => {
  comment.value = 'AI 분석 중입니다...';

  try {
    const result = await getAIComment(tripId, activeTab.value); // 탭에 따라 다르게 호출
    comment.value = result;
  } catch (err) {
    comment.value = 'AI 분석에 실패했습니다.';
  }
};

onMounted(loadComment);
watch(() => activeTab.value, loadComment);
</script>
<template>
  <div class="ai-report">
    <h3>AI 소비 분석</h3>
    <p>{{ comment }}</p>
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
