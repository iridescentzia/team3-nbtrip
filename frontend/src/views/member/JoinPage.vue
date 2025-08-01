<script setup>
import { ref, computed, onMounted, defineEmits, watch } from 'vue';
import { registerMember, checkNicknameDuplicate } from '@/api/memberApi.js';
import Button from '@/components/common/Button.vue';
import { useRouter } from 'vue-router';
import Header from '@/components/layout/Header.vue';
import {requestPermissionAndGetToken} from "@/firebase.js";

const router = useRouter();
const emit = defineEmits(['signup-complete']);

// 폼 입력 상태
const nickname = ref('');
const name = ref('');
const phoneNumber = ref('');
const email = ref('');
const password = ref('');
const passwordConfirm = ref('');
const bankCode = ref('');
const accountNumber = ref('');

// FCM 토큰
const fcmToken = ref('')  // firebase 연동 후

// FCM 토큰 받아오기(firebase 연동 후)
onMounted(async () => {
  try {
    const token = await requestPermissionAndGetToken();
    if (token) {
      fcmToken.value = token;
      console.log('FCM 토큰 발급 성공:', token);
    } else {
      console.warn('FCM 토큰을 가져오지 못했습니다.');
    }
  } catch (err) {
    console.error('FCM 토큰 발급 오류:', err);
  }
});

// 닉네임 중복 확인 상태
const isNicknameChecked = ref(false);
const nicknameValid = ref(false);
const nicknameMessage = ref('');

// 닉네임 중복 확인(POST /api/users/check-nickname)
const checkNickname = async () => {
  if (!nickname.value.trim()) {
    alert('닉네임을 입력해주세요.');
    return;
  }
  try {
    const res = await checkNicknameDuplicate(nickname.value);
    nicknameValid.value = true;
    nicknameMessage.value = res.message || '사용 가능한 닉네임입니다.';
    isNicknameChecked.value = true;
  } catch (err) {
    nicknameValid.value = false;
    isNicknameChecked.value = false;
    nicknameMessage.value = err.message || '이미 사용 중인 닉네임입니다.';
  }
};

// 이름 유효성 검사
const isNameValid = computed(() => name.value.length >= 2);
const nameMessage = computed(() =>
    name.value
        ? isNameValid.value
            ? '올바른 이름입니다.'
            : '이름은 2자 이상이어야 합니다.'
        : ''
);

// 이메일 유효성 검사
const emailMessage = ref('');
const isEmailValid = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value));
watch(email, () => {
  emailMessage.value = isEmailValid.value ? '올바른 이메일 형식입니다.' : '이메일 형식이 올바르지 않습니다.';
});

// 비밀번호 유효성 검사
const passwordMessage = ref('');
const isPasswordValid = computed(() => /^(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{9,}$/.test(password.value));
watch(password, () => {
  passwordMessage.value = isPasswordValid.value
      ? '사용 가능한 비밀번호입니다.'
      : '영문, 숫자, 특수문자를 포함해 9자 이상이어야 합니다.';
});

// 비밀번호 일치 여부
const isPasswordMatch = computed(
    () => password.value === passwordConfirm.value
);

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
const accountMessage = computed(() =>
    accountNumber.value
        ? isAccountValid.value
            ? '유효한 계좌번호입니다.'
            : '10~14자리 숫자만 입력 가능합니다.'
        : ''
);

// 은행 선택 시 코드 설정
// watch(bankName, () => {
//   const selected = bankList.value.find(b => b.name === bankName.value);
//   bankCode.value = selected ? selected.code : '';
// });

// 은행 코드 리스트(account DB)
const bankCodes = ref([
  { code: '003', name: '기업은행' },
  { code: '004', name: '국민은행' },
  { code: '011', name: '농협은행' },
  { code: '020', name: '우리은행' },
  { code: '023', name: 'SC제일은행' },
  { code: '027', name: '한국시티은행' },
  { code: '081', name: '하나은행' },
  { code: '088', name: '신한은행' },
  { code: '090', name: '카카오뱅크' },
  { code: '092', name: '토스뱅크' },
]);

// 회원가입(POST /api/auth/register)
const submitForm = async () => {
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
    if (!isNicknameChecked.value || !nicknameValid.value) {
      alert('닉네임 중복 확인을 해주세요.');
      return;
    }
    if (!isPasswordMatch.value || !isPasswordValid.value) {
      alert('비밀번호를 다시 확인해주세요.');
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
    try {
    const res = await registerMember({
      email: email.value,
      password: password.value,
      passwordConfirm: passwordConfirm.value,
      nickname: nickname.value,
      name: name.value,
      phoneNumber: phoneNumber.value,
      bankCode: bankCode.value,
      accountNumber: accountNumber.value,
      fcmToken: fcmToken.value,
    });
    if (res.success) {
      alert('회원가입이 완료되었습니다!');
      router.push('/login');
    }
  } catch (err) {
    alert(err.message || '회원가입 실패');
  }
};
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
      <!-- 닉네임 -->
      <label class="label">닉네임</label>
      <div class="nickname-wrapper">
        <input v-model="nickname" type="text" class="nickname-input" />
        <button class="nickname-check-button" @click="checkNickname">
          중복 확인
        </button>
      </div>
      <div v-if="nicknameMessage" class="nickname-check-message">
        <span :class="nicknameValid ? 'success' : 'error'">{{
            nicknameMessage
          }}</span>
      </div>

      <!-- 이름 -->
      <label class="label">이름</label>
      <input v-model="name" type="text" class="input-box" />
      <div class="check">
        <span v-if="nameMessage" :class="isNameValid ? 'success' : 'error'">
          {{ nameMessage }}
        </span>
      </div>

      <!-- 전화번호 -->
      <label class="label">전화번호</label>
      <input
          v-model="phoneNumber"
          type="text"
          class="input-box"
          placeholder="010-1234-5678"
      />
      <div class="check">
        <span v-if="phoneMessage" :class="isPhoneValid ? 'success' : 'error'">
          {{ phoneMessage }}
        </span>
      </div>

      <!-- 이메일 -->
      <label class="label">이메일</label>
      <input v-model="email" type="email" class="input-box" />
      <div class="check">
        <span v-if="emailMessage" :class="isEmailValid ? 'success' : 'error'">
          {{ emailMessage }}
        </span>
      </div>

      <!-- 비밀번호 -->
      <label class="label">비밀번호</label>
      <input v-model="password" type="password" class="input-box" />
      <span class="password-rules">
        • 영문 소문자, 숫자, 특수문자를 포함해 최소 9자리 이상이어야 합니다.<br />
        • 3자 이상의 연속되는 글자, 숫자는 사용이 불가능합니다.
      </span>

      <!-- 비밀번호 확인 -->
      <label class="label">비밀번호 확인</label>
      <input v-model="passwordConfirm" type="password" class="input-box" />
      <div class="check">
        <span v-if="passwordConfirm && !isPasswordMatch" class="error"
        >비밀번호가 동일하지 않습니다.</span
        >
        <span v-if="passwordConfirm && isPasswordMatch" class="success"
        >비밀번호가 동일합니다.</span
        >
      </div>

      <!-- 은행 선택 -->
      <label class="label">은행 선택</label>
      <div class="select-box">
        <select v-model="bankCode" class="input-box">
          <option disabled value="">은행을 선택하세요</option>
          <option v-for="bank in bankCodes" :key="bank.code" :value="bank.code">
            {{ bank.name }}
          </option>
        </select>
        <div class="dropdown-icon"></div>
      </div>

      <!-- 계좌번호 -->
      <label class="label">계좌번호</label>
      <input v-model="accountNumber" type="text" class="input-box" />
      <div class="check">
        <span v-if="accountMessage" :class="isAccountValid ? 'success' : 'error'">
          {{ accountMessage }}
        </span>
      </div>
    </div>

    <!-- 회원가입 버튼 -->
    <div class="bottom-fixed">
      <Button label="회원가입" @click="submitForm" />
    </div>
  </div>
</template>

<style scoped>
.join-content {
  width: 100%;
  height: 100%;
  background: #f8fafc;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 스크롤은 form-area에서 처리 */
}

/* 폼 영역이 스크롤되도록 수정 */
.form-area {
  flex: 1; /* 남은 공간을 모두 차지 */
  overflow-y: auto; /* 내용이 길어지면 스크롤 */
  padding: 24px;
  padding-top: calc(30px + 10px); /* Header 높이만큼 여백 추가 */
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
}

.nickname-check-message {
  font-size: 12px;
  margin-top: 4px;
}

.success {
  color: #61a569;
}

.error {
  color: #a76a6a;
}

.password-rules {
  font-size: 10px;
  color: #9a9595;
  margin-top: 8px;
  line-height: 1.5;
}

.check {
  font-size: 10px;
  margin-top: 8px;
}
</style>