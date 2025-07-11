import api from '@/api';

const BASE_URL = '/users';

export default {
  async get(id) {
    const { data } = await api.get(`${BASE_URL}/${id}`);
    console.log('USER GET', data);
    return data;
  },
};
