import api from '@/api'; // src/api/index.js의 export default

export default {
  // 전체 결제 내역 불러오기
  async getPaymentList(tripId) {
    const { data } = await api.get(`/paymentlist/${tripId}`);
    console.log("payment data: ", data);
    return data;
  },
  
  // 단건 결제 내역 불러오기
    async getPaymentListById(paymentId) {
    const { data } = await api.get(`/paymentlist/payment/${paymentId}`);
    console.log("payment data: ", data);
    return data;
  },
};
