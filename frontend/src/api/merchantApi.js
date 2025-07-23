import api from '@/api';

const BASE_URL = '/api/merchants';

export default {
  async get(no) {
    const { data } = await api.get(`${BASE_URL}/${no}`);
    console.log('MERCHANT GET', data);
    return data;
  },
};
