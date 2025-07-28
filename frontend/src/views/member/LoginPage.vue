<script setup>

</script>

<template>
  <div class="join-container">
    <!-- 로고 이미지 -->
    <img src="@/assets/img/logo.png" alt="N빵트립 로고" class="logo" />

    <!-- 슬로건 문구 -->
    <div class="subtitle">돈 걱정 말고, 여행 가자옹!</div>

    <!-- 로그인 입력 폼 -->
    <div class="form-wrapper">
      <label for="email" class="label">이메일</label>
      <input id="email" type="email" class="input-box" />

      <label class="label password-label">비밀번호</label>
      <input type="password" class="input-box" />

      <button type="button" class="login-button">로그인</button>
    </div>

    <!-- 로그인과 회원가입 사이 구분선 -->
    <div class="signup-section">
      <div class="line left-line"></div>
      <div class="or-text">또는</div>
      <div class="line right-line"></div>

      <div class="no-account-text">계정이 없으신가요?</div>
      <div class="signup-text">회원가입</div>
    </div>

    <!-- 회원가입 입력 폼 -->
    <div class="form-area">
      <!-- 닉네임 + 중복확인 버튼 -->
      <label class="label">닉네임</label>
      <div class="nickname-wrapper">
        <input type="text" class="input-box short" />
        <button class="check-button">중복 확인</button>
      </div>

      <!-- 이름 -->
      <label class="label">이름</label>
      <input type="text" class="input-box" />

      <!-- 전화번호 -->
      <label class="label">전화번호</label>
      <input type="text" class="input-box" />

      <!-- 이메일 -->
      <label class="label">이메일</label>
      <input type="email" class="input-box" />

      <!-- 비밀번호 -->
      <label class="label">비밀번호</label>
      <input type="password" class="input-box" />

      <!-- 비밀번호 조건 안내 -->
      <p class="password-rules">
        비밀번호는 영문 소문자, 숫자를 포함해 최소 9자리 이상이어야 한다.<br />
        3자 이상의 연속되는 글자, 숫자는 사용이 불가능하다.
      </p>

      <!-- 비밀번호 확인 -->
      <label class="label">비밀번호 확인</label>
      <input type="password" class="input-box" />

      <!-- 비밀번호 일치 여부 문구 -->
      <div class="password-check">
        <span class="error">비밀번호가 동일하지 않습니다.</span><br />
        <span class="success">비밀번호가 동일합니다.</span>
      </div>

      <!-- 은행 선택 드롭다운 -->
      <label class="label">은행 선택</label>
      <div class="select-box">
        <select class="dropdown">
          <option disabled selected>은행을 선택하세요</option>
          <option>국민은행</option>
          <option>신한은행</option>
          <option>우리은행</option>
          <option>카카오뱅크</option>
          <option>토스뱅크</option>
          <option>기업은행</option>
          <option>농협은행</option>
          <option>하나은행</option>
        </select>
      </div>

      <!-- 계좌번호 입력 -->
      <label class="label">계좌번호</label>
      <input type="text" class="input-box" />
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

.password-check .error {
  color: #a76a6a;
}

.password-check .success {
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
}

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
}
</style>
