<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useTripStore } from '@/stores/trip';
import paymentApi from '@/api/paymentApi';
import Header from '@/components/layout/Header.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

// --- 1. Imports & Setup ---
const router = useRouter();
const route = useRoute();
const tripStore = useTripStore();

// --- 2. State Management (ref, computed) ---

// 폼 입력 데이터
const form = ref({
  memo: '',
  amount: '',
  category: '',
  payerUserId: null,
});
const date = ref(new Date());

// 멤버별 분담금액 데이터 { [userId]: amount }
const splitAmounts = ref({});

// 남은 분담액 계산
const remainingAmount = computed(() => {
  const totalAmount =
    parseInt(String(form.value.amount).replace(/,/g, ''), 10) || 0;
  const calculatedTotal = Object.values(splitAmounts.value).reduce(
    (sum, val) => sum + (val || 0),
    0
  );
  return totalAmount - calculatedTotal;
});

// --- 3. Lifecycle Hooks ---

/**
 * 컴포넌트가 마운트될 때 실행됩니다.
 * 여행 정보를 불러오고, 참여자 목록을 초기화합니다.
 */
onMounted(async () => {
  const tripId = route.params.tripId;
  if (tripId) {
    await tripStore.fetchTrip(tripId);
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

// --- 4. Watchers ---

/**
 * 총 금액이 변경될 때마다 분담금을 자동으로 재계산합니다.
 */
watch(
  () => form.value.amount,
  () => {
    distributeAmountEqually();
  }
);

/**
 * 결제자가 변경될 때마다 분담금을 자동으로 재계산합니다.
 * 결제자는 항상 참여자에 포함됩니다.
 */
watch(
  () => form.value.payerUserId,
  () => {
    if (form.value.payerUserId && !isSelected(form.value.payerUserId)) {
      const newSplits = { ...splitAmounts.value };
      newSplits[form.value.payerUserId] = 0;
      splitAmounts.value = newSplits;
    }
    distributeAmountEqually();
  }
);

// --- 5. Core Logic (Amount Distribution) ---

/**
 * 결제 참여자 목록을 여행 멤버 전체로 초기화합니다.
 */
function initializeParticipants() {
  const newSplits = {};
  tripStore.currentTripMembers.forEach((member) => {
    newSplits[member.userId] = 0;
  });
  splitAmounts.value = newSplits;
  distributeAmountEqually();
}

/**
 * 선택된 참여자들에게 1/N로 금액을 균등하게 분배합니다.
 * 남은 금액은 결제자에게 더해집니다.
 */
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

/**
 * 사용자가 분담금을 수동으로 수정할 때 호출됩니다.
 * @param {number} userId - 수정할 멤버의 ID
 * @param {Event} event - 입력 이벤트 객체
 */
function updateSplitAmount(userId, event) {
  const totalAmount =
    parseInt(String(form.value.amount).replace(/,/g, ''), 10) || 0;
  let newAmount =
    parseInt(String(event.target.value).replace(/,/g, ''), 10) || 0;

  const sumOfOtherAmounts = Object.entries(splitAmounts.value)
    .filter(([key, _]) => Number(key) !== userId)
    .reduce((sum, [_, amount]) => sum + (amount || 0), 0);

  const maxAmountForThisUser = totalAmount - sumOfOtherAmounts;

  if (newAmount > totalAmount) {
    alert(`분배 금액은 총 금액을 넘을 수 없습니다.`);
    newAmount = maxAmountForThisUser < 0 ? 0 : maxAmountForThisUser;
  }

  splitAmounts.value[userId] = newAmount;
  event.target.value = newAmount.toLocaleString();
}

/**
 * 결제 참여자 목록에서 멤버를 추가하거나 제거합니다.
 * 결제자는 제거할 수 없습니다.
 * @param {number} userId - 토글할 멤버의 ID
 */
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

// --- 6. API Calls ---

/**
 * '저장' 버튼 클릭 시, 입력된 결제 정보를 서버로 전송합니다.
 */
const handleSave = async () => {
  const tripId = route.params.tripId;
  const totalAmount = parseInt(String(form.value.amount).replace(/,/g, ''), 10);
  const payerUserId = Number(form.value.payerUserId);

  if (!tripId || !totalAmount || !form.value.category || !payerUserId) {
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

  const selectedCategory = tripStore.merchantCategories.find(
    (c) => c.categoryId === Number(form.value.category)
  );
  const categoryName = selectedCategory
    ? selectedCategory.categoryName
    : '기타';
  const merchantNameForOther = `${categoryName} 선결제/기타결제`;
  const mockMerchants = [
    { merchantId: 1, merchantName: '식음료 선결제/기타결제' },
    { merchantId: 2, merchantName: '교통 선결제/기타결제' },
    { merchantId: 3, merchantName: '숙박 선결제/기타결제' },
    { merchantId: 4, merchantName: '관광 선결제/기타결제' },
    { merchantId: 5, merchantName: '쇼핑 선결제/기타결제' },
    { merchantId: 6, merchantName: '기타 선결제/기타결제' },
  ];
  const targetMerchant = mockMerchants.find(
    (m) => m.merchantName === merchantNameForOther
  );
  const merchantId = targetMerchant ? targetMerchant.merchantId : null;

  const paymentDTO = {
    tripId: tripId,
    memo: form.value.memo,
    amount: totalAmount,
    payAt: formatDateTime(date.value),
    paymentType: 'PREPAID',
    payerId: payerUserId,
    participants: participantsPayload,
    merchantId: merchantId,
  };

  try {
    await paymentApi.createPrepaid(paymentDTO);
    alert('선결제 내역이 성공적으로 등록되었습니다.');
    router.go(-1);
  } catch (e) {
    console.error('결제 등록 실패:', e.response?.data || e.message);
    alert('선결제 등록에 실패했습니다.');
  }
};

// --- 7. Utilities ---

/**
 * 특정 멤버가 현재 참여자로 선택되었는지 확인합니다.
 * @param {number} userId - 확인할 멤버의 ID
 * @returns {boolean}
 */
const isSelected = (userId) => splitAmounts.value.hasOwnProperty(userId);

/**
 * 금액 입력창에 3자리마다 콤마를 추가합니다.
 * @param {Event} event - 입력 이벤트 객체
 */
const formatAmountInput = (event) => {
  const numberOnly = event.target.value.replace(/[^0-9]/g, '');
  form.value.amount = Number(numberOnly).toLocaleString();
};

/**
 * Date 객체를 'YYYY-MM-DDTHH:mm' 형식의 문자열로 변환합니다.
 * @param {Date} date - 변환할 Date 객체
 * @returns {string}
 */
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
      <div class="textarea-box">
        <input
          class="input-text"
          v-model="form.memo"
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
          <div class="participant-info">
            <div class="badge">{{ member.nickname.charAt(0) }}</div>
            <span class="name">{{ member.nickname }}</span>
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
          </div>

          <div v-if="isSelected(member.userId)" class="split-amount-wrapper">
            <div class="input-box split-box">
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
  flex-wrap: wrap;
  padding: 12px;
  gap: 10px;
}
.participant-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-grow: 1;
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
  flex-basis: 100%;
  display: flex;
  justify-content: flex-end; /* Align to the right */
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
