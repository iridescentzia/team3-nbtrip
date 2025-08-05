import api from '@/api';

const BASE_URL = '/merchants';

export default {
  async get(id) {
    const { data } = await api.get(`${BASE_URL}/${id}`);
    console.log('MERCHANT GET', data);
    return data;
  },

  // 전체 카테고리 목록 조회
  async getAllMerchantCategories(){
    const { data } = await api.get(`${BASE_URL}/category`);
    console.log('GET ALL MERCHANT CATEGORIES', data);
    return data;
  }
};
