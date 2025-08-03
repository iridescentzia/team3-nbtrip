<script setup>
import Header from '@/components/layout/Header.vue';
import Button from '@/components/common/Button.vue';
import { useRouter } from 'vue-router';
import { useAgreementStore } from '@/stores/agreement.js';

const router = useRouter();
const agreementStore = useAgreementStore();

// 동의 클릭 시 약관 체크 + 뒤로 가기
const agreeAndGoBack = () => {
  agreementStore.check('push');
  router.back();
};
</script>

<template>
  <div class="page-content">
    <!-- Header에 backAction 전달 -->
    <Header title="푸시 알림 수신 동의" :backAction="agreeAndGoBack" />

    <div class="content-area">
      <div class="text">
        <h1>푸시 알림 수신 동의 (필수)</h1>
        <p>
          N빵페이는 서비스 이용 중 다음과 같은 정보를 푸시 알림을 통해 안내드릴
          수 있습니다.<br />
          본 항목에 동의하실 경우, 아래 항목에 해당하는 푸시 알림을 앱을 통해
          수신하게 됩니다.
        </p>

        <h3>1. 발송하는 알림 유형</h3>
        <ul>
          <li>
            <strong>정산 관련 알림</strong>
            <ul>
              <li>정산 요청 수신 시 알림</li>
              <li>정산 완료 시 알림</li>
              <li>미정산 상태 장기화 시 리마인드 알림</li>
            </ul>
          </li>
          <li>
            <strong>거래/결제 관련 알림</strong>
            <ul>
              <li>결제 완료/실패 알림</li>
              <li>송금 요청 및 입금 확인 안내</li>
              <li>계좌 등록 및 변경 알림</li>
              <li>비정상 거래 탐지 알림</li>
              <li>중복 계좌 등록 시도 알림</li>
            </ul>
          </li>
          <li>
            <strong>보안 관련 알림</strong>
            <ul>
              <li>로그인 알림</li>
              <li>비밀번호 변경 알림</li>
              <li>타인 명의 계좌 등록 시도 알림</li>
              <li>부정 거래 탐지 알림</li>
            </ul>
          </li>
          <li>
            <strong>그룹 관련 알림</strong>
            <ul>
              <li>그룹 초대 알림</li>
              <li>그룹 탈퇴 관련 알림</li>
            </ul>
          </li>
          <li>
            <strong>서비스 이용 편의를 위한 알림</strong>
            <ul>
              <li>시스템 점검 및 공지 알림</li>
              <li>서비스 장애 및 복구 알림</li>
            </ul>
          </li>
        </ul>

        <h3>2. 알림 발송 목적</h3>
        <ul>
          <li>금융거래 안전성 확보 및 실시간 안내</li>
          <li>투명한 정산 진행 및 완료 알림</li>
          <li>미정산 방지를 위한 리마인드</li>
          <li>중요한 서비스 변경사항 전달</li>
          <li>계좌 보안 사항 즉시 통지</li>
          <li>부정 거래/계좌 등록 시도 방지</li>
          <li>서비스 이용 편의 향상</li>
        </ul>

        <h3>3. 발송 시간</h3>
        <ul>
          <li>필수 알림: 24시간 발송 (보안, 정산의 긴급성 고려)</li>
          <li>시스템 점검 및 긴급 공지: 발생 시 즉시 발송</li>
        </ul>

        <h3>4. 알림 내용 및 개인정보</h3>
        <ul>
          <li>알림에 거래 금액, 정산 현황 포함 가능</li>
          <li>민감 정보는 요약 형태로만 표시</li>
          <li>자세한 내용은 앱 로그인 후 확인 가능</li>
        </ul>
      </div>
    </div>

    <footer class="bottom-fixed">
      <Button label="동의합니다." @click="agreeAndGoBack" />
    </footer>
  </div>
</template>

<style scoped>
.page-content {
  width: 100%;
  height: 100%;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 본문 스크롤 영역 */
.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  padding-top: calc(50px + 20px); /* Header 높이만큼 여백 추가 */
  box-sizing: border-box;
}

/* 하단 버튼을 화면 맨 아래에 고정 */
.bottom-fixed {
  padding: 16px 24px;
  background-color: #f8fafc;
}

/* 텍스트 영역 */
.text {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
}

.text h1 {
  font-size: 20px;
  margin-bottom: 16px;
}

.text h3 {
  font-size: 16px;
  margin-top: 20px;
  margin-bottom: 8px;
  font-weight: bold;
}

.text ul {
  padding-left: 20px;
  margin-bottom: 16px;
}

.text ul li {
  margin-bottom: 6px;
}

/* 하단 버튼 */
button {
  width: 100%;
  margin: 16px 24px;
  flex-shrink: 0;
}
</style>
