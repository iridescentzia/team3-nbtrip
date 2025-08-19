<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/layout/Header.vue'
import Button from '@/components/common/Button.vue'
import { checkNicknameDuplicate, updateMyInfo, getMyInfo } from '@/api/memberApi.js'

const router = useRouter()

// 사용자 정보 상태
const nickname = ref('');
const originalNickname = ref('');
const name = ref('');
const phoneNumber = ref('');
const maskedPhoneNumber = ref('');
const email = ref('');
const password = ref('');
const passwordConfirm = ref('');

// 비밀번호 변경 여부 추적 상태
const isPasswordChanged = ref(false);

// 닉네임 중복 확인 상태
const isNicknameChecked = ref(false);
const nicknameValid = ref(false);
const nicknameMessage = ref('');
const nicknameMessageType = ref('');
const isCheckingNickname = ref(false);

// 닉네임 중복 확인(POST /api/users/check-nickname)
const checkNickname = async () => {
  if (!nickname.value || nickname.value.trim().length === 0) {
    nicknameMessage.value = '닉네임을 입력해주세요.';
    nicknameMessageType.value = 'error';
    isNicknameChecked.value = false;
    return;
  }

  try {
    isCheckingNickname.value = true;
    const result = await checkNicknameDuplicate(nickname.value.trim());

    if (result.available) {
      nicknameMessage.value = result.message || '사용 가능한 닉네임입니다.';
      isNicknameChecked.value = true;
      nicknameValid.value = true;
      nicknameMessageType.value = 'success';
    } else {
      nicknameMessage.value = result.message || '이미 사용 중인 닉네임입니다.';
      isNicknameChecked.value = false;
      nicknameValid.value = false;
      nicknameMessageType.value = 'error';
    }
  } catch (error) {
    nicknameMessage.value = '닉네임 확인 중 오류가 발생했습니다.';
    isNicknameChecked.value = false;
    nicknameValid.value = false;
    nicknameMessageType.value = 'error';
    console.error('닉네임 중복 확인 실패:', error);
  } finally {
    isCheckingNickname.value = false;
  }
};

// 닉네임 변경 시 중복 확인 상태 초기화
watch(nickname, (newVal) => {
  if (newVal !== originalNickname.value) {
    isNicknameChecked.value = false;
    nicknameValid.value = false;
    nicknameMessage.value = '';
    nicknameMessageType.value = '';
  } else {
    // 원래 닉네임으로 돌아간 경우
    isNicknameChecked.value = true;
    nicknameValid.value = true;
    nicknameMessage.value = '';
    nicknameMessageType.value = '';
  }
});

// 비밀번호 변경 여부 추적
watch([password, passwordConfirm], () => {
  isPasswordChanged.value = password.value.length > 0 || passwordConfirm.value.length > 0;
});

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
  if (password.value) {
    passwordMessage.value = isPasswordValid.value
        ? '사용 가능한 비밀번호입니다.'
        : '영문, 숫자, 특수문자를 포함해 9자 이상이어야 합니다.';
  } else {
    passwordMessage.value = '';
  }
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

// 뒤로가기
const goBack = () => router.back();

// 마운트 시 기존 사용자 정보 불러오기
onMounted(async () => {
  try {
    const res = await getMyInfo()
    if (res.success) {
      nickname.value = res.data.nickname;
      originalNickname.value = res.data.nickname;
      name.value = res.data.name;
      maskedPhoneNumber.value = res.data.maskedPhoneNumber;
      phoneNumber.value = res.data.phoneNumber;
      email.value = res.data.email;
      password.value = '';
      passwordConfirm.value = '';
    }
  } catch (err) {
    alert('회원 정보 조회 실패')
  }
});

// 저장 버튼 클릭 시
const submitUpdate = async () => {
  if (
      !nickname.value ||
      !name.value ||
      !phoneNumber.value ||
      !email.value
  ) {
    alert('모든 항목을 입력해주세요.');
    return;
  }

// 닉네임을 수정했을 경우에만 중복 확인 요구
  if (nickname.value !== originalNickname.value) {
    if (!isNicknameChecked.value || !nicknameValid.value) {
      alert('닉네임 중복 확인을 해주세요.');
      return;
    }
  }

  // 비밀번호 변경 시에만 비밀번호 검증
  if (isPasswordChanged.value) {
    if (!password.value || !passwordConfirm.value) {
      alert('비밀번호와 비밀번호 확인을 모두 입력해주세요.');
      return;
    }
    if (!isPasswordMatch.value || !isPasswordValid.value) {
      alert('비밀번호를 다시 확인해주세요.');
      return;
    }
  }

  if (!isPhoneValid.value) {
    alert('전화번호 형식을 확인해주세요.');
    return;
  }
    try {
      const updateData = {
        nickname: nickname.value,
        name: name.value,
        phoneNumber: phoneNumber.value,
        email: email.value
      };

      const isChangingPassword = isPasswordChanged.value && password.value.trim();

      if (isChangingPassword) {
        updateData.password = password.value;
      }

      const res = await updateMyInfo(updateData);

      if (res.success) {
        if (isChangingPassword) {
          alert('비밀번호가 변경되었습니다.');
        } else {
          alert('회원 정보가 수정되었습니다.');
        }
        router.push('/mypage');
      }
    } catch (err) {
      console.error('회원정보 수정 오류:', err);
      alert(err.message || '회원 정보 수정 실패');
    }
  };

</script>

<template>
  <div class="update-page">
    <Header title="회원 정보" :back-action="goBack" />

    <div class="form-area">
      <!-- 닉네임 -->
      <label class="label">닉네임</label>
      <div class="nickname-wrapper">
        <input v-model="nickname" type="text" class="nickname-input" />
        <button class="nickname-check-button" @click="checkNickname">중복 확인</button>
      </div>
      <div v-if="nicknameMessage" class="nickname-check-message">
        <span :class="nicknameValid ? 'success' : 'error'">{{
            nicknameMessage
          }}</span>
      </div>

      <!-- 이름(수정 불가) -->
      <label class="label">이름</label>
      <input v-model="name" type="text" class="input-box readonly-input" readonly />

      <!-- 전화번호 -->
      <label class="label">전화번호</label>
      <!-- 전화번호 없을 경우에만 마스킹된 번호 placeholder에서 보여주기 -->
      <input v-model="phoneNumber" type="text" class="input-box" :placeholder="phoneNumber ? '' : maskedPhoneNumber"/>
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
      <div class="check">
        <span v-if="passwordMessage" :class="isPasswordValid ? 'success' : 'error'">
          {{ passwordMessage }}
        </span>
      </div>
      <p class="password-rules">
        • 비밀번호는 영문 소문자, 숫자를 포함해 최소 9자리 이상이어야 합니다.<br />
        • 3자 이상의 연속되는 글자, 숫자는 사용이 불가능합니다.
      </p>

      <!-- 비밀번호 확인 -->
      <label class="label">비밀번호 확인</label>
      <input v-model="passwordConfirm" type="password" class="input-box" />
      <div class="check">
        <span v-if="passwordConfirm && !isPasswordMatch" class="error">비밀번호가 동일하지 않습니다.</span>
        <span v-if="passwordConfirm && isPasswordMatch" class="success">비밀번호가 동일합니다.</span>
      </div>
    </div>

    <!-- 저장 버튼 -->
    <div class="bottom-fixed">
      <Button label="저장" @click="submitUpdate" />
    </div>
  </div>
</template>

<style scoped>
.update-page {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--theme-bg);
  padding-top: 76px; /* Header 높이 보정 */
  padding-bottom: 16px;
  box-sizing: border-box;
}

.form-area {
  flex: 1;
  overflow-y: auto;
  padding: 0 24px;
  box-sizing: border-box;
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

.readonly-input {
  background: #f5f5f5 !important;
  color: #666;
  cursor: not-allowed;
  border-color: #d1d5db;
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
  background: var(--theme-primary);
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
  font-size: 12px;
  margin-top: 8px;
}
</style>