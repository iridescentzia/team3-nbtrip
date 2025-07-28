<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import {
  registerMember,
  loginMember,
  logoutMember
} from '@/api/member'

// TODO: Account 기능 완성 후 주석 해제
// import {
//   getUserAccounts,
//   registerAccount,
//   getAvailableBanks
// } from '@/api/account'

const router = useRouter()
const authStore = useAuthStore()

// 현재 모드 관리 (로그인/회원가입)
const isSignupMode = ref(false)
const isLoading = ref(false)

// 로그인 폼 데이터
const loginForm = reactive({
  email: '',
  password: ''
})

// 회원가입 폼 데이터
const signupForm = reactive({
  email: '',
  password: '',
  passwordConfirm: '',
  nickname: '',
  name: '',
  phoneNumber: '',
  fcmToken: ''
})

// 계좌 정보 폼 (Account 기능 완성 후 활성화)
const accountForm = reactive({
  bankName: '',
  accountNumber: ''
})

// 상태 관리
const isNicknameChecked = ref(false)
const nicknameAvailable = ref(false)
const nicknameCheckMessage = ref('')

// TODO: Account 기능 완성 후 주석 해제
// const userAccounts = ref([])
// const availableBanks = ref([])

// 기본 은행 목록 (임시용 - Account API 완성 전까지 사용)
const defaultBanks = [
  '국민은행', '신한은행', '우리은행', '하나은행',
  '카카오뱅크', '토스뱅크', '기업은행', '농협은행',
  'SC제일은행', '케이뱅크', '수협은행', '대구은행',
  '부산은행', '경남은행', '광주은행', '전북은행',
  '제주은행', '신협'
]

// 사용할 은행 목록 결정
const availableBanks = computed(() => {
  // TODO: Account 기능 완성 후 주석 해제하고 위 return 주석 처리
  // if (authStore.isAuthenticated && userAccounts.value.length > 0) {
  //   // 로그인된 사용자의 등록된 은행들
  //   return [...new Set(userAccounts.value.map(account => account.bankName))]
  // }
  // 신규 가입자는 기본 은행 목록 사용
  return defaultBanks
})

// 비밀번호 유효성 검사 (백엔드 규칙에 맞춤)
const passwordValidation = computed(() => {
  const password = signupForm.password
  const minLength = password.length >= 8 && password.length <= 20
  const hasLetter = /[a-zA-Z]/.test(password)
  const hasNumber = /[0-9]/.test(password)
  const hasSpecial = /[!@#$%^&*]/.test(password)

  return {
    isValid: minLength && hasLetter && hasNumber && hasSpecial,
    minLength,
    hasLetter,
    hasNumber,
    hasSpecial
  }
})

// 비밀번호 확인 검사
const passwordMatch = computed(() => {
  if (!signupForm.passwordConfirm) return null
  return signupForm.password === signupForm.passwordConfirm
})

// 전화번호 유효성 검사
const phoneValidation = computed(() => {
  const phonePattern = /^01[0-9]-[0-9]{4}-[0-9]{4}$/
  return phonePattern.test(signupForm.phoneNumber)
})

// 계좌번호 유효성 검사
const accountValidation = computed(() => {
  if (!accountForm.accountNumber) return true // 선택사항이므로 빈 값은 유효
  // 계좌번호는 10-14자리 숫자만 허용
  const accountPattern = /^[0-9]{10,14}$/
  return accountPattern.test(accountForm.accountNumber.replace(/-/g, ''))
})

// 닉네임 중복 확인
const checkNickname = async () => {
  if (!signupForm.nickname.trim()) {
    alert('닉네임을 입력해주세요.')
    return
  }

  if (signupForm.nickname.length < 2 || signupForm.nickname.length > 100) {
    alert('닉네임은 2-100자 사이여야 합니다.')
    return
  }

  try {
    isLoading.value = true

    // TODO: 실제 API 호출 - 백엔드 구현 필요
    // const isDuplicate = await checkNicknameDuplicate(signupForm.nickname)

    // 임시로 랜덤 결과 생성 (실제 구현시 제거)
    const isDuplicate = Math.random() > 0.7

    isNicknameChecked.value = true
    nicknameAvailable.value = !isDuplicate
    nicknameCheckMessage.value = isDuplicate
        ? '이미 사용중인 닉네임입니다.'
        : '사용 가능한 닉네임입니다.'
  } catch (error) {
    alert('닉네임 확인 중 오류가 발생했습니다.')
    console.error('닉네임 확인 오류:', error)
  } finally {
    isLoading.value = false
  }
}

// 닉네임 변경시 중복확인 초기화
watch(() => signupForm.nickname, () => {
  isNicknameChecked.value = false
  nicknameAvailable.value = false
  nicknameCheckMessage.value = ''
})

// TODO: Account 기능 완성 후 주석 해제
// 사용자 계좌 목록 조회
// const fetchUserAccounts = async () => {
//   try {
//     if (authStore.user?.userId) {
//       const accounts = await getUserAccounts(authStore.user.userId)
//       userAccounts.value = accounts
//     }
//   } catch (error) {
//     console.error('계좌 목록 조회 오류:', error)
//   }
// }

// FCM 토큰 가져오기 (실제 Firebase 연동시 사용)
const getFcmToken = async () => {
  try {
    // TODO: Firebase Messaging을 통한 FCM 토큰 획득
    // const messaging = getMessaging()
    // const token = await getToken(messaging, { vapidKey: 'your-vapid-key' })
    // return token
    return '' // 임시로 빈 문자열 반환
  } catch (error) {
    console.error('FCM 토큰 획득 실패:', error)
    return ''
  }
}

// 로그인 처리
const handleLogin = async () => {
  if (!loginForm.email || !loginForm.password) {
    alert('이메일과 비밀번호를 모두 입력해주세요.')
    return
  }

  try {
    isLoading.value = true

    // FCM 토큰 획득
    const fcmToken = await getFcmToken()

    const loginData = {
      ...loginForm,
      fcmToken
    }

    const result = await loginMember(loginData)

    if (result.success || result.accessToken) {
      const token = result.accessToken || result.token
      const userInfo = result.member || result.user

      localStorage.setItem('accessToken', token)
      authStore.setUser(userInfo)
      authStore.setToken(token)

      // TODO: Account 기능 완성 후 주석 해제
      // await fetchUserAccounts()

      alert('로그인되었습니다.')
      router.push('/dashboard')
    }
  } catch (error) {
    alert(error.message || '로그인에 실패했습니다.')
    console.error('로그인 오류:', error)
  } finally {
    isLoading.value = false
  }
}

// 회원가입 처리
const handleSignup = async () => {
  // 기본 유효성 검사
  if (!signupForm.email || !signupForm.password || !signupForm.passwordConfirm ||
      !signupForm.nickname || !signupForm.name || !signupForm.phoneNumber) {
    alert('모든 필수 필드를 입력해주세요.')
    return
  }

  if (!isNicknameChecked.value || !nicknameAvailable.value) {
    alert('닉네임 중복확인을 해주세요.')
    return
  }

  if (!passwordValidation.value.isValid) {
    alert('비밀번호는 8-20자, 영문, 숫자, 특수문자를 포함해야 합니다.')
    return
  }

  if (!passwordMatch.value) {
    alert('비밀번호가 일치하지 않습니다.')
    return
  }

  if (!phoneValidation.value) {
    alert('올바른 휴대폰 번호 형식이 아닙니다. (010-1234-5678)')
    return
  }

  // 계좌 정보 유효성 검사 (선택사항)
  if (accountForm.accountNumber && !accountValidation.value) {
    alert('올바른 계좌번호 형식이 아닙니다. (10-14자리 숫자)')
    return
  }

  if (accountForm.accountNumber && !accountForm.bankName) {
    alert('계좌번호를 입력하신 경우 은행을 선택해주세요.')
    return
  }

  try {
    isLoading.value = true

    // FCM 토큰 획득
    signupForm.fcmToken = await getFcmToken()

    // 1단계: 회원 정보 등록
    const memberResult = await registerMember(signupForm)

    if (memberResult.success) {
      // 2단계: 계좌 정보 등록 (선택사항)
      if (accountForm.bankName && accountForm.accountNumber) {
        try {
          // TODO: Account 기능 완성 후 주석 해제
          // const accountResult = await registerAccount({
          //   bankName: accountForm.bankName,
          //   accountNumber: accountForm.accountNumber.replace(/-/g, '')
          // })

          // 임시 처리
          const accountResult = { success: true }

          if (accountResult.success) {
            alert('회원가입 및 계좌 등록이 완료되었습니다!\n로그인해주세요.')
          } else {
            alert('회원가입은 완료되었습니다.\n계좌는 나중에 마이페이지에서 등록하실 수 있습니다.')
          }
        } catch (accountError) {
          console.error('계좌 등록 오류:', accountError)
          alert('회원가입은 완료되었습니다.\n계좌는 나중에 마이페이지에서 등록하실 수 있습니다.')
        }
      } else {
        alert('회원가입이 완료되었습니다!\n로그인해주세요.')
      }

      toggleMode()
      resetForms()
    }
  } catch (error) {
    alert(error.message || '회원가입에 실패했습니다.')
    console.error('회원가입 오류:', error)
  } finally {
    isLoading.value = false
  }
}

// 모드 전환 (로그인 ↔ 회원가입)
const toggleMode = () => {
  isSignupMode.value = !isSignupMode.value
  resetForms()
}

// 폼 초기화
const resetSignupForm = () => {
  Object.keys(signupForm).forEach(key => {
    signupForm[key] = ''
  })
  Object.keys(accountForm).forEach(key => {
    accountForm[key] = ''
  })
  isNicknameChecked.value = false
  nicknameAvailable.value = false
  nicknameCheckMessage.value = ''
}

const resetForms = () => {
  Object.keys(loginForm).forEach(key => {
    loginForm[key] = ''
  })
  resetSignupForm()
}

// 폼 유효성 검사 상태
const isLoginFormValid = computed(() => {
  return loginForm.email && loginForm.password
})

const isSignupFormValid = computed(() => {
  const basicValid = signupForm.email &&
      signupForm.password &&
      signupForm.passwordConfirm &&
      signupForm.nickname &&
      signupForm.name &&
      signupForm.phoneNumber &&
      isNicknameChecked.value &&
      nicknameAvailable.value &&
      passwordValidation.value.isValid &&
      passwordMatch.value &&
      phoneValidation.value

  // 계좌 정보가 입력된 경우 추가 검증
  const accountValid = accountForm.accountNumber ?
      (accountForm.bankName && accountValidation.value) : true

  return basicValid && accountValid
})

// 페이지 이탈시 자동 로그아웃 (토큰 관리)
const setupTokenManagement = () => {
  // 페이지 언로드시 처리
  window.addEventListener('beforeunload', () => {
    // 로그인 유지 체크박스가 없다면 항상 토큰 제거
    localStorage.removeItem('accessToken')
    authStore.clearAuth()
  })

  // 탭 변경시 처리
  document.addEventListener('visibilitychange', () => {
    if (document.hidden) {
      // 탭이 숨겨졌을 때 일정 시간 후 로그아웃 처리
      setTimeout(() => {
        if (document.hidden) {
          localStorage.removeItem('accessToken')
          authStore.clearAuth()
        }
      }, 30000) // 30초 후 자동 로그아웃
    }
  })
}

// 계좌번호 입력시 자동 하이픈 추가
const formatAccountNumber = () => {
  let value = accountForm.accountNumber.replace(/[^0-9]/g, '')
  if (value.length > 14) {
    value = value.substring(0, 14)
  }
  // 은행별 계좌번호 형식에 따라 하이픈 추가 (기본적으로 4-4-6 형식)
  if (value.length > 8) {
    value = value.substring(0, 4) + '-' + value.substring(4, 8) + '-' + value.substring(8)
  } else if (value.length > 4) {
    value = value.substring(0, 4) + '-' + value.substring(4)
  }
  accountForm.accountNumber = value
}

// 컴포넌트 마운트시 설정
onMounted(() => {
  setupTokenManagement()

  // TODO: Account 기능 완성 후 주석 해제
  // if (authStore.isAuthenticated) {
  //   fetchUserAccounts()
  // }
})
</script>

<template>
  <div class="join-container">
    <!-- 로고 이미지 -->
    <img src="@/assets/img/logo.png" alt="N빵트립 로고" class="logo" />

    <!-- 슬로건 문구 -->
    <div class="subtitle">돈 걱정 말고, 여행 가자옹!</div>

    <!-- 로그인 모드일 때만 표시 -->
    <div v-if="!isSignupMode" class="form-wrapper">
      <label for="login-email" class="label">이메일</label>
      <input
          id="login-email"
          v-model="loginForm.email"
          type="email"
          class="input-box"
          placeholder="이메일을 입력하세요"
          :disabled="isLoading"
      />

      <label class="label password-label">비밀번호</label>
      <input
          v-model="loginForm.password"
          type="password"
          class="input-box"
          placeholder="비밀번호를 입력하세요"
          :disabled="isLoading"
          @keyup.enter="handleLogin"
      />

      <button
          type="button"
          class="login-button"
          :disabled="!isLoginFormValid || isLoading"
          @click="handleLogin"
      >
        {{ isLoading ? '로그인 중...' : '로그인' }}
      </button>
    </div>

    <!-- 로그인과 회원가입 사이 구분선 -->
    <div class="signup-section">
      <div class="line left-line"></div>
      <div class="or-text">또는</div>
      <div class="line right-line"></div>

      <div class="no-account-text">
        {{ isSignupMode ? '이미 계정이 있으신가요?' : '계정이 없으신가요?' }}
      </div>
      <div class="signup-text" @click="toggleMode">
        {{ isSignupMode ? '로그인' : '회원가입' }}
      </div>
    </div>

    <!-- 회원가입 모드일 때만 표시 -->
    <div v-if="isSignupMode" class="form-area">
      <!-- 닉네임 + 중복확인 버튼 -->
      <label class="label">닉네임 *</label>
      <div class="nickname-wrapper">
        <input
            v-model="signupForm.nickname"
            type="text"
            class="input-box short"
            placeholder="닉네임 입력"
            :disabled="isLoading"
            maxlength="100"
        />
        <button
            class="check-button"
            :disabled="!signupForm.nickname || isLoading"
            @click="checkNickname"
        >
          {{ isLoading ? '확인중...' : '중복 확인' }}
        </button>
      </div>

      <!-- 닉네임 확인 메시지 -->
      <div v-if="nicknameCheckMessage" class="nickname-check">
        <span :class="{ success: nicknameAvailable, error: !nicknameAvailable }">
          {{ nicknameCheckMessage }}
        </span>
      </div>

      <!-- 이름 -->
      <label class="label">이름 *</label>
      <input
          v-model="signupForm.name"
          type="text"
          class="input-box"
          placeholder="실명을 입력하세요"
          :disabled="isLoading"
          maxlength="50"
      />

      <!-- 전화번호 -->
      <label class="label">전화번호 *</label>
      <input
          v-model="signupForm.phoneNumber"
          type="text"
          class="input-box"
          placeholder="010-1234-5678"
          :disabled="isLoading"
          maxlength="13"
      />

      <!-- 이메일 -->
      <label class="label">이메일 *</label>
      <input
          v-model="signupForm.email"
          type="email"
          class="input-box"
          placeholder="이메일을 입력하세요"
          :disabled="isLoading"
          maxlength="100"
      />

      <!-- 비밀번호 -->
      <label class="label">비밀번호 *</label>
      <input
          v-model="signupForm.password"
          type="password"
          class="input-box"
          placeholder="비밀번호를 입력하세요"
          :disabled="isLoading"
          maxlength="20"
      />

      <!-- 비밀번호 조건 안내 -->
      <p class="password-rules">
        비밀번호는 8-20자, 영문, 숫자, 특수문자(!@#$%^&*)를 포함해야 합니다.
      </p>

      <!-- 비밀번호 확인 -->
      <label class="label">비밀번호 확인 *</label>
      <input
          v-model="signupForm.passwordConfirm"
          type="password"
          class="input-box"
          placeholder="비밀번호를 다시 입력하세요"
          :disabled="isLoading"
          maxlength="20"
      />

      <!-- 비밀번호 일치 여부 문구 -->
      <div v-if="signupForm.passwordConfirm" class="password-check">
        <span v-if="passwordMatch === false" class="error">
          비밀번호가 동일하지 않습니다.
        </span>
        <span v-if="passwordMatch === true" class="success">
          비밀번호가 동일합니다.
        </span>
      </div>

      <!-- 계좌 정보 섹션 (선택사항) -->
      <div class="account-section">
        <div class="account-header">
          <h4>계좌 정보 (선택사항)</h4>
          <span class="account-subtitle">나중에 마이페이지에서도 등록 가능합니다</span>
        </div>

        <!-- 은행 선택 드롭다운 -->
        <label class="label">은행 선택</label>
        <div class="select-box">
          <select
              v-model="accountForm.bankName"
              class="dropdown"
              :disabled="isLoading"
          >
            <option disabled value="">은행을 선택하세요</option>
            <option
                v-for="bank in availableBanks"
                :key="bank"
                :value="bank"
            >
              {{ bank }}
            </option>
          </select>
        </div>

        <!-- 계좌번호 입력 -->
        <label class="label">계좌번호</label>
        <input
            v-model="accountForm.accountNumber"
            type="text"
            class="input-box"
            placeholder="계좌번호를 입력하세요 (숫자만)"
            :disabled="isLoading"
            maxlength="17"
            @input="formatAccountNumber"
        />

        <!-- 계좌번호 안내 -->
        <div v-if="accountForm.accountNumber && !accountValidation" class="account-error">
          올바른 계좌번호 형식이 아닙니다. (10-14자리 숫자)
        </div>

        <!-- TODO: Account 기능 완성 후 주석 해제 -->
        <!-- 기존 계좌 목록 표시 (로그인 사용자용) -->
        <!-- <div v-if="authStore.isAuthenticated && userAccounts.length > 0" class="existing-accounts">
          <h5>등록된 계좌</h5>
          <div v-for="account in userAccounts" :key="account.account_id" class="account-item">
            <span class="bank-name">{{ account.bank_name }}</span>
            <span class="account-number">{{ account.account_number }}</span>
            <span class="balance">잔액: {{ account.balance.toLocaleString() }}원</span>
          </div>
        </div> -->
      </div>

      <!-- 회원가입 버튼 -->
      <button
          type="button"
          class="login-button signup-btn"
          :disabled="!isSignupFormValid || isLoading"
          @click="handleSignup"
      >
        {{ isLoading ? '가입 중...' : '회원가입' }}
      </button>

      <!-- 필수/선택 안내 -->
      <div class="form-notice">
        <span class="required">* 필수 입력 항목</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 전체 컨테이너 스타일 */
.join-container {
  width: 384px;
  height: 800px;
  position: relative;
  background: #f8fafc;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: auto;
  border-radius: 24px;
  outline: 1px black solid;
  outline-offset: -1px;
  margin: 0 auto;
  padding: 32px;
  box-sizing: border-box;
}

/* 로고 이미지 */
.logo {
  width: 250px;
  height: auto;
  display: block;
  margin: 0 auto 12px auto;
}

/* 서브 타이틀 */
.subtitle {
  text-align: center;
  font-size: 16px;
  color: #8d8d8d;
  margin-bottom: 24px;
}

/* 로그인 영역 */
.form-wrapper {
  width: 100%;
}

/* 회원가입 영역 */
.form-area {
  width: 100%;
  margin-top: 24px;
}

.label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-top: 24px;
  display: block;
}

.password-label {
  margin-top: 20px;
}

.input-box {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: white;
  padding: 0 12px;
  font-size: 16px;
  margin-top: 6px;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

.input-box:focus {
  outline: none;
  border-color: #fddf99;
}

.input-box:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.input-box.short {
  width: 229px;
  margin-right: 8px;
}

.nickname-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.check-button {
  width: 84px;
  height: 52px;
  background: #fddf99;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #2e363a;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.check-button:hover:not(:disabled) {
  background: #fcd34d;
}

.check-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
}

.nickname-check {
  font-size: 12px;
  margin-top: 4px;
}

.password-rules {
  font-size: 10px;
  color: #9a9595;
  margin-top: 8px;
  line-height: 1.5;
}

.password-check {
  font-size: 10px;
  margin-top: 8px;
  line-height: 1.5;
}

.password-check .error,
.nickname-check .error,
.account-error {
  color: #a76a6a;
}

.password-check .success,
.nickname-check .success {
  color: #61a569;
}

.select-box {
  margin-top: 6px;
}

.dropdown {
  width: 100%;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: white;
  padding: 0 12px;
  font-size: 14px;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.dropdown:focus {
  outline: none;
  border-color: #fddf99;
}

.dropdown:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

/* 계좌 정보 섹션 */
.account-section {
  margin-top: 32px;
  padding: 20px;
  background-color: #f9fafb;
  border-radius: 12px;
  border-left: 4px solid #fddf99;
}

.account-header {
  margin-bottom: 16px;
}

.account-header h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.account-subtitle {
  font-size: 12px;
  color: #6b7280;
}

.account-error {
  font-size: 12px;
  margin-top: 4px;
}

/* TODO: Account 기능 완성 후 주석 해제 */
/* .existing-accounts {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.existing-accounts h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.account-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: white;
  border-radius: 8px;
  margin-bottom: 8px;
  font-size: 12px;
}

.bank-name {
  font-weight: 600;
  color: #374151;
}

.account-number {
  color: #6b7280;
}

.balance {
  color: #059669;
  font-weight: 500;
} */

.login-button {
  width: 100%;
  height: 56px;
  background: #fddf99;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 800;
  color: #2e363a;
  margin-top: 16px;
  cursor: pointer;
  border: none;
  transition: background-color 0.3s ease;
}

.login-button:hover:not(:disabled) {
  background: #fcd34d;
}

.login-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
  color: #9ca3af;
}

.signup-btn {
  margin-top: 24px;
}

.form-notice {
  text-align: center;
  margin-top: 12px;
}

.required {
  font-size: 12px;
  color: #6b7280;
}

.signup-section {
  width: 320px;
  margin: 40px auto 0 auto;
  text-align: center;
  position: relative;
}

.line {
  width: 130px;
  height: 1px;
  border-top: 1px solid #cbd5e1;
  position: absolute;
  top: 10px;
}

.left-line {
  left: 0;
}

.right-line {
  right: 0;
}

.or-text {
  font-size: 14px;
  color: #8d8d8d;
  line-height: 20px;
  position: relative;
}

.no-account-text {
  margin-top: 40px;
  font-size: 14px;
  color: #8d8d8d;
  display: inline;
}

.signup-text {
  font-size: 14px;
  font-weight: 700;
  color: #333333;
  margin-left: 6px;
  display: inline;
  cursor: pointer;
  transition: color 0.3s ease;
}

.signup-text:hover {
  color: #fddf99;
}
</style>
