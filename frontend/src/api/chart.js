import axios from 'axios';

const chartApi = {
  getChart(tripId) {
    return axios
      .get(`/api/report/${tripId}`) // 토큰 없음
      .then((res) => res.data);
  },
};

export default chartApi;
