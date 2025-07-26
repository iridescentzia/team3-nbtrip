import api from '@/api';

const BASE_URL = '/api/merchants';

export default {
  async get(id) {
    const { data } = await api.get(`${BASE_URL}/${id}`);
    console.log('MERCHANT GET', data);
    return data;
  },
};
