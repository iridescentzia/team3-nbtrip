<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Header from '@/components/layout/Header.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import { useTripStore } from '@/stores/trip';
import paymentlistApi from '@/api/paymentlistApi';
import paymentApi from '@/api/paymentApi';

const route = useRoute();
const router = useRouter();
const paymentId = route.params.paymentId;
const tripStore = useTripStore();

const paymentType = ref('');
const date = ref(new Date());
const form = ref({
  content: '',
  amount: '',
  category: '',
  payerUserId: '',
  memo: ''
});
const selectedMembers = ref([]);

// 선택 여부 확인
const isSelected = (userId) => {
  return selectedMembers.value.some(p => p.userId === userId);
};

// 토글 ON/OFF
const toggleMember = (userId) => {
  const index = selectedMembers.value.findIndex(p => p.userId === userId);
  if (index === -1) {
    selectedMembers.value.push({ userId, splitAmount: 0 }); // 새로 추가
  } else {
    selectedMembers.value.splice(index, 1); // 제거
  }
};

// 현재 금액 가져오기
const getSplitAmount = (userId) => {
  const member = selectedMembers.value.find(p => p.userId === userId);
  return member ? member.splitAmount : 0;
};

// 금액 수정 처리
const updateSplitAmount = (userId, value) => {
  const member = selectedMembers.value.find(p => p.userId === userId);
  if (member) {
    member.splitAmount = Number(value);
  }
};

const formatAmountInput = () => {
  const numberOnly = form.value.amount.replace(/[^0-9]/g, '');
  form.value.amount = Number(numberOnly).toLocaleString();
};

const formatDateTime = (date) => {
  const pad = (n) => n.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

onMounted(async () => {
  await tripStore.fetchTrips();
  await tripStore.fetchCurrentTripMemberNicknames();
  await tripStore.fetchMerchantCategories();

  try {
    const data = await paymentlistApi.getPaymentListById(paymentId);
    const p = data.payment;
    paymentType.value = p.paymentType;
    console.log("paymentType: ", paymentType.value)

    const res = await paymentApi.getParticipantsByPaymentId(paymentId);
    selectedMembers.value = res.map(p => ({
      userId: p.userId,
      splitAmount: p.splitAmount
    }));
    console.log("selectedMembers: ", selectedMembers.value)

    form.value = {
      content: p.merchantName,
      amount: Number(p.amount).toLocaleString(),
      category: p.categoryId,
      payerUserId: p.userId,
      memo: p.memo
    };
    date.value = new Date(p.payAt);


  } catch (e) {
    console.error('결제 내역 불러오기 실패:', e);
  }
});

const handleSave = async () => {
  const amount = parseInt(form.value.amount.replace(/,/g, ''), 10);
  const tripId = tripStore.currentTrip?.tripId;

  if (!tripId || !form.value.content || !amount || !form.value.category || !form.value.payerUserId || selectedMembers.value.length === 0) {
    console.error('모든 항목을 입력해주세요.');
    return;
  }

  const payerUserId = Number(form.value.payerUserId);
  const isPayerIncluded = selectedMembers.value.some(p => Number(p.userId) === payerUserId);

  if (!isPayerIncluded) {
    selectedMembers.value.push({
      userId: payerUserId,
      splitAmount: 0
    });
  }


  const payAt = formatDateTime(date.value);
  const payload = {
    merchantId: Number(form.value.category),
    amount,
    memo: form.value.memo,
    paymentDate: payAt.split(' ')[0],
    paymentTime: payAt.split(' ')[1],
    paymentType: paymentType.value,
    participants: selectedMembers.value.map(p => ({
      userId: Number(p.userId),
      splitAmount: Number(p.splitAmount)
    }))
  };

  console.log("payload", payload);
  console.log("<< 참여자 userId 타입 체크 >>");
  selectedMembers.value.forEach(p =>
    console.log("userId:", p.userId, "typeof:", typeof p.userId)
  );

  try {
    if (paymentType.value === 'OTHER') {
      await paymentApi.updateOtherPayment(paymentId, payload);
    } else if (paymentType.value === 'PREPAID') {
      await paymentApi.updatePrepaidPayment(paymentId, payload);
    } else if (paymentType.value === 'QR') {
      await paymentApi.updateQrPayment(paymentId, payload);
    } else {
      throw new Error(`지원되지 않는 paymentType: ${paymentType.value}`);
    }

    console.log('결제 내역 수정 완료!');
    router.go(-1)
  } catch (e) {
    console.error('수정 실패:', e.response?.data || e);
  }
};
</script>

<template>
  <Header title="결제 상세 내역" @back="router.back"/>

  <!-- 메인 입력 영역 -->
  <main class="content-container">
    <!-- 내용 입력 -->
    <div class="form-section">
      <label class="form-label">내용</label>
      <div class="input-box">
        <input 
        class="input-text"
        v-model="form.content" 
        placeholder="지출 내용을 입력하세요" 
        :readonly="paymentType == 'QR'"
        />
      </div>
    </div>

    <!-- 금액 입력 -->
    <div class="form-section">
      <label class="form-label">금액</label>
      <div class="input-box">
        <input
          v-model="form.amount"
          class="input-text"
          placeholder="금액을 입력하세요"
          :readonly="paymentType == 'QR'"
          @input="formatAmountInput"
        />
        <span class="input-suffix">원</span>
      </div>
    </div>

    <!-- 날짜 입력 -->
    <div class="form-section">
      <label class="form-label">날짜</label>
      <div class="input-box date-picker">
        <VueDatePicker
          v-model="date"
          :enable-time-picker="true"
          format="yyyy-MM-dd HH:mm"
          class="date-picker"
        />
      </div>
    </div>

    <!-- 카테고리 선택 -->
    <div class="form-section">
      <label class="form-label">카테고리</label>
      <select v-model="form.category" class="select-box">
        <option
          v-for="category in tripStore.merchantCategories"
          :key="category.categoryId"
          :value="category.categoryId"
        >
          {{ category.categoryName }}
        </option>
      </select>
    </div>

    <!-- 결제자 선택 -->
    <div class="form-section">
      <label class="form-label">결제자</label>
      <select v-model="form.payerUserId" class="select-box">
        <option
          v-for="member in tripStore.currentTripMembers"
          :key="member.userId"
          :value="member.userId"
        >
          {{ member.nickname }}
        </option>
      </select>
    </div>

    <!-- 결제 참여자 선택 및 분배 금액 -->
    <div class="form-section">
      <label class="form-label">결제 참여자</label>
      <div class="participant-list">
        <div
          v-for="member in tripStore.currentTripMembers"
          :key="member.userId"
          class="participant-item"
        >
          <div class="badge">{{ member.nickname.charAt(0) }}</div>
          <span class="name">{{ member.nickname }}</span>

          <!-- 참여자 토글 -->
          <div class="toggle-wrapper" @click="toggleMember(member.userId)">
            <div class="toggle" :class="{ on: isSelected(member.userId) }">
              <div class="circle"></div>
            </div>
          </div>

          <!-- 분배 금액 입력 -->
          <div v-if="isSelected(member.userId)" class="input-box" style="margin-top: 8px;">
            <input
              type="number"
              :value="getSplitAmount(member.userId)"
              @input="updateSplitAmount(member.userId, $event.target.value)"
              class="input-text"
              style="width: 100px;"
            />
            <span class="input-suffix">원</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 메모 -->
    <div class="form-section">
      <label class="form-label">메모</label>
      <div class="textarea-box">
        <textarea
          v-model="form.memo"
          class="input-text"
          placeholder="상세 내용을 입력하세요..."
          rows="4"
          style="width: 100%; border: none; outline: none; resize: none;"
        ></textarea>
      </div>
    </div>
  </main>

  <!-- 하단 저장 버튼 -->
  <footer class="footer">
    <button class="delete-button">삭제</button>
    <button class="save-button" @click="handleSave">저장</button>    
  </footer>
</template>

<style scoped>
.add-payment {
  max-width: 384px;
  margin: 0 auto;
  background: #f8f9fa;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
}

.title {
  font-size: 18px;
  font-weight: 800;
  color: #4a4a4a;
}

.icon {
  width: 20px;
  height: 20px;
}

.content-container {
  padding: 0 16px;
  overflow-y: auto;
  flex: 1;
  padding-top: 56px;
  min-height: 0;
  max-height: 100%;
}

.form-section {
  margin-bottom: 16px;
}

.form-label {
  font-size: 14px;
  font-weight: bold;
  color: #4a4a4a;
  margin-bottom: 6px;
  display: block;
}

.input-box {
  background: #fff;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.input-text,
.input-suffix {
  font-size: 16px;
  color: #000;
  border: none;
  outline: none;
  font-family: 'IBM Plex Sans KR', sans-serif;
}

/* input[type=number]의 스핀버튼 제거 */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}


.select-box {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 10px 16px;
  font-size: 16px;
  width: 100%;
  text-align: center;
  cursor: pointer;
}

.participant-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.participant-item {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  padding: 12px;
  position: relative;
  flex-wrap: wrap;
  gap: 10px;
}

.badge {
  background: rgba(255, 209, 102, 0.65);
  border-radius: 9999px;
  color: white;
  font-weight: bold;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.name {
  flex: 1;
  font-weight: 600;
  color: #4a4a4a;
  min-width: 70px;
}

.toggle-wrapper {
  width: 48px;
  height: 28px;
  cursor: pointer;
}

.toggle {
  width: 100%;
  height: 100%;
  border-radius: 9999px;
  background-color: #ccc;
  position: relative;
  transition: background-color 0.3s ease;
}

.toggle.on {
  background-color: rgba(255, 209, 102, 0.65);
}

.toggle .circle {
  width: 20px;
  height: 20px;
  background-color: white;
  border-radius: 50%;
  position: absolute;
  top: 4px;
  left: 4px;
  transition: left 0.3s ease;
}

.toggle.on .circle {
  left: 24px;
}

.textarea-box {
  background: #fff;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  height: 100px;
  padding: 12px 16px;
  position: relative;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
}

.delete-button{
  background-color:rgba(255, 29, 12, 0.20);
  border-radius: 12px;
  padding: 16px 24px;
  font-weight: 1000;
  border: none;
  flex: 1;
  margin-left: 8px;
  font-size: 16px;
  color: #4A4A4A;

}
.save-button {
  background-color:rgba(255, 209, 102, 0.65);
  border-radius: 12px;
  padding: 16px 24px;
  font-weight: 1000;
  border: none;
  flex: 1;
  margin-left: 8px;
  font-size: 16px;
  color: #4A4A4A;
}

/* 스크롤바 */
.content-container::-webkit-scrollbar {
  width: 8px;
}

.content-container::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 50px;
}

.content-container::-webkit-scrollbar-thumb {
  background-color: #bbb;
  border-radius: 50px;
  border: 2px solid transparent;
  background-clip: padding-box;
}

.content-container::-webkit-scrollbar-thumb:hover {
  background-color: #888;
}
</style>

