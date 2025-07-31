<script setup>
import Header from '@/components/layout/Header.vue'
import Button from '@/components/common/Button.vue'
import { useRouter } from 'vue-router'
import { useAgreementStore} from "@/stores/agreement.js";

const router = useRouter()
const agreementStore = useAgreementStore()

// 동의 클릭 시 약관 체크 + 뒤로 가기
const agreeAndGoBack = () => {
  agreementStore.check('terms')
  router.back()
}

// 회원가입/마이페이지 뷰 전환용
const props = defineProps({
  viewOnly : {
    type: Boolean,
    default: false
  }
})
</script>

<template>
  <div class="page-container">
    <Header title="이용 약관 동의" :back-action="agreeAndGoBack"/>

    <div class="content-area">
      <div class="text">
        <h1>이용 약관 동의 (필수)</h1>

        <h3>제1조 (목적)</h3>
        <p>본 약관은 [N빵 트립] 서비스 이용에 관한 기본적인 사항을 규정합니다.</p>

        <h3>제2조 (서비스 내용)</h3>
        <ul>
          <li>여행 그룹 생성 및 관리</li>
          <li>QR/바코드 결제 서비스 (테스트 환경)</li>
          <li>그룹 가계부 및 정산 서비스</li>
          <li>실시간 결제 내역 공유</li>
        </ul>

        <h3>제3조 (회원의 의무)</h3>
        <ul>
          <li>정확한 개인정보 제공 (닉네임, 이름, 전화번호, 이메일)</li>
          <li>고유한 닉네임 사용 (중복 불가)</li>
          <li>타인의 결제수단 무단 사용 금지</li>
          <li>그룹 내 허위 결제 내역 입력 금지</li>
          <li>서비스 부정 이용 금지</li>
          <li>본인 명의 계좌만 등록 및 사용 (타인 명의 계좌 등록 금지)</li>
          <li>1인당 1개의 계좌만 등록 (중복 계좌 등록 금지)</li>
        </ul>

        <h3>제4조 (서비스 이용 제한)</h3>
        <ul>
          <li>미성년자는 법정대리인 동의 필요</li>
          <li>신용불량자 등 금융거래 제한자는 일부 기능 제한</li>
        </ul>

        <h3>제5조 (계좌 등록 및 관리)</h3>
        <ul>
          <li>본인 명의 계좌만 등록 가능 (현재는 테스트 환경으로 더미 데이터 사용)</li>
          <li>회원당 최대 1개의 계좌만 등록 허용</li>
          <li>은행명과 계좌번호 정확히 입력 필수</li>
          <li>계좌 소유자와 회원 정보 일치 필요 (향후 실제 인증 시스템 도입 예정)</li>
          <li>서비스 정식 오픈 시 금융기관 연동을 통한 실계좌 인증 절차 적용</li>
          <li>허위 계좌 정보 입력 시 법적 책임 부담</li>
          <li>계좌 변경 시 기존 계좌 삭제 후 새 계좌 등록 가능</li>
        </ul>

        <h3>제6조 (닉네임 관리)</h3>
        <ul>
          <li>고유한 닉네임 사용 필수 (중복 불가)</li>
          <li>욕설, 비속어, 타인의 권리를 침해하는 닉네임 사용 금지</li>
          <li>닉네임 변경 시 그룹 내 다른 멤버들에게 변경 내용 자동 공지</li>
        </ul>

        <h3>제7조 (결제 및 정산)</h3>
        <ul>
          <li>결제 오류 시 즉시 신고 의무</li>
          <li>정산 완료 후 이의제기는 7일 이내</li>
          <li>그룹 탈퇴 시 정산 완료 후 가능</li>
          <li>자동 정산 시 등록된 본인 명의 계좌에서만 입출금 처리 (정식 오픈 시)</li>
          <li>계좌 미등록 시 정산 참여 불가</li>
        </ul>

        <h3>제8조 (면책사항)</h3>
        <ul>
          <li>회원 간 금전 분쟁에 대한 중재 역할만 수행</li>
          <li>천재지변 등 불가항력으로 인한 서비스 중단 시 면책</li>
          <li>타인 명의 계좌 사용으로 발생하는 모든 문제에 대해 면책</li>
          <li>회원이 입력한 부정확한 계좌 정보로 인한 문제에 대해 면책</li>
        </ul>
      </div>
    </div>

    <Button v-if="!viewOnly" label="동의합니다." @click="agreeAndGoBack" />
  </div>
</template>

<style scoped>
.page-container {
  width: 384px;
  height: 800px;
  position: relative;
  background: #f8fafc;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: auto;
  border-radius: 24px;
  outline: 1px solid black;
  outline-offset: -1px;
  margin: 0 auto;
  padding: 32px 24px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

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
</style>