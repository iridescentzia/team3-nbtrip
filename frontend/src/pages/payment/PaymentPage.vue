<script setup>
import { usePaymentStore } from '@/stores/paymentStore';
import paymentApi from '@/api/paymentApi';
import tripApi from '@/api/tripApi';
import QRScanner from '@/components/qr/QRScanner.vue';
import { getMyInfo } from '@/api/memberApi.js';
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import Header from '@/components/layout/Header.vue';
import Button from '@/components/common/Button.vue';

const router = useRouter();
const store = usePaymentStore();

const INT_MAX = 2147483647;
const canSubmit = computed(() => {
  return store.amount > 0 && store.amount <= INT_MAX;
});

onMounted(async () => {
  try {
    const tripId = await tripApi.getCurrentTripId();
    if (tripId == 0) {
      console.error('현재 여행 ID를 가져오지 못했습니다.');
      alert(
        '현재 날짜에 진행 중인 여행이 없습니다. 여행을 생성하거나 참여해주세요.'
      );
      router.push('/');
    }
  } catch (err) {
    console.error('마운트 중 에러 발생:', err);
  }
});

// 모달이 열릴 때 참여자 모두 선택되도록
watch(
  () => store.isModalVisible && store.modalType === 1,
  (visible) => {
    if (visible) {
      store.selectedParticipants = store.participantsNickname.map(
        (p) => p.userId
      );
    }
  }
);

async function submitPayment() {
  try {
    const payload = {
      merchantId: store.merchantID,
      amount: store.amount,
      paymentType: 'QR',
      participants: store.selectedParticipants.map((id) => ({ userId: id })),
    };
    store.payment = await paymentApi.create(payload);
    store.modalType = 2;
  } catch (error) {
    store.reason = error.response?.data || '알 수 없는 오류';
    store.modalType = 3;
  }
}
</script>

<template>
  <div>
    <Header
      title="QR 스캔"
      :backAction="
        () => {
          store.resetModal();
          router.push('/');
        }
      "
    />

    <main class="content">
      <div>
        <div style="text-align: center; margin-bottom: 16px; margin-top: 60px">
          <p>결제할 가게의 QR코드를 인식해주세요.</p>
        </div>

        <QRScanner style="height: 744px" />

        <div v-if="store.isModalVisible && store.modalType === 1" class="modal">
          <!-- 결제 입력 모달 -->
          <div
            style="
              width: calc(100% - 32px);
              display: flex;
              flex-direction: column;
              justify-content: center;
              align-items: center;
              margin: 10px auto 0 auto;
            "
          >
            <h2 style="text-align: center; width: 100%; margin: 0 0 16px 0">
              '{{ store.tripName }}' 중
            </h2>

            <div
              class="input-group"
              style="width: 100%; max-width: 320px; margin: 0 auto 16px auto"
            >
              <label for="merchantName">가게 이름</label><br />
              <input
                id="merchantName"
                type="text"
                :value="store.merchantName"
              />
            </div>

            <div
              class="input-group"
              style="width: 100%; max-width: 320px; margin: 0 auto 16px auto"
            >
              <label for="amount">결제 금액</label><br />
              <div style="display: flex; align-items: center">
                <input
                  id="amount"
                  :value="store.amount ? store.amount.toLocaleString() : ''"
                  @input="
                    (e) => {
                      const val = e.target.value.replace(/[^0-9]/g, '');
                      store.amount = Number(val);
                      e.target.value = val ? Number(val).toLocaleString() : '';
                    }
                  "
                  type="text"
                  placeholder="예: 55,000"
                  style="padding-right: 12px; vertical-align: middle; flex: 1"
                />
                <span style="margin-left: 8px">원</span>
              </div>
              <div v-if="store.amount > INT_MAX" class="amount-error">
                최대 {{ INT_MAX.toLocaleString() }}원까지만 입력 가능합니다.
              </div>
            </div>

            <div
              class="input-group"
              style="width: 100%; max-width: 320px; margin: 0 auto"
            >
              <label>결제에 참여하는 사람들</label>
              <div
                v-for="participant in store.participantsNickname"
                :key="participant.userId"
                class="checkbox-group"
              >
                <input
                  type="checkbox"
                  :id="`participant-${participant.userId}`"
                  :value="participant.userId"
                  v-model="store.selectedParticipants"
                  :checked="
                    store.selectedParticipants.includes(participant.userId)
                  "
                />
                <label :for="`participant-${participant.userId}`">{{
                  participant.nickname
                }}</label>
              </div>
            </div>
          </div>

          <Button
            :label="'결제하기'"
            :disabled="!canSubmit"
            @click="submitPayment"
          />
        </div>

        <!-- 결제 성공 모달 -->
        <div
          v-if="store.isModalVisible && store.modalType === 2"
          class="modal"
          style="
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
          "
        >
          <div
            style="
              width: calc(100% - 32px);
              text-align: left;
              margin-bottom: 8px;
              display: flex;
              flex-direction: row;
              justify-content: space-between;
            "
          >
            <h3 style="font-size: 22px; font-weight: bold; color: #34495e">
              결제 완료!
            </h3>
            <img
              src="@/assets/img/smiling_cat.png"
              alt="웃는 고양이"
              style="width: 100px"
            />
          </div>
          <Button
            :label="'홈으로 돌아가기'"
            @click="
              () => {
                store.resetModal();
                router.push('/');
              }
            "
            :style="{ margin: '0' }"
          />
        </div>

        <!-- 결제 실패 모달 -->
        <div
          v-if="store.isModalVisible && store.modalType === 3"
          class="modal"
          style="
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
          "
        >
          <div
            style="
              width: calc(100% - 32px);
              text-align: left;
              margin-bottom: 8px;
              display: flex;
              flex-direction: row;
              justify-content: space-between;
            "
          >
            <div>
              <h3>결제에 실패했어요...</h3>
              <p>사유: {{ store.reason }}</p>
            </div>
            <img
              src="@/assets/img/crying_cat.png"
              alt="우는 고양이"
              style="width: 100px"
            />
          </div>
          <div
            style="
              width: calc(100% - 32px);
              height: 48px;
              display: flex;
              justify-content: space-between;
            "
          >
            <Button
              :label="'취소하기'"
              @click="store.resetModal()"
              style="margin-right: 8px"
            />
            <Button :label="'다시 시도하기'" @click="submitPayment" />
          </div>
        </div>
      </div>
    </main>
    <div class="mid"></div>
  </div>
</template>

<style scoped>
/* Payment Modal */
/* 모달 박스 */
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

/* 입력 그룹 */
.input-group {
  margin-bottom: 16px;
}

.input-group label {
  font-size: 14px;
  color: #333333;
  margin-bottom: 6px;
}

.input-group input[type='text'],
.input-group input[type='number'] {
  width: 100%;
  padding: 10px 12px;
  font-size: 14px;
  border: 1px solid #cccccc;
  border-radius: 4px;
  box-sizing: border-box;
  margin-top: 8px;
  border-radius: 0.6rem;
}

/* 체크박스 그룹 */
.checkbox-group {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.checkbox-group input[type='checkbox'] {
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;

  width: 18px;
  height: 18px;
  border: 2px solid #ffd166;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  position: relative;
  margin-right: 8px;
  transition: background-color 0.2s, border-color 0.2s;
}

/* 체크된 상태 */
.checkbox-group input[type='checkbox']:checked {
  background-color: #ffd166;
}

/* 체크 표시 중앙 정렬 */
.checkbox-group input[type='checkbox']:checked::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 5px;
  height: 9px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: translate(-50%, -50%) rotate(45deg);
  display: block;
}

.amount-error {
  color: #ef4444;
  font-size: 13px;
  margin-top: 4px;
}
</style>
