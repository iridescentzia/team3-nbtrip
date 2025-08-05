import apiClient from '@/api/index.js';

const chartApi = {
  getChart(tripId) {
    return apiClient
      .get(`/report/${tripId}`) // 백엔드에서 제공하는 API
      .then((res) => res.data); // data만 추출해서 반환
  },
};

export default chartApi;
