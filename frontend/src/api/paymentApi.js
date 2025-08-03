import api from '@/api';

const BASE_URL = '/payments';

export default {
  async create(payload) {
    const { data } = await api.post(`${BASE_URL}/qr`, payload);
    console.log('PAYMENT CREATE', data);
    return data;
  },
};
