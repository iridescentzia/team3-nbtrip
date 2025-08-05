import api from '@/api';

const BASE_URL = '/payments';

export default {
  async create(payload) {
    const { data } = await api.post(`${BASE_URL}/qr`, payload);
    console.log('PAYMENT CREATE', data);
    return data;
  },

  // 기타 결제 등록
  async createOther(payload) {
    const { data } = await api.post(`${BASE_URL}/other`, payload);
    console.log('OTHER PAYMENT CREATE', data);
    return data;
  },

  async updateOtherPayment(paymentId, payload) {
    return await api.put(`/payments/other/${paymentId}`, payload);
  }

};
