<script setup>

</script>

<template>
  <div class="join-container">
    <!-- 상단 텍스트 -->
    <div class="page-title">회원 정보 입력</div>
    <!-- 상단 이미지 -->
    <img src="@/assets/img/sitting_cat.png" alt="로고 이미지" class="top-image" />

    <!-- 폼 영역 -->
    <div class="form-area">
      <!-- 닉네임 + 중복확인 -->
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
        비밀번호는 영문 소문자, 숫자를 포함해 최소 9자리 이상이어야 한다. <br />
        3자 이상의 연속되는 글자, 숫자는 사용이 불가능하다.
      </p>

      <!-- 비밀번호 확인 -->
      <label class="label">비밀번호 확인</label>
      <input type="password" class="input-box" />

      <!-- 비밀번호 일치 여부 안내 -->
      <div class="password-check">
        <span class="error">비밀번호가 동일하지 않습니다.</span><br />
        <span class="success">비밀번호가 동일합니다.</span>
      </div>

      <!-- 은행 선택 -->
      <label class="label">은행 선택</label>
      <div class="select-box">
        <div class="dropdown-icon"></div>
      </div>

      <!-- 계좌번호 -->
      <label class="label">계좌번호</label>
      <input type="text" class="input-box" />
    </div>

    <!-- 회원가입 버튼 -->
    <button class="join-button">회원가입</button>
  </div>
</template>

<style scoped>
.join-container {
  width: 384px;
  height: 800px;
  position: relative;
  background: #f8fafc;
  box-shadow: 0px 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  border-radius: 24px;
  outline: 1px solid black;
  outline-offset: -1px;
  margin: 0 auto;
}

/* 상단 텍스트 */
.page-title {
  position: absolute;
  top: 65px;
  left: 32px;
  font-size: 28px;
  font-weight: 400;
  line-height: 24px;
}

/* 상단 이미지 */
.top-image {
  position: absolute;
  top: 15px;
  left: 263px;
  width: 95px;
  height: 143px;
}

/* 폼 영역 */
.form-area {
  position: absolute;
  top: 158px;
  left: 32px;
  width: 321px;
  height: 814px;
}

.label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-top: 12px;
  display: block;
}

.input-box {
  width: 320px;
  height: 52px;
  border-radius: 12px;
  border: 2px solid #e2e8f0;
  background: white;
  padding: 0 12px;
  margin-top: 4px;
  box-sizing: border-box;
}

.input-box.short {
  width: 229px;
  display: inline-block;
  margin-right: 8px;
}

.nickname-wrapper {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
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
  line-height: 1.5;
  margin-top: 8px;
}

.password-check {
  margin-top: 8px;
  font-size: 10px;
  line-height: 1.5;
}

.password-check .error {
  color: #a76a6a;
}

.password-check .success {
  color: #61a569;
}

.select-box {
  width: 320px;
  height: 52px;
  border-radius: 12px;
  outline: 2px solid #e2e8f0;
  outline-offset: -2px;
  background: white;
  margin-top: 4px;
  position: relative;
}

.dropdown-icon {
  width: 11px;
  height: 7px;
  background: #8d8d8d;
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
}

/* 회원가입 버튼 */
.join-button {
  position: absolute;
  bottom: 45px;
  left: 32px;
  width: 320px;
  height: 56px;
  background: #fddf99;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 800;
  color: #2e363a;
  border: none;
  cursor: pointer;
}
</style>