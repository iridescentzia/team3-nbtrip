<script setup>
import { ref, computed, onMounted, defineEmits, watch, onUnmounted } from 'vue';
import { registerMember, checkNicknameDuplicate, sendVerificationEmail, verifyEmailCode } from '@/api/memberApi.js';
import Button from '@/components/common/Button.vue';
import { useRouter } from 'vue-router';
import Header from '@/components/layout/Header.vue';
import accountApi from "@/api/accountApi.js";

const router = useRouter();
const emit = defineEmits(['signup-complete']);

/**
 * =================================================
 * 기본 회원정보 입력 상태 관리
 * =================================================
 */
const nickname = ref('');
const name = ref('');
const phoneNumber = ref('');
const email = ref('');
const password = ref('');
const passwordConfirm = ref('');
const bankCode = ref('');
const accountNumber = ref('');
const isSubmitting = ref(false);

/**
 * =================================================
 * 이메일 인증 관련 상태 관리 (JWT 토큰 방식)
 * =================================================
 */
const isEmailSent = ref(false);
const isEmailVerified = ref(false);
const verificationCode = ref('');
const verificationToken = ref('');
const emailMessage = ref('');
const isResending = ref(false);
const resendCooldown = ref(0);
const resendTimer = ref(null);

/**
 * =================================================
 * 계좌 인증 관련 상태 관리
 * =================================================
 */
const isAccountVerified = ref(false);
const accountMessage = ref('');

/**
 * =================================================
 * 닉네임 중복 확인 상태 관리
 * =================================================
 */
const isNicknameChecked = ref(false);
const nicknameValid = ref(false);
const nicknameMessage = ref('');

/**
 * =================================================
 * 은행 목록 데이터
 * =================================================
 */
const bankList = ref([]);

/**
 * =================================================
 * 전화번호 자동 포맷팅 함수
 * =================================================
 */
// 전화번호 자동 포맷팅
const formatPhoneNumber = (value) => {
  const numbers = value.replace(/[^\d]/g, '');
  if (numbers.length <= 3) {
    return numbers;
  } else if (numbers.length <= 7) {
    return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
  } else {
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
  }
};

// 전화번호 입력 시 자동 포맷팅 처리
const handlePhoneInput = (event) => {
  const input = event.target;
  const cursorPosition = input.selectionStart;
  const beforeLength = phoneNumber.value.length;
  phoneNumber.value = formatPhoneNumber(input.value);
  const afterLength = phoneNumber.value.length;
  const newCursorPosition = cursorPosition + (afterLength - beforeLength);
  setTimeout(() => {
    input.setSelectionRange(newCursorPosition, newCursorPosition);
  });
};

/**
 * =================================================
 * 이메일 관련 처리
 * =================================================
 */
const isEmailValid = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value));

// 이메일 변경 시 인증 상태 초기화
watch(email, () => {
  if (isEmailVerified.value) {
    isEmailVerified.value = false;
    isEmailSent.value = false;
    verificationCode.value = '';
    verificationToken.value = '';
  }

  emailMessage.value = email.value ?
      (isEmailValid.value ? '올바른 이메일 형식입니다.' : '이메일 형식이 올바르지 않습니다.') : '';
});

// JWT 토큰 기반 이메일 인증번호 발송
const sendEmailVerification = async () => {
  if (!isEmailValid.value) {
    alert('올바른 이메일 형식을 입력해주세요.');
    return;
  }

  try {
    const response = await sendVerificationEmail(email.value);

    if (response && response.success) {
      verificationToken.value = response.verificationToken;

      isEmailSent.value = true;
      emailMessage.value = '인증번호를 이메일로 발송했습니다. 이메일을 확인해주세요.';
      alert('인증번호를 이메일로 발송했습니다.\n메일함(스팸함 포함)을 확인해주세요.');
    } else {
      throw new Error('이메일 발송에 실패했습니다.');
    }
  } catch (error) {
    console.error('❌ 이메일 발송 에러:', error);

    if (error.message?.includes('이미 가입된') || error.message?.includes('중복')) {
      alert('이미 가입된 이메일입니다.');
    } else if (error.message?.includes('형식') || error.message?.includes('유효하지')) {
      alert('올바른 이메일 형식을 입력해주세요.');
    } else if (error.message?.includes('서버')) {
      alert('서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
    } else {
      alert('이메일 발송에 실패했습니다. 다시 시도해주세요.');
    }
  }
};

// JWT 토큰 기반 이메일 인증번호 확인 함수 - 완전 수정
const verifyEmailCodeSubmit = async () => {
  if (!verificationCode.value.trim()) {
    alert('인증번호를 입력해주세요.');
    return;
  }

  if (!/^\d{6}$/.test(verificationCode.value)) {
    alert('인증번호는 6자리 숫자를 입력해주세요.');
    return;
  }

  try {
    const response = await verifyEmailCode({
      verificationToken: verificationToken.value,
      code: verificationCode.value,
      email: email.value
    });

    if (response.success || response.verified) {
      isEmailVerified.value = true;
      emailMessage.value = '이메일 인증이 완료되었습니다.';
      alert('이메일 인증이 완료되었습니다!');

      // 인증 완료 후 정리
      verificationToken.value = '';
      verificationCode.value = '';

      console.log('✅ 이메일 인증 완료');
    } else {
      throw new Error('인증번호가 올바르지 않습니다.');
    }
  } catch (error) {
    console.error('❌ 이메일 인증 실패:', error);

    if (error.message?.includes('만료')) {
      alert('인증번호가 만료되었습니다. 새로운 인증번호를 발송해주세요.');
      isEmailSent.value = false;
      verificationToken.value = '';
      verificationCode.value = '';
    } else if (error.message?.includes('토큰') || error.message?.includes('Token')) {
      alert('인증 정보에 문제가 있습니다. 인증번호를 다시 발송해주세요.');
      isEmailSent.value = false;
      verificationToken.value = '';
      verificationCode.value = '';
    } else if (error.response?.status === 400) {
      alert('잘못된 인증번호입니다. 다시 확인해주세요.');
    } else if (error.response?.status === 500) {
      alert('서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
    } else {
      alert('인증번호가 올바르지 않습니다. 다시 확인해주세요.');
    }
  }
};

// 인증번호 재발송 - 새로운 토큰 발급
const resendEmail = async () => {
  if (isResending.value || resendCooldown.value > 0) return;

  try {
    isResending.value = true;

    const response = await sendVerificationEmail(email.value);

    if (response.success && response.verificationToken) {
      verificationToken.value = response.verificationToken;
      verificationCode.value = '';
      alert('인증번호를 다시 발송했습니다.');

      // 60초 쿨다운 타이머 시작
      resendCooldown.value = 60;
      resendTimer.value = setInterval(() => {
        resendCooldown.value--;
        if (resendCooldown.value <= 0) {
          clearInterval(resendTimer.value);
        }
      }, 1000);
    }
  } catch (error) {
    console.error('인증번호 재발송 실패:', error);
    alert('인증번호 재발송에 실패했습니다.');
  } finally {
    isResending.value = false;
  }
};

// 이메일 메시지 색상 클래스
const getEmailMessageClass = () => {
  if (isEmailVerified.value) {
    return 'success';
  } else if (isEmailValid.value && !isEmailSent.value) {
    return 'success';
  } else if (isEmailSent.value && !isEmailVerified.value) {
    return 'info';
  } else if (!isEmailValid.value && email.value) {
    return 'error';
  } else {
    return '';
  }
};

/**
 * =================================================
 * 닉네임 중복 확인 - 백엔드 500 에러 대응
 * =================================================
 */
// 닉네임 변경 시 상태 초기화
watch(nickname, () => {
  if (isNicknameChecked.value) {
    isNicknameChecked.value = false;
    nicknameValid.value = false;
    nicknameMessage.value = '';
  }
});

// 닉네임 중복 확인 - 백엔드 응답 구조 맞춤
const checkNickname = async () => {
  if (!nickname.value.trim()) {
    alert('닉네임을 입력해주세요.');
    return;
  }

  try {
    const response = await checkNicknameDuplicate(nickname.value);

    if (response.available) {
      nicknameValid.value = true;
      nicknameMessage.value = '사용 가능한 닉네임입니다.';
      isNicknameChecked.value = true;
    } else {
      nicknameValid.value = false;
      isNicknameChecked.value = false;
      nicknameMessage.value = response.message || '이미 사용 중인 닉네임입니다.';
    }
  } catch (error) {
    console.error('닉네임 중복 확인 실패:', error);
    nicknameValid.value = false;
    isNicknameChecked.value = false;

    // 500 에러에 대한 사용자 친화적 메시지
    if (error.message?.includes('서버')) {
      nicknameMessage.value = '서버에 일시적인 문제가 발생했습니다.';
    } else {
      nicknameMessage.value = '닉네임 확인 중 오류가 발생했습니다.';
    }
  }
};

/**
 * =================================================
 * 계좌 인증 (목업)
 * =================================================
 */
const verifyAccount = async () => {
  if (!bankCode.value) {
    alert('은행을 선택해주세요.');
    return;
  }
  if (!isAccountValid.value) {
    alert('올바른 계좌번호를 입력해주세요.');
    return;
  }

  const selectedBank = bankList.value.find(b => b.bankCode === bankCode.value);
  const bankName = selectedBank ? selectedBank.bankName : '선택된 은행';

  // 1초 대기로 실제 API 호출하는 것처럼 연출
  setTimeout(() => {
    isAccountVerified.value = true;
    accountMessage.value = `${bankName} 계좌 인증이 완료되었습니다. (1원 입금 확인)`;
    alert(`${bankName} 계좌로 1원을 입금했습니다.\n계좌 인증이 완료되었습니다!`);
  }, 1000);
};

/**
 * =================================================
 * 폼 유효성 검사
 * =================================================
 */
// 이름 유효성 검사
const isNameValid = computed(() => name.value.length >= 2);
const nameMessage = computed(() =>
    name.value
        ? isNameValid.value
            ? '올바른 이름입니다.'
            : '이름은 2자 이상이어야 합니다.'
        : ''
);

// 비밀번호 유효성 검사
const isPasswordValid = computed(() => /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{9,}$/.test(password.value));
const passwordMessage = computed(() =>
    password.value
        ? isPasswordValid.value
            ? '사용 가능한 비밀번호입니다.'
            : '영문, 숫자, 특수문자를 포함해 9자 이상이어야 합니다.'
        : ''
);

// 비밀번호 일치 여부
const isPasswordMatch = computed(() => password.value === passwordConfirm.value);

// 전화번호 유효성 검사
const isPhoneValid = computed(() => /^010-\d{4}-\d{4}$/.test(phoneNumber.value));
const phoneMessage = computed(() =>
    phoneNumber.value
        ? isPhoneValid.value
            ? '유효한 전화번호입니다.'
            : '010-1234-5678 형식으로 입력해주세요.'
        : ''
);

// 계좌번호 유효성 검사
const isAccountValid = computed(() => /^\d{10,14}$/.test(accountNumber.value));

/**
 * =================================================
 * 은행 목록 로드 - 500 에러 대응 개선
 * =================================================
 */
onMounted(async () => {
  await loadBankList();
});

// 은행 목록 조회 - 실패 시 기본값 사용
const loadBankList = async () => {
  try {
    const banks = await accountApi.getBankList();
    bankList.value = banks;
    console.log('✅ 은행 목록 API 로드 성공');
  } catch (error) {
    console.error('❌ 은행 목록 API 실패:', error);

    // 기본 은행 목록 사용
    bankList.value = [
      { bankCode: '003', bankName: '기업은행' },
      { bankCode: '004', bankName: '국민은행' },
      { bankCode: '011', bankName: '농협은행' },
      { bankCode: '020', bankName: '우리은행' },
      { bankCode: '023', bankName: 'SC제일은행' },
      { bankCode: '027', bankName: '한국씨티은행' },
      { bankCode: '081', bankName: '하나은행' },
      { bankCode: '088', bankName: '신한은행' },
      { bankCode: '090', bankName: '카카오뱅크' },
      { bankCode: '092', bankName: '토스뱅크' },
    ];
    console.warn('은행 목록 API 로드 실패, 기본 목록 사용');
  }
};

/**
 * =================================================
 * 회원가입 처리 - 백엔드 MemberDTO 구조 맞춤
 * =================================================
 */
const getBankNameByCode = (code) => {
  const bank = bankList.value.find(b => b.bankCode === code);
  return bank ? bank.bankName : '';
};

// 회원가입 처리 - 백엔드 구조에 맞춘 데이터 전송
const submitForm = async () => {
  // 필수 입력 검증
  if (
      !nickname.value ||
      !name.value ||
      !phoneNumber.value ||
      !email.value ||
      !password.value ||
      !passwordConfirm.value ||
      !bankCode.value ||
      !accountNumber.value
  ) {
    alert('모든 항목을 입력해주세요.');
    return;
  }

  // 개별 유효성 검증
  if (!isNicknameChecked.value || !nicknameValid.value) {
    alert('닉네임 중복 확인을 해주세요.');
    return;
  }
  if (!isPasswordValid.value) {
    alert('비밀번호는 영문, 숫자, 특수문자를 포함해 9자 이상이어야 합니다.');
    return;
  }
  if (!isPasswordMatch.value) {
    alert('비밀번호가 일치하지 않습니다.');
    return;
  }
  if (!isPhoneValid.value) {
    alert('전화번호 형식을 확인해주세요.');
    return;
  }
  if (!isAccountValid.value) {
    alert('계좌번호 형식을 확인해주세요.');
    return;
  }
  if (!isEmailVerified.value) {
    alert('이메일 인증을 완료해주세요.');
    return;
  }
  if (!isAccountVerified.value) {
    alert('계좌 인증을 완료해주세요.');
    return;
  }

  if (isSubmitting.value) return;
  isSubmitting.value = true;

  try {
    // 백엔드 MemberDTO 구조에 맞춰 데이터 전송
    const memberData = {
      email: email.value,
      password: password.value,
      passwordConfirm: passwordConfirm.value, // 백엔드에서 비밀번호 확인 검증
      nickname: nickname.value,
      name: name.value,
      phoneNumber: phoneNumber.value,
      accountNumber: accountNumber.value, // 백엔드 MemberDTO에 포함
      bankName: getBankNameByCode(bankCode.value), // 백엔드 MemberDTO에 포함
      fcmToken: '' // 기본값
    };

    const response = await registerMember(memberData);

    if (response.success !== false) {
      alert(`${nickname.value}님, 회원가입이 완료되었습니다!`);
      router.push('/login');
    } else {
      throw new Error(response.message || '회원가입에 실패했습니다.');
    }
  } catch (error) {
    console.error('❌ 회원가입 실패:', error);

    // 구체적인 에러 메시지 처리
    if (error.message?.includes('이메일')) {
      alert('이메일 정보가 올바르지 않습니다.');
    } else if (error.message?.includes('비밀번호')) {
      alert('비밀번호가 일치하지 않습니다.');
    } else if (error.message?.includes('서버')) {
      alert('서버에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.');
    } else {
      alert(error.message || '회원가입에 실패했습니다.');
    }
  } finally {
    isSubmitting.value = false;
  }
};

/**
 * =================================================
 * 컴포넌트 정리
 * =================================================
 */
onUnmounted(() => {
  if (resendTimer.value) {
    clearInterval(resendTimer.value);
  }
});
</script>

<template>
  <div class="join-content">
    <br />
    <Header title="회원 정보 입력" :back-action="() => router.back()" />

    <div class="form-area">
      <div class="title-area">
        <h1>회원 정보 입력</h1>
        <img src="@/assets/img/sitting_cat.png" class="cat" />
      </div>

      <!-- 🔧 개발 모드 전용 (배포 시 제거) -->
      <div v-if="isDevelopmentMode" class="debug-section">
        <h4>🔧 개발 모드</h4>
        <button @click="mockEmailVerification" class="debug-button">
          이메일 발송 시뮬레이션
        </button>
        <button @click="mockEmailCodeVerification" class="debug-button">
          인증 완료 시뮬레이션 (123456)
        </button>
        <p class="debug-info">
          디버깅 정보:
          isEmailSent={{ isEmailSent }},
          isEmailVerified={{ isEmailVerified }},
          토큰={{ verificationToken ? '있음' : '없음' }}
        </p>
      </div>

      <!-- 닉네임 -->
      <label class="label">닉네임</label>
      <div class="nickname-wrapper">
        <input v-model="nickname" type="text" class="nickname-input" placeholder="2~10자 한글, 영문, 숫자" maxlength="10" />
        <button class="nickname-check-button" @click="checkNickname" :disabled="!nickname.trim()">
          중복 확인
        </button>
      </div>
      <div v-if="nicknameMessage" class="nickname-check-message">
        <span :class="nicknameValid ? 'success' : 'error'">{{ nicknameMessage }}</span>
      </div>

      <!-- 이름 -->
      <label class="label">이름</label>
      <input v-model="name" type="text" class="input-box" placeholder="실명을 입력해주세요" />
      <div class="check">
        <span v-if="nameMessage" :class="isNameValid ? 'success' : 'error'">{{ nameMessage }}</span>
      </div>

      <!-- 전화번호 -->
      <label class="label">전화번호</label>
      <input
          v-model="phoneNumber"
          type="text"
          class="input-box"
          placeholder="휴대폰 번호 (숫자만 입력)"
          maxlength="13"
          @input="handlePhoneInput"
      />
      <div class="check">
        <span v-if="phoneMessage" :class="isPhoneValid ? 'success' : 'error'">{{ phoneMessage }}</span>
      </div>

      <!-- 이메일 -->
      <label class="label">이메일</label>
      <div class="nickname-wrapper">
        <input
            v-model="email"
            type="email"
            class="nickname-input"
            placeholder="example@gmail.com"
            :disabled="isEmailVerified"
        />
        <button
            class="nickname-check-button"
            @click="sendEmailVerification"
            :disabled="!isEmailValid || isEmailVerified"
        >
          {{ isEmailVerified ? '인증완료' : '이메일 인증' }}
        </button>
      </div>
      <div class="check">
        <span v-if="emailMessage" :class="getEmailMessageClass()">
          {{ emailMessage }}
        </span>
      </div>

      <!-- 이메일 인증번호 입력 -->
      <div v-if="isEmailSent && !isEmailVerified" class="verification-section">
        <label class="label">인증번호 (6자리)</label>
        <div class="nickname-wrapper">
          <input
              v-model="verificationCode"
              type="text"
              class="nickname-input"
              placeholder="123456"
              maxlength="6"
              @input="verificationCode = verificationCode.replace(/[^0-9]/g, '')"
          />
          <button class="nickname-check-button" @click="verifyEmailCodeSubmit">
            확인
          </button>
        </div>
        <button
            class="resend-button"
            @click="resendEmail"
            :disabled="isResending || resendCooldown > 0"
        >
          {{ isResending ? '발송 중...' :
            resendCooldown > 0 ? `재발송 (${resendCooldown}초)` : '인증번호 재발송' }}
        </button>
      </div>

      <!-- 비밀번호 -->
      <label class="label">비밀번호</label>
      <input v-model="password" type="password" class="input-box" />
      <div class="check">
        <span v-if="passwordMessage" :class="isPasswordValid ? 'success' : 'error'">{{ passwordMessage }}</span>
      </div>
      <span class="password-rules">
        • 영문 소문자, 숫자, 특수문자를 포함해 최소 9자리 이상이어야 합니다.<br />
        • 3자 이상의 연속되는 글자, 숫자는 사용이 불가능합니다.
      </span>

      <!-- 비밀번호 확인 -->
      <label class="label">비밀번호 확인</label>
      <input v-model="passwordConfirm" type="password" class="input-box" />
      <div class="check">
        <span v-if="passwordConfirm && !isPasswordMatch" class="error">
          비밀번호가 동일하지 않습니다.
        </span>
        <span v-if="passwordConfirm && isPasswordMatch" class="success">
          비밀번호가 동일합니다.
        </span>
      </div>

      <!-- 은행 선택 -->
      <label class="label">은행 선택</label>
      <div class="select-box">
        <select v-model="bankCode" class="input-box">
          <option disabled value="">은행을 선택하세요</option>
          <option v-for="bank in bankList" :key="bank.bankCode" :value="bank.bankCode">
            {{ bank.bankName }}
          </option>
        </select>
      </div>

      <!-- 계좌번호 -->
      <label class="label">계좌번호</label>
      <div class="nickname-wrapper">
        <input
            v-model="accountNumber"
            type="text"
            class="nickname-input"
            placeholder="계좌번호 입력 (10-14자리)"
            :disabled="isAccountVerified"
            @input="accountNumber = accountNumber.replace(/[^0-9]/g, '')"
            maxlength="14"
        />
        <button
            class="nickname-check-button"
            @click="verifyAccount"
            :disabled="!bankCode || !isAccountValid || isAccountVerified"
        >
          {{ isAccountVerified ? '인증완료' : '계좌 인증' }}
        </button>
      </div>
      <div class="check">
        <span v-if="isAccountVerified" class="success">{{ accountMessage }}</span>
        <span v-else-if="accountNumber && !isAccountValid" class="error">
          10~14자리 숫자만 입력 가능합니다.
        </span>
        <span v-else-if="accountNumber && isAccountValid" class="info">
          계좌 인증을 완료해주세요.
        </span>
      </div>
    </div>

    <!-- 회원가입 버튼 -->
    <div class="bottom-fixed">
      <Button
          label="회원가입"
          @click="submitForm"
          :disabled="isSubmitting"
      />
    </div>
  </div>
</template>

<style scoped>
/* 기존 스타일 유지 */
.join-content {
  width: 100%;
  height: 100%;
  background: #f8fafc;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.form-area {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  padding-top: calc(30px + 10px);
  box-sizing: border-box;
}

.title-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  margin-bottom: 16px;
}

.title-area h1 {
  font-size: 22px;
  font-weight: 600;
  margin: 0;
}

.title-area .cat {
  width: 80px;
  height: auto;
}

/* 개발 모드 전용 스타일 */
.debug-section {
  background: #fff3cd;
  border: 2px solid #ffeaa7;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 16px;
}

.debug-section h4 {
  margin: 0 0 10px 0;
  color: #856404;
}

.debug-button {
  background: #ffc107;
  color: #212529;
  border: none;
  border-radius: 6px;
  padding: 8px 12px;
  margin-right: 8px;
  margin-bottom: 8px;
  font-size: 12px;
  cursor: pointer;
}

.debug-button:hover {
  background: #e0a800;
}

.debug-info {
  font-size: 11px;
  color: #6c757d;
  margin: 10px 0 0 0;
}

.label {
  font-size: 14px;
  font-weight: 500;
  margin-top: 16px;
  display: block;
}

.input-box {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: white;
  padding: 0 12px;
  margin-top: 4px;
  box-sizing: border-box;
}

.nickname-wrapper {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-top: 4px;
}

.nickname-input {
  flex: 1;
  height: 52px;
  padding: 0 12px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  background: white;
  box-sizing: border-box;
}

.nickname-check-button {
  width: 84px;
  height: 52px;
  background: #fddf99;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #2e363a;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.nickname-check-button:hover:not(:disabled) {
  background: #fcd34d;
}

.nickname-check-button:disabled {
  background: #e2e8f0;
  color: #a0aec0;
  cursor: not-allowed;
}

.nickname-check-message {
  font-size: 10px;
  margin-top: 8px;
}

.success {
  color: #61a569;
}

.error, .info {
  color: #a76a6a;
}

.password-rules {
  font-size: 10px;
  color: #9a9595;
  margin-top: 8px;
  line-height: 1.5;
  display: block;
}

.check {
  font-size: 10px;
  margin-top: 8px;
  min-height: 14px;
}

.verification-section {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  margin-top: 8px;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.resend-button {
  width: 100%;
  height: 40px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #4a5568;
  cursor: pointer;
  margin-top: 8px;
  transition: all 0.2s ease;
}

.resend-button:hover:not(:disabled) {
  border-color: #cbd5e1;
  background: #f7fafc;
}
</style>