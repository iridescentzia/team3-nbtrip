import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePaymentStore = defineStore('payment', () => {
  const merchantID = ref(null);
  const merchantName = ref('');

  const tripName = ref('');
  const participantsId = ref([]);

  const amount = ref(0);
  const reason = ref('');

  const payment = ref(null);

  const isModalVisible = ref(false);
  const modalType = ref(1);

  function setScannerData({
    merchantID: id,
    merchantName: name,
    tripName: trip,
    participants,
  }) {
    merchantID.value = id;
    merchantName.value = name;
    tripName.value = trip;
    participantsId.value = participants;
    isModalVisible.value = true;
    modalType.value = 1;
  }

  function resetModal() {
    isModalVisible.value = false;
    modalType.value = 1;
    amount.value = 0;
    reason.value = '';
    payment.value = null;
  }

  return {
    merchantID,
    merchantName,
    tripName,
    participantsId,
    isModalVisible,
    modalType,
    amount,
    reason,
    payment,
    setScannerData,
    resetModal,
  };
});
