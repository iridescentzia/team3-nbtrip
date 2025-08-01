<template>
  <div class="ai-report">
    <h3>AI 분석 리포트</h3>
    <p v-if="loading">분석 중입니다...</p>
    <p v-else>{{ comment }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { getAIComment } from '@/api/openaiApi';

// ✅ props 추가 (탭 정보)
const props = defineProps({
  activeTab: {
    type: String,
    required: true, // 'category' or 'date'
  },
});

const route = useRoute();
const tripId = route.params.tripId;

const loading = ref(true);
const comment = ref('');
const donutData = ref([]);
const lineData = ref([]);

// 🧠 도넛 데이터 포맷팅
function formatDonutData(data) {
  return data.reduce((acc, item) => {
    if (item?.category && typeof item.total_amount === 'number') {
      acc[item.category] = item.total_amount;
    }
    return acc;
  }, {});
}

// 🧠 라인 하이라이트 추출
function extractLineHighlights(data) {
  const valid = data.filter((d) => d?.total_amount > 0);
  if (!valid.length) return null;

  const sorted = [...valid].sort((a, b) => b.total_amount - a.total_amount);
  return {
    highest: sorted[0],
    lowest: sorted[sorted.length - 1],
  };
}

// 🧠 AI 프롬프트 생성 및 요청
async function generateAIReport() {
  loading.value = true;
  comment.value = '';

  try {
    // 데이터 없으면 먼저 호출
    if (donutData.value.length === 0 || lineData.value.length === 0) {
      const res = await axios.get(`/api/openai/${tripId}`);
      donutData.value = res.data.donutData || [];
      lineData.value = res.data.lineData || [];
    }

    let prompt = `다음은 사용자의 여행 지출 데이터입니다.\n`;

    // ✅ 탭에 따라 다른 데이터 기반 분석
    if (props.activeTab === 'category') {
      const donutJson = formatDonutData(donutData.value);
      if (Object.keys(donutJson).length === 0) {
        comment.value = '분석할 도넛 차트 데이터가 없습니다.';
        return;
      }
      prompt += `1. 항목별 지출: ${JSON.stringify(donutJson)}\n`;
    } else if (props.activeTab === 'date') {
      const highlight = extractLineHighlights(lineData.value);
      if (!highlight) {
        comment.value = '분석할 라인 차트 데이터가 없습니다.';
        return;
      }
      prompt += `2. 날짜별 최고 지출: ${highlight.highest.date} (${highlight.highest.total_amount}원), `;
      prompt += `최저 지출: ${highlight.lowest.date} (${highlight.lowest.total_amount}원)\n`;
    }

    prompt += `이 데이터를 분석해 간단한 한국어 코멘트를 150자 이내로 작성해주세요.\n`;

    const result = await getAIComment(prompt);
    comment.value =
      typeof result === 'string'
        ? result
        : typeof result?.data === 'string'
        ? result.data
        : '분석 결과를 가져오지 못했습니다.';
  } catch (e) {
    console.error('AI 분석 실패:', e);
    comment.value = '분석에 실패했습니다.';
  } finally {
    loading.value = false;
  }
}

// 🧠 초기 실행
onMounted(generateAIReport);

// 🧠 탭 변경 시 재실행
watch(() => props.activeTab, generateAIReport);
</script>

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
