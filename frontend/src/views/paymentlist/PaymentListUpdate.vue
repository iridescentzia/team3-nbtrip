<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Header from '@/components/layout/Header.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import paymentlistApi from '@/api/paymentlistApi';
import paymentApi from '@/api/paymentApi';
import {usePaymentListStore} from "@/stores/tripStore.js";
import { Trash2 } from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();
const paymentId = route.params.paymentId;
const tripStore = usePaymentListStore();

const paymentType = ref('');
const date = ref(new Date());
const form = ref({
  content: '',
  amount: '',
  category: '',
  payerUserId: '',
  memo: ''
});

const selectedMembers = ref([]); // 결제 참여자 목록
const splitAmounts = ref({}); // 결제 참여자별 분배 금액 저장
const isDeleteModalVisible = ref(false);
const isLoading = ref(true); // 로딩 여부

// 선택 여부 확인
const isSelected = (userId) => {
  return selectedMembers.value.some(p => p.userId === userId);
};

// 토글 ON/OFF
const toggleMember = (userId) => {
  if (userId === form.value.payerUserId){
    alert('결제자는 항상 포함되어야 합니다.')
    return;
  }

  const index = selectedMembers.value.findIndex(p => p.userId === userId);
  if (index === -1) {
    selectedMembers.value.push({ userId, splitAmount: 0 }); // 새로 추가
  } else {
    selectedMembers.value.splice(index, 1); // 제거
  }
  distributeAmountEqually();
};

// 현재 금액 가져오기
const getSplitAmount = (userId) => {
  const member = selectedMembers.value.find(p => p.userId === userId);
  return member ? member.splitAmount : 0;
};

// 금액 수정 처리
// const updateSplitAmount = (userId, value) => {
//   const member = selectedMembers.value.find(p => p.userId === userId);
//   if (member) {
//     const clean = value.toString().replace(/[^0-9]/g, '')
//     member.splitAmount = Number(clean);
//   }
// };

// 수동 입력 후 자동 분배 ver.1
// const updateSplitAmount = (userId, value) => {
//   const totalAmount = parseInt(form.value.amount.replace(/,/g, ''), 10) || 0;
//   const newAmount = parseInt(String(value).replace(/,/g, ''), 10);

//   if (isNaN(newAmount)) {
//     alert('숫자만 입력할 수 있습니다.');
//     return;
//   }

//   if (newAmount > totalAmount) {
//     alert('분배 금액은 총 결제 금액을 초과할 수 없습니다.');
//     return;
//   }

//   const target = selectedMembers.value.find(p => p.userId === userId);
//   if (target) {
//     target.splitAmount = newAmount;
//   }

//   const remainingAmount = totalAmount - newAmount;

//   // 나머지 참여자 필터링
//   const others = selectedMembers.value.filter(p => p.userId !== userId);
//   const otherCount = others.length;

//   if (otherCount === 0) return;

//   const base = Math.floor(remainingAmount / otherCount);
//   let remainder = remainingAmount % otherCount;

//   // 재분배
//   others.forEach(p => {
//     p.splitAmount = base;
//   });

//   // 나머지는 결제자에게 우선 할당
//   const payer = others.find(p => p.userId === form.value.payerUserId);
//   if (payer) {
//     payer.splitAmount += remainder;
//   } else {
//     others[0].splitAmount += remainder;
//   }
// };

const manuallyEditedUserIds = ref([]); // 사용자 직접 수정한 userId 추적

const updateSplitAmount = (userId, value) => {
  const totalAmount = parseInt(form.value.amount.replace(/,/g, ''), 10) || 0;
  const newAmount = parseInt(String(value).replace(/,/g, ''), 10);

  if (isNaN(newAmount)) {
    alert('숫자만 입력할 수 있습니다.');
    return;
  }

  if (newAmount > totalAmount) {
    alert('분배 금액은 총 결제 금액을 초과할 수 없습니다.');
    return;
  }

  // 직접 수정한 유저 목록에 등록
  if (!manuallyEditedUserIds.value.includes(userId)) {
    manuallyEditedUserIds.value.push(userId);
  }

  // 현재 유저 splitAmount 업데이트
  const target = selectedMembers.value.find(p => p.userId === userId);
  if (target) {
    target.splitAmount = newAmount;
  }

  // 총 금액 - 직접 입력된 유저들의 합
  const manuallyEnteredTotal = selectedMembers.value
    .filter(p => manuallyEditedUserIds.value.includes(p.userId))
    .reduce((sum, p) => sum + p.splitAmount, 0);

  const remainingAmount = totalAmount - manuallyEnteredTotal;

  // 재분배 대상
  const toDistribute = selectedMembers.value.filter(
    p => !manuallyEditedUserIds.value.includes(p.userId)
  );
  const count = toDistribute.length;

  if (count === 0) return;

  const base = Math.floor(remainingAmount / count);
  let remainder = remainingAmount % count;

  toDistribute.forEach(p => {
    p.splitAmount = base;
  });

  // 결제자가 재분배 대상이면 그에게 나머지 몰아주기
  const payer = toDistribute.find(p => p.userId === form.value.payerUserId);
  if (payer) {
    payer.splitAmount += remainder;
  } else {
    toDistribute[0].splitAmount += remainder;
  }
};



// 금액 입력 시 콤마 포맷 적용
const formatAmountInput = () => {
  const numberOnly = form.value.amount.replace(/[^0-9]/g, '');
  if (numberOnly.length !== form.value.amount.replace(/,/g, '').length) {
    alert('숫자만 입력할 수 있습니다.');
  }
  form.value.amount = Number(numberOnly).toLocaleString();
};

// 날짜 포맷
const formatDateTime = (date) => {
  const pad = (n) => n.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
};

// 자동 1/N 분배 함수
const distributeAmountEqually = () => {
  const totalAmount = parseInt(String(form.value.amount).replace(/,/g, ''), 10) || 0;
  const participantUserIds = selectedMembers.value.map(p => p.userId);
  const participantCount = participantUserIds.length;

  if (participantCount === 0 || totalAmount === 0) {
    participantUserIds.forEach(userId => {
      const member = selectedMembers.value.find(p => p.userId === userId);
      if (member) member.splitAmount = 0;
    });
    return;
  }

  const baseAmount = Math.floor(totalAmount / participantCount);
  let remainder = totalAmount % participantCount;

  participantUserIds.forEach(userId => {
    const member = selectedMembers.value.find(p => p.userId === userId);
    if (member) member.splitAmount = baseAmount;
  });

  // 결제자에게 나머지 금액 더하기
  if (remainder > 0 && form.value.payerUserId) {
    const payer = selectedMembers.value.find(p => p.userId === form.value.payerUserId);
    if (payer) payer.splitAmount += remainder;
  }
};

// 금액 또는 결제자 변경 시 자동 분배
watch(() => form.value.amount, distributeAmountEqually);
watch(() => form.value.payerUserId, () => {
  if (form.value.payerUserId && !isSelected(form.value.payerUserId)) {
    splitAmounts.value[form.value.payerUserId] = 0;
  }
  distributeAmountEqually();
});

onMounted(async () => {
  await tripStore.fetchCurrentTripMemberNicknames();
  await tripStore.fetchMerchantCategories();

  try {
    const data = await paymentlistApi.getPaymentListById(paymentId);
    const p = data.payment;
    paymentType.value = p.paymentType;
    console.log("paymentType: ", paymentType.value)

    console.log("tripStore.merchantCategories: ", tripStore.merchantCategories)

    // res: 결제 참여자 목록
    const res = await paymentApi.getParticipantsByPaymentId(paymentId);
    selectedMembers.value = res.map(p => ({
      userId: p.userId,
      splitAmount: p.splitAmount
    }));
    console.log("selectedMembers: ", selectedMembers.value)

    // splitAmount 초기값을 splitAmounts 객체({ [userId]: splitAmount })에 저장
    res.forEach(p => {
      splitAmounts.value[p.userId] = p.splitAmount;
    })

    form.value = {
      content: p.merchantName,
      amount: Number(p.amount).toLocaleString(),
      category: p.categoryId,
      payerUserId: p.userId,
      memo: p.memo
    };
    date.value = new Date(p.payAt);

    // 초기 진입 시에도 자동 분배 적용
    distributeAmountEqually(); 

  } catch (e) {
    console.error('결제 내역 불러오기 실패:', e);
  } finally {
    isLoading.value = false;
  }
});

// 저장 버튼 클릭 시 api 호출
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
  console.log("payerUserId", form.value.payerUserId);
  console.log("participants", selectedMembers.value);
  

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
    alert('결제 내역이 수정되었습니다.')
    router.go(-1)
  } catch (e) {
    console.error('수정 실패:', e.response?.data || e);
     alert('결제 내역 수정에 실패했습니다.')
  }
};

// 결제 내역 삭제
const handleDelete = async () => {
  try {
    await paymentApi.deletePayment(paymentId)
    alert('결제 내역이 삭제되었습니다.')
    router.go(-1)
  }catch (error) {
    console.error('결제 내역 삭제 실패: ', error)
    alert('결제 삭제에 실패했습니다.')
  }
}
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
        :disabled="paymentType == 'QR'"
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
          pattern="[0-9]*"
          :disabled="paymentType == 'QR'"
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
          :disabled="paymentType === 'QR'"
          format="yyyy-MM-dd HH:mm"
          class="date-picker"
        />
      </div>
    </div>

    <!-- 카테고리 선택 -->
    <div class="form-section">
      <label class="form-label">카테고리</label>
      <select v-model="form.category" class="select-box" :disabled="paymentType === 'QR'">
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
      <select v-model="form.payerUserId" class="select-box" :disabled="paymentType === 'QR'">
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
            <div class="toggle" :class="{ 
              on: isSelected(member.userId),
              disabled: member.userId === form.payerUserId 
              }">
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

  <footer class="footer">
    <button 
      class="delete-button" 
      @click="isDeleteModalVisible = true" 
      v-if="!isLoading && paymentType !== 'QR'">
      <Trash2 class="icon trash-icon" />
    </button>

    <button class="save-button" @click="handleSave">저장</button>    
  </footer>
  
  <Transition name="slide-up">
    <div v-if="isDeleteModalVisible" class="modal">
      <div style="margin-bottom: 16px; text-align: center">
        <p style="font-size: 20px; font-weight: bold;">⚠️ 결제 내역을 삭제하시겠어요?</p>
        <p style="font-size: 16px; color: gray; padding-bottom:25px;">삭제된 결제는 복구할 수 없어요.</p>
      </div>

      <div style="display: flex; justify-content: space-between;">
        <button class="cancle-button" @click="isDeleteModalVisible = false">취소</button>
        <button class="ok-button" @click="handleDelete">삭제</button>
      </div>
    </div>
  </Transition>

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
  padding: 8px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
   margin-left: auto; /* 오른쪽 정렬 */
}

.input-text,
.input-suffix {
  font-size: 18px;
  color: #000;
  border: none;
  outline: none;
  font-family: 'IBM Plex Sans KR', sans-serif;
}

.input-text:disabled{
  color:rgb(170, 170, 170);
  background-color: transparent  ;
}

/* input[type=number]의 스핀버튼 제거 */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

::v-deep(.date-picker input) {
  font-size: 16px;
  color: #000;
}

.select-box {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 10px 16px;
  font-size: 18px;
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
  justify-content: space-between; /* 왼쪽/오른쪽 정렬 */
  
}

.badge {
  background: rgba(255, 209, 102, 0.65);
  border-radius: 9999px;
  color: white;
  font-weight:600;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  
}

.name {
  flex: 1;
  font-weight: 600;
  color: #4a4a4a;
  min-width: 70px;
  padding-left:3px;
  font-size: 18px;
  transform: translateY(-1px); /* 아래로 2px 이동 */  
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

.trash-icon{
  stroke-width: 2.3;
  width: 26px;
  transform: translateY(2px); /* 아래로 2px 이동 */
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
  cursor:pointer;

}
.save-button {
  /* background-color:rgba(255, 209, 102, 0.65); */
  background-color: #FFE499;
  border-radius: 12px;
  padding: 16px 24px;
  font-weight: 1000;
  border: none;
  flex: 9;
  margin-left: 8px;
  font-size: 17px;
  color: #4A4A4A;
  cursor: pointer;
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

.modal {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 352px;
  background-color: #ffffff;
  border-radius: 16px 16px 0 0;
  padding: 16px 16px 24px 16px;
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.15);
  z-index: 1001;
  animation: modalUp 0.25s ease;
}

@keyframes modalUp {
  from {
    bottom: -300px;
    opacity: 0;
  }
  to {
    bottom: 0;
    opacity: 1;
  }
}

.cancle-button,
.ok-button {
  background-color: #FFE499;
  border-radius: 12px;
  padding: 16px 24px;
  font-weight: 1000;
  border: none;
  flex: 9;
  margin-left: 8px;
  font-size: 17px;
  color: #4A4A4A;
  cursor: pointer;

}

/* transition 이름은 "slide-up" */
.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.5s ease;
}
.slide-up-enter-from, .slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}
.slide-up-enter-to, .slide-up-leave-from {
  transform: translateY(0%);
  opacity: 1;
}

</style>

