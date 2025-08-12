<template>
  <div class="content-container">
    <Header :title="title" @back="router.back"/>

    <TravelCard
      v-if="isReady"
      :trip-name="tripStore.currentTrip.tripName"
      :start-date="formatDate(tripStore.currentTrip.startDate)"
      :end-date="formatDate(tripStore.currentTrip.endDate)"
      v-model:activeTab="activeTab"
      :trip-id="tripStore.currentTrip.tripId"
      :trip-status="tripStore.currentTrip.tripStatus"
      :on-delete="handleDelete"
      :is-owner="isOwner"
      showEdit
    />

    <div v-if="isReady && (activeTab === '그룹 지출 내역' || activeTab === '선결제 내역')">
      <Summary
        v-if="tripStore.currentTrip"
        :amount="totalAmount"
        :budget="tripStore.currentTrip.budget"
        :on-terminate="handleTripTerminate"
        :is-owner="isOwner"
        :is-closed="isClosed"
      >
      </Summary>

      <Filter
        v-if="tripStore.currentTrip"
        :start-date="formatDate(tripStore.currentTrip.startDate)"
        :members="tripStore.currentTripMembers"
        @date-filtered="onDateFiltered"
        @participant-filtered="onParticipantFiltered"
        @category-filtered="onCategoryFiltered"
      />
      <PaymentListInfo
        :date-range="selectedDateRange"
        :selected-participants="selectedParticipants"
        :selected-categories="selectedCategories"
        :active-tab="activeTab"
        @init-total="onInitTotal"
      />
    </div>
    <div v-else-if="isReady">
      <TripEdit
          ref="updateTrip"
          :isOwner="isOwner"
      />
    </div>
  </div>

    <button
      v-if="isReady && !isClosed && activeTab === '그룹 지출 내역'"
      class="floating-button"
      @click="goToOtherRegister">
      +  기타 결제
    </button>

    <button
      v-if="isReady && !isClosed && activeTab === '선결제 내역'"
      class="floating-button"
      @click="goToPrepaidRegister"
    >
      + 선결제
    </button>

    <button
      v-if="isReady && !isClosed && activeTab && isOwner === '그룹 관리'"
      class="floating-button"
      @click="callChildUpdate"
    >
      저장하기
    </button>

</template>

<script setup>
import Header from '@/components/layout/Header.vue';
import TravelCard from '@/components/common/TravelCard.vue';
import Filter from '@/components/paymentlist/Filter.vue';

import { computed, onMounted, ref } from 'vue';
import { usePaymentlistStore } from '@/stores/tripStore.js';
import { useRouter, useRoute } from 'vue-router';
import Summary from '@/components/common/Summary.vue';
import PaymentListInfo from '@/views/paymentlist/PaymentListInfo.vue';
import TripEdit from '@/views/trip/TripEdit.vue';
import tripApi from '@/api/tripApi.js';
import {
  requestSettlement,
  getSettlementSummary,
} from '@/api/settlementApi.js';

const router = useRouter();
const route = useRoute();
const tripStore = usePaymentlistStore();
const activeTab = ref('그룹 지출 내역');
const updateTrip = ref(null);
const isOwner = ref(false);
const title = ref('');
const isClosed = ref(false);

const isReady = computed(()=> !!tripStore.currentTrip) // 데이터 준비 여부

const callChildUpdate = async () => {
  if (updateTrip.value) {
    await updateTrip.value.handleUpdate();
    await tripStore.fetchTrip(route.params.tripId);
    activeTab.value = '그룹 지출 내역';
  }
};

const checkIsOwner = async () => {
  try {
    const tripId = route.params.tripId;
    isOwner.value = await tripApi.isOwner(parseInt(tripId));
  } catch (error) {
    console.error('그룹장 여부 확인 실패:', error);
    isOwner.value = false;
  }
};

const goToOtherRegister = () => {
  const tripId = route.params.tripId;
  router.push({
    name: 'payment-register-other', // 라우터 이름
    params: {
      tripId: tripId,
    },
  });
};

const goToPrepaidRegister = () => {
  const tripId = route.params.tripId;
  router.push({
    name: 'payment-register-prepaid', // 라우터 이름
    params: {
      tripId: tripId,
    },
  });
};

const formatDate = (dateInput) => {
  let date;

  if (Array.isArray(dateInput) && dateInput.length === 3) {
    const [year, month, day] = dateInput;
    date = new Date(year, month - 1, day);
  } else if (typeof dateInput === 'string') {
    date = new Date(dateInput);
  } else {
    return '날짜 오류';
  }

  return date
    .toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      timeZone: 'Asia/Seoul',
    })
    .replace(/\.\s/g, '.')
    .replace(/\.$/, '');
};

// Filter.vue에서 emit된 ref
const selectedDateRange = ref({ start: '', end: '' });
const selectedParticipants = ref([]); // 선택된 참여자 userId 배열
const selectedCategories = ref([]);

const tripMembers = ref([]);
const totalAmount = ref(0);

// Filter.vue로부터 'date-filtered' 이벤트 전달받는 핸들러
function onDateFiltered(range) {
  console.log('[PaymentList.vue] 받은 날짜 범위:', range);
  // 부모 컴포넌트가 선택된 날짜 값을 보관
  selectedDateRange.value = range;
}

// PaymentListInfo에서 전체 총액을 보내줄 때만 저장
function onInitTotal(amount) {
  totalAmount.value = amount;
}

function onParticipantFiltered(userIds) {
  console.log('[Paymentlist.vue] 선택된 결제 참여자: ', userIds);
  selectedParticipants.value = userIds;
}

function onCategoryFiltered(categoryIds) {
  console.log('[PaymentList.vue] 선택된 카테고리: ', categoryIds);
  selectedCategories.value = categoryIds;
}

// PaymentList.vue의 handleTripTerminate 함수 수정
const handleTripTerminate = async () => {
  try {
    const tripId = route.params.tripId;

    // 정산 필요 여부 확인
    const needsSettlement = await checkIfSettlementNeeded();

    // 1. 여행 상태를 CLOSED로 변경 (항상 실행)
    await tripApi.closeTrip(parseInt(tripId));

    if (needsSettlement) {
      // 2-A. 정산이 필요한 경우: 정산 요청 생성 후 정산 페이지로 이동
      await requestSettlement({ tripId: parseInt(tripId) });
      router.push(`/settlement/${tripId}`);
    } else {
      // 2-B. 정산이 불필요한 경우: 홈으로 이동
      alert(
        '여행이 종료되었습니다. 모든 멤버의 결제 금액이 같아 정산이 필요하지 않습니다.'
      );
      await router.push('/'); // 또는 '/trips' 등 적절한 페이지
    }
  } catch (error) {
    console.error('여행 종료 중 오류 발생:', error);
    alert(error.message || '여행 종료 중 오류가 발생했습니다.');
  }
};

//삭제 버튼 클릭 처리하는 함수
const handleDelete = async () => {
  const tripId = route.params.tripId;
  await tripApi.deleteTrip(Number(tripId));
  console.log('id: ' + tripId + ' 여행 삭제');
  alert('여행이 삭제되었습니다.');
  await router.replace(`/`);
};

// 정산 필요 여부 확인 함수
const checkIfSettlementNeeded = async () => {
  try {
    // 1. 멤버 수 확인
    const memberCount = tripStore.currentTripMembers?.length || 0;

    // 1인 여행인 경우 정산 불필요
    if (memberCount <= 1) {
      console.log('1인 여행 - 정산 불필요');
      return false;
    }

    // 2. 멤버별 결제 금액이 모두 같은지 확인
    // 정산 요약 API를 호출해서 멤버별 결제 내역 확인
    const summaryResponse = await getSettlementSummary(route.params.tripId);
    const memberPayments = summaryResponse.data.memberPayments || [];

    if (memberPayments.length === 0) {
      console.log('결제 내역이 없음 - 정산 불필요');
      return false;
    }

    // 모든 멤버의 결제 금액이 같은지 확인
    const firstAmount = memberPayments[0]?.amount || 0;
    const allAmountsSame = memberPayments.every(
      (member) => member.amount === firstAmount
    );

    if (allAmountsSame) {
      console.log('모든 멤버 결제 금액 동일 - 정산 불필요');
      return false;
    }

    console.log('정산 필요');
    return true;
  } catch (error) {
    console.error('정산 필요 여부 확인 실패:', error);
    // 에러 발생 시 안전하게 정산이 필요하다고 가정
    return true;
  }
};

onMounted(async () => {
  await tripStore.fetchTrip(route.params.tripId);
  console.log('currenttrip: ', tripStore.currentTrip);
  if (tripStore.currentTrip) {
    await tripStore.fetchCurrentTripMemberNicknames();
    await checkIsOwner();
    console.log("isOwner: "+ isOwner.value)
    tripStore.currentTrip.tripStatus === 'ACTIVE'
      ? (title.value = '진행 중인 여행')
      : tripStore.currentTrip.tripStatus === 'READY'
      ? (title.value = '예정된 여행')
      : (title.value = '지난 여행');
  }
  if (tripStore.currentTrip.tripStatus === 'CLOSED') {
    isClosed.value = true;
  }
});
</script>

<style scoped>
.content-container {
  flex-grow: 1;
  padding: 1.25rem;
  overflow-y: auto;
  padding-top: 56px;
  position: relative;
  max-width: 384px;
  /* margin: 0 auto; */
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

.floating-button {
  position: absolute;
  bottom: 50px;
  right: calc(50% - 192px + 40px);  /* 화면 가운데 정렬된 384px 카드의 오른쪽 끝에서 20px 안쪽 */
  width: 120px;

  background-color: #ffe499;
  color: #4a4a4a;
  font-weight: bold;
  font-size: 16px;
  padding: 14px 0;
  border: none;
  border-radius: 9999px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  z-index: 1000;
  font-family: 'IBM Plex Sans KR', sans-serif;
}

.floating-button:hover {
  background-color: #ffd166;
}

</style>
