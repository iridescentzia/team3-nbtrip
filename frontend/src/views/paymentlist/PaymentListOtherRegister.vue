<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import Header from '@/components/layout/Header.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import { useTripStore } from '@/stores/trip';
import paymentApi from '@/api/paymentApi';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();
const tripStore = useTripStore();
const date = ref(new Date());

const form = ref({
  content: '',
  amount: '',
  category: '',
  payerUserId: null,
  memo: '',
});

// { [userId]: amount } 형태로 각 멤버의 분담액을 관리
const splitAmounts = ref({});

// --- 로직 ---

// 1. 컴포넌트가 마운트될 때 실행될 로직
onMounted(async () => {
  // 1. 라우트 파라미터에서 tripId를 가져옴
  const tripId = route.params.tripId;

  if (tripId) {
    // tripStore의 기존 fetchTrip 함수를 호출하여 현재 여행 정보를 설정합니다.
    await tripStore.fetchTrip(tripId);

    // 현재 여행 데이터가 설정된 것을 확인하고
    // 나머지 데이터를 불러옵니다.
    if (tripStore.currentTrip) {
      await tripStore.fetchCurrentTripMemberNicknames();
      await tripStore.fetchMerchantCategories();

      if (tripStore.currentTripMembers.length > 0) {
        form.value.payerUserId = tripStore.currentTripMembers[0].userId;
        initializeParticipants();
      }
    }
  }
});

// 2. 참여자 목록을 초기화하고 1/N 금액을 분배하는 함수
function initializeParticipants() {
  const newSplits = {};
  tripStore.currentTripMembers.forEach((member) => {
    newSplits[member.userId] = 0;
  });
  splitAmounts.value = newSplits;
  distributeAmountEqually();
}

// 3. 금액 또는 결제자가 변경되면, 1/N을 다시 계산
watch(() => form.value.amount, distributeAmountEqually);
watch(
  () => form.value.payerUserId,
  () => {
    if (form.value.payerUserId && !isSelected(form.value.payerUserId)) {
      splitAmounts.value[form.value.payerUserId] = 0;
    }
    distributeAmountEqually();
  }
);

// 4. 1/N로 금액을 균등하게 분배하는 함수
function distributeAmountEqually() {
  const totalAmount =
    parseInt(String(form.value.amount).replace(/,/g, ''), 10) || 0;
  const selectedUserIds = Object.keys(splitAmounts.value);
  const participantCount = selectedUserIds.length;

  if (participantCount === 0 || totalAmount === 0) {
    selectedUserIds.forEach((userId) => {
      splitAmounts.value[userId] = 0;
    });
    return;
  }

  const baseSplitAmount = Math.floor(totalAmount / participantCount);
  let remainder = totalAmount % participantCount;

  selectedUserIds.forEach((userId) => {
    splitAmounts.value[userId] = baseSplitAmount;
  });

  if (
    remainder > 0 &&
    form.value.payerUserId &&
    isSelected(form.value.payerUserId)
  ) {
    splitAmounts.value[form.value.payerUserId] += remainder;
  }
}

// 5. 특정 멤버의 분담액을 수동으로 업데이트하는 함수
function updateSplitAmount(userId, event) {
  const totalAmount =
    parseInt(String(form.value.amount).replace(/,/g, ''), 10) || 0;
  let newAmount =
    parseInt(String(event.target.value).replace(/,/g, ''), 10) || 0;

  // 입력값이 총액을 넘지 않도록 제한
  if (newAmount > totalAmount) {
    alert('분배 금액은 총 결제 금액을 초과할 수 없습니다.');
    newAmount = totalAmount; // 최대값으로 강제 설정
  }

  splitAmounts.value[userId] = newAmount;
  // 입력창의 값을 포맷팅하여 다시 보여줌 (사용자 경험 향상)
  event.target.value = newAmount.toLocaleString();
}

// 6. 남은 금액을 계산하여 사용자에게 보여주는 computed 속성
const remainingAmount = computed(() => {
  const totalAmount =
    parseInt(String(form.value.amount).replace(/,/g, ''), 10) || 0;
  const calculatedTotal = Object.values(splitAmounts.value).reduce(
    (sum, val) => sum + (val || 0),
    0
  );
  return totalAmount - calculatedTotal;
});

// 참여자 토글 함수 (결제자는 해제 불가)
const toggleMember = (userId) => {
  if (userId === form.value.payerUserId) {
    alert('결제자는 항상 포함되어야 합니다.');
    return;
  }

  if (isSelected(userId)) {
    delete splitAmounts.value[userId];
  } else {
    splitAmounts.value[userId] = 0;
  }
  distributeAmountEqually();
};

const isSelected = (userId) => splitAmounts.value.hasOwnProperty(userId);

// 최종 저장 함수
const handleSave = async () => {
  const tripId = tripStore.currentTrip?.tripId;
  const totalAmount = parseInt(String(form.value.amount).replace(/,/g, ''), 10);
  const payerUserId = Number(form.value.payerUserId);

  if (
    !tripId ||
    !form.value.content ||
    !totalAmount ||
    !form.value.category ||
    !payerUserId
  ) {
    alert('모든 필수 항목을 입력해주세요.');
    return;
  }

  if (remainingAmount.value !== 0) {
    alert(`분배된 금액의 총합이 결제 금액과 일치하지 않습니다.`);
    return;
  }

  const participantsPayload = Object.entries(splitAmounts.value).map(
    ([userId, splitAmount]) => ({
      userId: Number(userId),
      splitAmount: splitAmount,
    })
  );

  // '기타 결제'용 merchantId를 찾는 로직
  const selectedCategory = tripStore.merchantCategories.find(
    (c) => c.categoryId === Number(form.value.category)
  );
  const categoryName = selectedCategory
    ? selectedCategory.categoryName
    : '기타';
  const merchantNameForOther = `${categoryName} 기타결제`;
  // To-do: 이 부분은 실제 가맹점 목록을 가져오는 로직으로 대체해야 합니다.
  const mockMerchants = [
    { merchantId: 7, merchantName: '식음료 기타결제' },
    { merchantId: 8, merchantName: '교통 기타결제' },
    { merchantId: 9, merchantName: '숙박 기타결제' },
    { merchantId: 10, merchantName: '관광 기타결제' },
    { merchantId: 11, merchantName: '쇼핑 기타결제' },
    { merchantId: 12, merchantName: '기타 기타결제' },
  ];
  const targetMerchant = mockMerchants.find(
    (m) => m.merchantName === merchantNameForOther
  );
  const merchantId = targetMerchant ? targetMerchant.merchantId : null;

  const paymentDTO = {
    tripId: tripId,
    memo: form.value.content,
    amount: totalAmount,
    payAt: formatDateTime(date.value),
    paymentType: 'OTHER',
    payerId: payerUserId,
    participants: participantsPayload,
    merchantId: merchantId,
  };

  try {
    await paymentApi.createOther(paymentDTO);
    alert('결제 내역이 성공적으로 등록되었습니다.');
    router.push(`/paymentlist/${tripId}`);
  } catch (e) {
    console.error('결제 등록 실패:', e.response?.data || e.message);
    alert('결제 등록에 실패했습니다.');
  }
};

// 금액 입력 시 콤마 추가
const formatAmountInput = (event) => {
  const numberOnly = event.target.value.replace(/[^0-9]/g, '');
  form.value.amount = Number(numberOnly).toLocaleString();
};

// 날짜 포맷팅 함수
const formatDateTime = (date) => {
  const pad = (n) => n.toString().padStart(2, '0');
  const yyyy = date.getFullYear();
  const MM = pad(date.getMonth() + 1);
  const dd = pad(date.getDate());
  const HH = pad(date.getHours());
  const mm = pad(date.getMinutes());
  return `${yyyy}-${MM}-${dd}T${HH}:${mm}`;
};
</script>

<template>
  <Header title="결제 내역 추가" @back="router.back" />
  <main class="content-container">
    <div class="form-section">
      <label class="form-label">내용</label>
      <div class="input-box">
        <input
          class="input-text"
          v-model="form.content"
          placeholder="지출 내용을 입력하세요"
        />
      </div>
    </div>

    <div class="form-section">
      <label class="form-label">금액</label>
      <div class="input-box">
        <input
          :value="form.amount"
          class="input-text"
          placeholder="금액을 입력하세요"
          @input="formatAmountInput"
        />
        <span class="input-suffix">원</span>
      </div>
    </div>

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

    <div class="form-section">
      <label class="form-label">카테고리</label>
      <select v-model="form.category" class="select-box">
        <option disabled value="">카테고리를 선택하세요</option>
        <option
          v-for="category in tripStore.merchantCategories"
          :key="category.categoryId"
          :value="category.categoryId"
        >
          {{ category.categoryName }}
        </option>
      </select>
    </div>

    <div class="form-section">
      <label class="form-label">결제자</label>
      <select v-model="form.payerUserId" class="select-box">
        <option disabled value="">결제자를 선택하세요</option>
        <option
          v-for="member in tripStore.currentTripMembers"
          :key="member.userId"
          :value="member.userId"
        >
          {{ member.nickname }}
        </option>
      </select>
    </div>

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

          <div class="split-amount-wrapper">
            <div class="toggle-wrapper" @click="toggleMember(member.userId)">
              <div
                class="toggle"
                :class="{
                  on: isSelected(member.userId),
                  disabled: member.userId === form.payerUserId,
                }"
              >
                <div class="circle"></div>
              </div>
            </div>

            <div v-if="isSelected(member.userId)" class="input-box split-box">
              <input
                type="text"
                :value="splitAmounts[member.userId]?.toLocaleString()"
                @input="updateSplitAmount(member.userId, $event)"
                class="input-text"
              />
              <span class="input-suffix">원</span>
            </div>
          </div>
        </div>
        <div
          v-if="remainingAmount !== 0"
          class="remaining-amount"
          :class="{ error: remainingAmount < 0 }"
        >
          {{
            remainingAmount > 0
              ? `${remainingAmount.toLocaleString()}원 남음`
              : `${Math.abs(remainingAmount).toLocaleString()}원 초과`
          }}
        </div>
      </div>
    </div>

    <div class="form-section">
      <label class="form-label">메모</label>
      <div class="textarea-box">
        <textarea
          v-model="form.memo"
          class="input-text"
          placeholder="상세 내용을 입력하세요..."
          rows="4"
        ></textarea>
      </div>
    </div>
  </main>

  <footer class="footer">
    <button
      class="save-button"
      @click="handleSave"
      :disabled="remainingAmount !== 0"
    >
      저장
    </button>
  </footer>
</template>

<style scoped>
.content-container {
  padding: 0 16px;
  overflow-y: auto;
  flex: 1;
  padding-top: calc(56px + 16px);
  padding-bottom: 96px;
}
.form-section {
  margin-bottom: 24px;
}
.form-label {
  font-size: 14px;
  font-weight: bold;
  color: #4a4a4a;
  margin-bottom: 8px;
  display: block;
}
.input-box,
.select-box,
.textarea-box {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px 16px;
  display: flex;
  align-items: center;
}
.input-text {
  width: 100%;
  font-size: 16px;
  border: none;
  outline: none;
  background: transparent;
  font-family: 'Pretendard', sans-serif;
}
.input-suffix {
  color: #888;
}
.select-box {
  width: 100%;
  -webkit-appearance: none;
  appearance: none;
  background-image: url('data:image/svg+xml;charset=UTF-8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" /></svg>');
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1.25rem;
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
  gap: 10px;
}
.badge {
  background: #ffd166;
  border-radius: 50%;
  color: white;
  font-weight: bold;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
}
.name {
  flex-grow: 1;
  font-weight: 600;
  color: #4a4a4a;
}
.split-amount-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
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
  background-color: #ffd166;
}
.toggle.disabled {
  background-color: #ffd166 !important;
  cursor: not-allowed;
  opacity: 0.7;
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
.input-box.split-box {
  padding: 8px 12px;
  width: 120px;
}
.split-box .input-text {
  text-align: right;
}
.textarea-box {
  padding: 12px 16px;
}
.textarea-box textarea {
  resize: none;
  font-family: inherit;
}
.footer {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  max-width: 384px;
  margin: 0 auto;
  padding: 16px;
  background: #f8f9fa;
  border-top: 1px solid #e2e8f0;
}
.save-button {
  width: 100%;
  background-color: #ffd166;
  border-radius: 12px;
  padding: 16px 24px;
  font-weight: 1000;
  border: none;
  font-size: 16px;
  color: #4a4a4a;
  cursor: pointer;
}
.save-button:disabled {
  background-color: #e5e7eb;
  cursor: not-allowed;
  opacity: 0.8;
}
.remaining-amount {
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  margin-top: 8px;
  color: #3a86ff;
}
.remaining-amount.error {
  color: #ef4444;
}
</style>
