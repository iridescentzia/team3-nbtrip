<script setup>
import Header from '@/components/layout/Header.vue';
import Button from '@/components/common/Button.vue';
import { useRouter } from 'vue-router';
import { useAgreementStore } from '@/stores/agreement.js';

const router = useRouter();
const agreementStore = useAgreementStore();

// 동의 클릭 시 약관 체크 + 뒤로 가기
const agreeAndGoBack = () => {
  agreementStore.check('privacy');
  router.back();
};
</script>

<template>
  <div class="page-content">
    <Header title="개인정보 이용 동의" :backAction="agreeAndGoBack" />

    <div class="content-area">
      <div class="text">
        <h1>개인정보 수집·이용 동의 (필수)</h1>
        <p>
          ※ 테스트 환경 안내: 현재 더미 데이터로 운영되어 실제 금융거래는
          발생하지 않습니다.
        </p>

        <h3>1. 수집하는 개인정보 항목</h3>
        <ul>
          <li>닉네임, 이름, 전화번호, 이메일, 비밀번호(암호화 저장)</li>
          <li>본인 명의 은행 계좌 정보(은행명, 계좌번호) - 1개만 등록 가능</li>
        </ul>

        <h3>2. 개인정보 수집·이용 목적</h3>
        <ul>
          <li>회원 가입 및 본인 확인</li>
          <li>서비스 이용을 위한 회원 식별 (닉네임)</li>
          <li>본인 명의 계좌 1개 등록 및 관리</li>
          <li>계좌 소유주 본인 인증 (정식 오픈 시)</li>
          <li>결제 및 정산 서비스 제공</li>
          <li>그룹 멤버 간 정보 공유 (닉네임, 이름, 결제내역)</li>
          <li>금융거래 내역 관리 및 정산 처리</li>
          <li>고객 상담 및 분쟁 해결 (전화번호, 이메일)</li>
          <li>금융사고 예방 및 부정거래 방지</li>
          <li>중복 계좌/닉네임 등록 방지</li>
          <li>서비스 개선 및 통계 분석</li>
        </ul>

        <h3>3. 개인정보 보유·이용 기간</h3>
        <ul>
          <li>회원 탈퇴 시까지 (단, 관련 법령에 따라 일정 기간 보관)</li>
          <li>금융거래 기록: 5년 (전자금융거래법)</li>
          <li>계좌 인증 정보: 3년 (본인확인 목적)</li>
          <li>로그인 기록: 6개월</li>
          <li>상담 및 분쟁 해결 기록: 3년</li>
        </ul>

        <h3>4. 개인정보 제3자 제공</h3>
        <ul>
          <li>결제대행업체 (정식 오픈 시)</li>
          <li>금융기관 (계좌 연동 및 인증, 자동이체 - 정식 오픈 시)</li>
          <li>그룹 멤버 (정산 시 닉네임, 이름, 결제내역 공유)</li>
          <li>금융감독원 (금융사고 신고 시)</li>
          <li>수사기관, 감독기관 등 법령에 의한 경우</li>
        </ul>

        <h3>5. 본인 명의 계좌 등록 관련</h3>
        <ul>
          <li>본인 명의 1개 계좌만 등록 가능</li>
          <li>정확한 은행명과 계좌번호 입력 필수</li>
          <li>법인 계좌 등록 불가</li>
          <li>계좌번호 중복 확인 및 실계좌 확인 예정</li>
          <li>계좌 변경은 기존 삭제 후 가능 (정산 중일 경우 제한)</li>
          <li>변경 시 그룹원 자동 알림</li>
          <li>타인 명의 등록, 허위 입력 시 서비스 정지 및 법적 책임</li>
        </ul>

        <h3>6. 개인정보 처리 위탁</h3>
        <p>현재 없음 (정식 오픈 시 별도 공지)</p>

        <h3>7. 정보주체의 권리 및 행사방법</h3>
        <ul>
          <li>개인정보 열람, 정정, 삭제, 처리정지 요구 가능</li>
          <li>앱 설정 또는 고객센터를 통해 요청</li>
          <li>만 14세 미만 아동의 경우 법정대리인 통해 가능</li>
        </ul>

        <h3>8. 개인정보 보호책임자</h3>
        <ul>
          <li>성명: [담당자명]</li>
          <li>이메일: [이메일]</li>
          <li>전화번호: [전화번호]</li>
        </ul>

        <h3>9. 동의 거부 권리 및 불이익</h3>
        <p>동의하지 않을 경우 회원가입 및 서비스 이용이 제한될 수 있습니다.</p>

        <h3>10. 개인정보의 안전성 확보조치</h3>
        <ul>
          <li>비밀번호 암호화 저장</li>
          <li>접근권한 및 접속기록 관리</li>
          <li>취급 직원 최소화 및 보안 교육</li>
          <li>시스템 접근 권한 통제</li>
          <li>정식 오픈 시 금융권 수준 보안 적용 예정</li>
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
