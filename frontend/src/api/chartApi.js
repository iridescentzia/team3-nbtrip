import axios from 'axios';

const chartApi = {
  getChart(tripId) {
    return axios
      .get(`/api/report/${tripId}`) // 백엔드에서 제공하는 API
      .then((res) => res.data); // data만 추출해서 반환
  },
};

export default chartApi;
