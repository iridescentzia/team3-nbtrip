import apiClient from '@/api/index.js';

// 리포트 진입 시 한 번에 두 코멘트 가져오기
export async function getReportComments(tripId) {
  try {
    const [catRes, dateRes] = await Promise.all([
      apiClient.get(`/openai/${tripId}`),
      apiClient.get(`/openai/${tripId}/date`),
    ]);

    // 프로젝트에 따라 {comment} 또는 {data:{comment}}일 수 있어요.
    const cat = catRes?.data?.comment ?? catRes?.data?.data?.comment ?? '';
    const date = dateRes?.data?.comment ?? dateRes?.data?.data?.comment ?? '';

    return { category: cat, date };
  } catch (err) {
    // 네트워크/401/500 구분에 도움
    console.error('[getReportComments] failed:', err?.response || err);
    throw err;
  }
}

export async function refreshReportComments(tripId) {
  return getReportComments(tripId);
}
