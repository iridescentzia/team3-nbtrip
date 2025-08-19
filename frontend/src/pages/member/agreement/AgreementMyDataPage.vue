<script setup>
import Header from '@/components/layout/Header.vue';
import Button from '@/components/common/Button.vue';
import { useRouter, useRoute } from 'vue-router';
import { useAgreementStore } from '@/stores/agreementStore.js';

const router = useRouter();
const agreementStore = useAgreementStore();

// 동의 클릭 시 약관 체크 + 뒤로 가기
const agreeAndGoBack = () => {
  agreementStore.check('myData');
  router.back();
};

// 동의 버튼(회원가입/마이페이지)
const isViewOnly = useRoute().query.viewOnly === 'true'
</script>

<template>
  <div class="page-content">
    <Header title="마이데이터 활용 동의" :backAction="agreeAndGoBack" />

    <div class="content-area">
      <div class="text">
        <h1>마이데이터 수집·활용 동의 (필수)</h1>
        <p>
          ※ 테스트 환경 안내: 이 화면은 동의 UI 시연용이며, 현재는 더미 데이터만 사용합니다. 실제 금융정보 연동·거래는 발생하지 않습니다.
        </p>

        <h3>1. 마이데이터 서비스란?</h3>
        <ul>
          <li>고객의 금융정보를 안전하게 수집·분석하여 개인 맞춤형 금융서비스를 제공하는 서비스</li>
          <li>고객이 직접 동의한 범위 내에서만 정보 수집 및 활용</li>
        </ul>

        <h3>2. 수집·활용하는 마이데이터 정보</h3>
        <ul>
          <li>계좌 기본정보 (은행명, 계좌번호, 계좌 잔액)</li>
          <li>거래내역 (입출금 내역, 거래 상대방, 거래 목적)</li>
          <li>결제정보 (카드 사용 내역, 온라인 결제 기록)</li>
          <li>여행 관련 소비 (숙박, 교통, 식음료 등 여행 카테고리 거래)</li>
          <li>정산 관련 송금 내역</li>
        </ul>

        <h3>3. 마이데이터 활용 목적</h3>
        <ul>
          <li>자동 정산: 여행 중 소비 패턴 분석하여 정확한 정산</li>
          <li>가계부 자동화: 결제 내역을 자동으로 카테고리 분류</li>
          <li>예산 관리: 여행 예산 대비 실제 소비 분석</li>
          <li>추천 서비스: 과거 여행 패턴 기반 맞춤 추천</li>
          <li>부정거래 방지: 이상 거래 패턴 탐지 및 알림</li>
          <li>그룹 정산 최적화: 복잡한 채무관계 자동 계산</li>
        </ul>

        <h3>4. 정보 제공 금융회사</h3>
        <ul>
          <li>국민은행, 신한은행, 우리은행, 하나은행, 기업은행</li>
          <li>농협은행, SC제일은행, 한국씨티은행</li>
          <li>카카오뱅크, 토스뱅크 등 인터넷 전문은행</li>
          <li>정식 오픈 시 추가 금융기관 연동 예정</li>
          <li>※ 현재 단계: 실제 연동 없음, 목업 데이터만 사용</li>
        </ul>

        <h3>5. 정보 이용·제3자 제공</h3>
        <ul>
          <li>내부 이용: 서비스 개선, 맞춤형 기능 제공</li>
          <li>그룹 내 공유: 정산 목적으로 여행 그룹원과 거래내역 공유</li>
          <li>제3자 제공: 고객 별도 동의 시에만 제공</li>
          <li>법정 요구: 관련 법령에 따른 제공</li>
          <li>통계 분석: 개인식별 불가능한 형태로 서비스 개선에 활용</li>
        </ul>

        <h3>6. 보유·이용 기간</h3>
        <ul>
          <li>기본 보유 기간: 동의 철회 시까지</li>
          <li>여행 종료 후: 3개월 (정산 분쟁 대비)</li>
          <li>법정 보관: 5년 (전자금융거래법 제22조)</li>
          <li>통계 분석용: 개인식별 불가 형태로 보관</li>
          <li>회원 탈퇴 시: 즉시 삭제 (법정 보관 기간 제외)</li>
        </ul>

        <h3>7. 동의 철회 및 정보 삭제</h3>
        <ul>
          <li>철회 방법: 앱 설정 > 마이데이터 관리 > 동의 철회</li>
          <li>즉시 처리: 철회 즉시 신규 정보 수집 중단</li>
          <li>기존 정보: 법정 보관 기간 외 즉시 삭제</li>
          <li>서비스 제한: 철회 시 자동 정산 등 일부 기능 이용 제한</li>
        </ul>

        <h3>8. 정보보호 및 보안</h3>
        <ul>
          <li>암호화: 전송 및 저장 시 최고 수준 암호화 적용</li>
          <li>접근 통제: 권한자만 접근, 모든 접근 기록 보관</li>
          <li>※ 개발 단계: 기본 보안만 적용, 정식 서비스 시 금융권 수준 보안</li>
        </ul>

        <h3>9. 마이데이터 사업자 정보</h3>
        <ul>
          <li>사업자: 여행공유 서비스</li>
          <li>등록번호: 정식 서비스 시 금융위원회 등록 예정</li>
          <li>연락처: 고객센터 1588-0000 (정식 오픈 시)</li>
          <li>※ 현재 단계: 마이데이터 사업자 미등록 상태</li>
        </ul>

        <h3>10. 동의 거부 시 불이익</h3>
        <ul>
          <li>마이데이터 수집·활용에 동의하지 않을 경우 다음 서비스가 제한됩니다:</li>
          <li>스마트 정산 기능</li>
          <li>소비 패턴 분석 기능</li>
          <li>그룹 여행 기능</li>
          <li>QR 결제 기능</li>
        </ul>

        <h3>11. 여행 그룹 서비스 특성</h3>
        <ul>
          <li>그룹 정보 공유: 같은 여행 그룹 내에서 결제내역, 정산 정보 공유</li>
          <li>실시간 동기화: 모든 그룹원에게 거래내역 실시간 공유</li>
          <li>자동 정산: 여행 종료 시 복잡한 채무관계 자동 계산 및 처리</li>
          <li>임시 그룹: 여행 종료 후 일정 기간 후 그룹 정보 자동 정리</li>
        </ul>

        <h3>12. 개발 단계 특별 고지사항</h3>
        <ul>
          <li>실제 연동 불가: 마이데이터 사업자 미등록으로 실제 금융정보 수집 불가</li>
          <li>시뮬레이션만: 동의 절차 및 UI/UX만 구현된 상태</li>
          <li>목업 데이터: 가상의 거래내역으로만 서비스 시연</li>
          <li>금융 API 수취 조회 기능이 없어, 실제 계좌 내역 조회는 불가하며 '조회 완료' 상태를 가정하여 화면을 구성함</li>
          <li>정식 서비스 시, 계좌 소유주 확인을 위해 1원 입금 후 인증번호를 확인하는 절차(계좌 1원 입금 인증)를 적용할 계획임</li>
          <li>향후 계획: 정식 서비스 시 관련 인허가 취득 후 실제 연동 예정</li>
          <li>데이터 보관: 개발 완료 후 모든 테스트 데이터 삭제 예정</li>
        </ul>
      </div>
    </div>

    <footer v-if="!isViewOnly" class="bottom-fixed">
      <Button label="동의합니다" @click="agreeAndGoBack" />
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
  padding-top: calc(50px + 20px);
  box-sizing: border-box;
}

/* 하단 버튼을 화면 맨 아래에 고정 */
.bottom-fixed {
  padding: 16px 24px;
  background-color: #f8fafc;
}

/* 약관 텍스트 스타일 */
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

/* 하단 버튼 스타일 */
button {
  width: 100%;
  margin: 16px 24px;
  flex-shrink: 0;
}
</style>