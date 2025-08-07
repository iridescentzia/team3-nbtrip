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

  // 기타 결제 수정
  async updateOtherPayment(paymentId, payload) {
    return await api.put(`/payments/other/${paymentId}`, payload);
  },

    // 선결제 수정
  async updatePrepaidPayment(paymentId, payload) {
    return await api.put(`/payments/prepaid/${paymentId}`, payload);
  },

    // qr 결제 수정
  async updateQrPayment(paymentId, payload) {
    return await api.put(`/payments/qr/${paymentId}`, payload);
  },

    // 결제 참여자 리스트 조회
  async getParticipantsByPaymentId(paymentId) {
    const { data } = await api.get(`${BASE_URL}/${paymentId}/participants`);
    return data; // [{ participantId, paymentId, userId, splitAmount }, ...]
  },
  
  // 결제 내역 삭제
  async deletePayment(paymentId){
    return api.delete(`${BASE_URL}/${paymentId}`)
  }

};
