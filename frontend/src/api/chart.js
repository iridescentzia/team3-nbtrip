import api from '@/api';

const BASE_URL = '/api/chart';

export default {
  async getDonut(tripId) {
    const { data } = await api.get(`/api/chart/${tripId}/donut`);
    return data;
  },
  async getLine(tripId) {
    const { data } = await api.get(`/api/chart/${tripId}/line`);
    return data;
  },
};
