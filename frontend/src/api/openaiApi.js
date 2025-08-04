import apiClient from '@/api/index.js';

export const getAIComment = async (tripId, type = 'category') => {
  try {
    const url =
      type === 'category' ? `/openai/${tripId}` : `/openai/${tripId}/date`;

    const response = await apiClient.get(url);
    return response.data.comment;
  } catch (error) {
    console.error('AI 분석 코멘트 가져오기 실패:', error);
    return 'AI 분석에 실패했습니다.';
  }
};
