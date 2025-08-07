<template>
  <Header title="결제 내역 추가" @back="router.back"/>

  <!-- 메인 입력 영역 -->
  <main class="content-container">
    <!-- 내용 입력 -->
    <div class="form-section">
      <label class="form-label">내용</label>
      <div class="input-box">
        <input 
        class="input-text"
        v-model="form.content" 
        placeholder="지출 내용을 입력하세요" />
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
    <button class="save-button" @click="handleSave">저장</button>
  </footer>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Header from '@/components/layout/Header.vue';
import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'
import {usePaymentListStore} from "@/stores/tripStore.js";
import paymentApi from '@/api/paymentApi';
import { useRoute, useRouter } from 'vue-router';
// import { useToast } from 'vue-toastification';

const router = useRouter();
const route = useRoute();

// const toast = useToast();
const tripStore = usePaymentListStore();
const date = ref(new Date());


const form = ref({
  content: '',
  amount: '',
  category: '',
  payerUserId: '',
  memo: ''
});


const isOn = ref(true); // 상태 값 (true: ON, false: OFF)
// 참여자로 선택된 userId 목록 (토글 상태)
const selectedMembers = ref([]);

// 특정 userId가 선택됐는지 여부 확인
const isSelected = (userId) => {
  return selectedMembers.value.includes(userId);
};

// 토글 클릭 시 참여 여부 ON/OFF
const toggleMember = (userId) => {
  const index = selectedMembers.value.indexOf(userId);
  if (index === -1) {
    selectedMembers.value.push(userId);
  } else {
    selectedMembers.value.splice(index, 1);
  }
};

// 금액 표시용 포맷터
const formatAmountInput = () => {
  const numberOnly = form.value.amount.replace(/[^0-9]/g, '');
  form.value.amount = Number(numberOnly).toLocaleString();
};

const handleSave = async () => {
  const tripId = tripStore.currentTrip?.tripId;
  const amount = parseInt(form.value.amount.replace(/,/g, ''), 10);
  const split = Math.floor(amount / selectedMembers.value.length);

  if (!tripId || !form.value.content || !amount || !form.value.category || !form.value.payerUserId || selectedMembers.value.length === 0) {
    console.error('모든 항목을 입력해주세요.');
    return;
  }

  const payAt = formatDateTime(date.value);
  const paymentDTO = {
    merchantId: Number(form.value.category), // categoryId와 merchantId 동일
    amount,
    memo: form.value.memo,
    paymentDate: payAt.split(' ')[0],
    paymentTime: payAt.split(' ')[1],
    paymentType: 'PREPAID',
    participants: selectedMembers.value.map(userId => ({
      userId,
      splitAmount: split
    }))
  };

  try {
    await paymentApi.createOther(paymentDTO);
    // toast.success('기타 결제 등록 완료!');
    console.log('기타 결제 등록 완료!')
    // 폼 초기화 원하면 여기에 추가
  } catch (e) {
    // toast.error('등록 실패: ' + e.response?.data || '알 수 없는 오류');
    console.error(e.reponse.data);
  }
};

const formatDateTime = (date) => {
  const pad = (n) => n.toString().padStart(2, '0');
  const yyyy = date.getFullYear();
  const MM = pad(date.getMonth() + 1);
  const dd = pad(date.getDate());
  const HH = pad(date.getHours());
  const mm = pad(date.getMinutes());
  const ss = pad(date.getSeconds());
  return `${yyyy}-${MM}-${dd} ${HH}:${mm}:${ss}`;
};

// 마운트 시 카테고리 목록 불러오기
onMounted(async () => {
  //await tripStore.fetchTrip(route.params.tripId);
  await tripStore.fetchCurrentTripMemberNicknames(); // 이거 꼭 호출해야 닉네임 목록 들어옴
  await tripStore.fetchMerchantCategories();
});

</script>

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
