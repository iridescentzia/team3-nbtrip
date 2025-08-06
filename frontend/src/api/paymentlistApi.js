import api from '@/api'; // src/api/index.js의 export default

export default {
  async getPaymentList(tripId) {
    const { data } = await api.get(`/paymentlist/${tripId}`);
    console.log("payment data: ", data);
    return data;
  },
    async getPaymentListById(paymentId) {
    const { data } = await api.get(`/paymentlist/payment/${paymentId}`);
    console.log("payment data: ", data);
    return data;
  },
};
