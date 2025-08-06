<template>
  <div class="screen">
    <div class="div">
      <header class="header">
        <div class="button-SVG">
          <img class="vector" alt="Vector" :src="vector" />
        </div>
        <div class="heading">결제 내역 추가</div>
        <img class="img" alt="Vector" :src="image" />
      </header>
      
      <div class="main">
        <div class="label">내용</div>
        <div class="input">
          <div class="container">
            <div class="text-wrapper">광장시장 찹쌀꽈배기</div>
            <img class="vector-2" alt="Vector" :src="vector2" />
          </div>
        </div>
        <div class="label-2">금액</div>
        <div class="overlap">
          <div class="container-wrapper">
            <div class="div-wrapper">
              <div class="text-wrapper-2">10,000</div>
            </div>
          </div>
          <div class="text-wrapper-3">원</div>
        </div>
        <div class="label-3">날짜</div>
        <div class="input-2">
          <div class="navbar">
            <div class="spinbutton-month">07</div>
            <div class="text-wrapper-4">/</div>
            <div class="spinbutton-day">11</div>
            <div class="text-wrapper-5">/</div>
            <div class="spinbutton-year">2025</div>
            <div class="text-wrapper-6">,</div>
            <div class="spinbutton-hours">10</div>
            <div class="text-wrapper-7">:</div>
            <div class="spinbutton-minutes">00</div>
            <div class="spinbutton-AM-PM-AM">AM</div>
          </div>
          <div class="image">
            <div class="image-fill">
              <div class="overlap-group-wrapper">
                <div class="overlap-group">
                  <img class="vector-3" alt="Vector" :src="vector3" />
                  <img class="vector-4" alt="Vector" :src="vector4" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="label-4">카테고리</div>
        <div class="label-5">결제자</div>
        <div class="select">
          <div class="text-wrapper-8">이건우</div>
          <Size16 class="chevron-down" />
        </div>

        
        <div class="label-6">결제 참여자</div>
        <div class="background-shadow">
          <div class="background">
            <div class="text-wrapper-9">김</div>
          </div>
          <div class="text-wrapper-10">김민수</div>
          <div class="input-wrapper">
            <div class="input-3" />
          </div>
        </div>
        <div class="background-shadow-2">
          <div class="background">
            <div class="text-wrapper-9">이</div>
          </div>
          <div class="text-wrapper-10">이건우</div>
          <div class="overlap-2">
            <div class="input-4" />
          </div>
        </div>
        <div class="background-shadow-3">
          <div class="background-2">
            <div class="text-wrapper-9">최</div>
          </div>
          <div class="text-wrapper-10">최정훈</div>
          <div class="overlap-3">
            <div class="input-4" />
          </div>
        </div>
        <div class="background-shadow-4">
          <div class="background-2">
            <div class="text-wrapper-9">권</div>
          </div>
          <div class="text-wrapper-10">권준호</div>
          <div class="overlap-3">
            <div class="input-4" />
          </div>
        </div>
        <div class="label-7">메모</div>
        <div class="textarea">
          <div class="text-wrapper-11">상세 내용을 입력하세요...</div>
        </div>
        <div class="select-2">
          <div class="text-wrapper-8">식음료</div>
          <div class="chevron-down" />
        </div>
      </div>
      <footer class="footer">
        <button class="button">
          <div class="text-wrapper-12">저장</div>
        </button>
        <div class="vector-wrapper">
          <img class="vector-5" alt="Vector" :src="vector5" />
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Screen',
  components: {
    
  },
  data() {
    return {

    };
  }
};
</script>

<style>
.screen {
  align-items: start;
  background-color: #f8f9fa;
  display: grid;
  height: 100%;
  justify-items: center;
  min-height: 100vh;
  width: 100vw;
}

.screen .div {
  background-color: #f8f9fa;
  height: 800px;
  overflow: hidden;
  position: relative;
  width: 384px;
}

.screen .header {
  background-color: transparent;
  height: 68px;
  left: 0;
  position: absolute;
  top: 0;
  width: 384px;
}

.screen .button-SVG {
  height: 24px;
  left: 20px;
  position: absolute;
  top: 22px;
  width: 24px;
}

.screen .vector {
  height: 16px;
  left: 7px;
  position: absolute;
  top: 4px;
  width: 10px;
}

.screen .heading {
  color: #4a4a4a;
  font-family: "Inter-ExtraBold", Helvetica;
  font-size: 20px;
  font-weight: 800;
  height: 28px;
  left: 130px;
  letter-spacing: 0;
  line-height: 28px;
  position: absolute;
  top: 19px;
  white-space: nowrap;
  width: 124px;
}

.screen .img {
  height: 20px;
  left: 336px;
  position: absolute;
  top: 22px;
  width: 18px;
}

.screen .main {
  height: 644px;
  left: -2px;
  overflow: scroll;
  position: absolute;
  top: 68px;
  width: 384px;
}

.screen .main::-webkit-scrollbar {
  display: none;
  width: 0;
}

.screen .label {
  color: #4a4a4a;
  font-family: "Inter-Bold", Helvetica;
  font-size: 14px;
  font-weight: 700;
  height: 17px;
  left: 20px;
  letter-spacing: 0;
  line-height: 20px;
  position: absolute;
  top: 8px;
  white-space: nowrap;
  width: 28px;
}

.screen .input {
  background-color: #ffffff;
  border: 2px solid;
  border-color: #e2e8f0;
  border-radius: 12px;
  height: 52px;
  left: 20px;
  overflow: hidden;
  position: absolute;
  top: 32px;
  width: 344px;
}

.screen .container {
  height: 24px;
  left: 18px;
  overflow: scroll;
  position: relative;
  top: 14px;
  width: 308px;
}

.screen .container::-webkit-scrollbar {
  display: none;
  width: 0;
}

.screen .text-wrapper {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 0;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 1px;
  white-space: nowrap;
  width: 159px;
}

.screen .vector-2 {
  height: 18px;
  left: 290px;
  position: absolute;
  top: 4px;
  width: 18px;
}

.screen .label-2 {
  color: #4a4a4a;
  font-family: "Inter-Bold", Helvetica;
  font-size: 14px;
  font-weight: 700;
  height: 17px;
  left: 20px;
  letter-spacing: 0;
  line-height: 20px;
  position: absolute;
  top: 104px;
  white-space: nowrap;
  width: 28px;
}

.screen .overlap {
  border-radius: 12px;
  height: 52px;
  left: 20px;
  position: absolute;
  top: 128px;
  width: 344px;
}

.screen .container-wrapper {
  background-color: #ffffff;
  border: 2px solid;
  border-color: #e2e8f0;
  border-radius: 12px;
  height: 52px;
  left: 0;
  overflow: hidden;
  position: absolute;
  top: 0;
  width: 344px;
}

.screen .div-wrapper {
  height: 24px;
  left: 18px;
  overflow: scroll;
  position: relative;
  top: 14px;
  width: 284px;
}

.screen .div-wrapper::-webkit-scrollbar {
  display: none;
  width: 0;
}

.screen .text-wrapper-2 {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 0;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 1px;
  white-space: nowrap;
  width: 64px;
}

.screen .text-wrapper-3 {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 312px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 15px;
  white-space: nowrap;
  width: 16px;
}

.screen .label-3 {
  color: #4a4a4a;
  font-family: "Inter-Bold", Helvetica;
  font-size: 14px;
  font-weight: 700;
  height: 17px;
  left: 20px;
  letter-spacing: 0;
  line-height: 20px;
  position: absolute;
  top: 200px;
  white-space: nowrap;
  width: 28px;
}

.screen .input-2 {
  background-color: #ffffff;
  border: 2px solid;
  border-color: #e2e8f0;
  border-radius: 12px;
  height: 54px;
  left: 20px;
  overflow: hidden;
  position: absolute;
  top: 224px;
  width: 344px;
}

.screen .navbar {
  height: 26px;
  left: 18px;
  position: absolute;
  top: 14px;
  width: 288px;
}

.screen .spinbutton-month {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 1px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 19px;
}

.screen .text-wrapper-4 {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 21px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 6px;
}

.screen .spinbutton-day {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 28px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 18px;
}

.screen .text-wrapper-5 {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 45px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 6px;
}

.screen .spinbutton-year {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 52px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 39px;
}

.screen .text-wrapper-6 {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 92px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 9px;
}

.screen .spinbutton-hours {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 102px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 20px;
}

.screen .text-wrapper-7 {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 123px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 5px;
}

.screen .spinbutton-minutes {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 129px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 21px;
}

.screen .spinbutton-AM-PM-AM {
  color: #000000;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 20px;
  left: 153px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 2px;
  white-space: nowrap;
  width: 26px;
}

.screen .image {
  height: 20px;
  left: 306px;
  position: absolute;
  top: 17px;
  width: 20px;
}

.screen .image-fill {
  height: 20px;
}

.screen .overlap-group-wrapper {
  height: 15px;
  left: 2px;
  position: relative;
  top: 2px;
  width: 16px;
}

.screen .overlap-group {
  height: 15px;
  position: relative;
  width: 15px;
}

.screen .vector-3 {
  height: 14px;
  left: 1px;
  position: absolute;
  top: 1px;
  width: 12px;
}

.screen .vector-4 {
  height: 15px;
  left: 0;
  position: absolute;
  top: 0;
  width: 15px;
}

.screen .label-4 {
  color: #4a4a4a;
  font-family: "Inter-Bold", Helvetica;
  font-size: 14px;
  font-weight: 700;
  height: 17px;
  left: 20px;
  letter-spacing: 0;
  line-height: 20px;
  position: absolute;
  top: 298px;
  white-space: nowrap;
  width: 56px;
}

.screen .label-5 {
  color: #4a4a4a;
  font-family: "Inter-Bold", Helvetica;
  font-size: 14px;
  font-weight: 700;
  height: 17px;
  left: 20px;
  letter-spacing: 0;
  line-height: 20px;
  position: absolute;
  top: 394px;
  white-space: nowrap;
  width: 42px;
}

.screen .select {
  align-items: center;
  background-color: #ffffff;
  border: 1px solid;
  border-color: #e2e8f0;
  border-radius: var(--size-radius-200);
  display: flex;
  gap: 8px;
  height: 42px;
  left: 19px;
  padding: var(--size-space-300) var(--size-space-300) var(--size-space-300)
    var(--size-space-400);
  position: absolute;
  top: 426px;
  width: 346px;
}

.screen .text-wrapper-8 {
  color: #000000;
  flex: 1;
  font-family: "Inter-Medium", Helvetica;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0;
  line-height: 16px;
  margin-top: -0.50px;
  position: relative;
  text-align: center;
}

.screen .chevron-down {
  height: 16px !important;
  position: relative !important;
  width: 16px !important;
}

.screen .label-6 {
  color: #4a4a4a;
  font-family: "Inter-Bold", Helvetica;
  font-size: 14px;
  font-weight: 700;
  height: 17px;
  left: 20px;
  letter-spacing: 0;
  line-height: 20px;
  position: absolute;
  top: 490px;
  white-space: nowrap;
  width: 74px;
}

.screen .background-shadow {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0px 1px 2px #0000000d;
  height: 64px;
  left: 20px;
  position: absolute;
  top: 518px;
  width: 344px;
}

.screen .background {
  background-color: #ffd166a6;
  border-radius: 9999px;
  height: 40px;
  left: 12px;
  position: absolute;
  top: 12px;
  width: 40px;
}

.screen .text-wrapper-9 {
  color: #ffffff;
  font-family: "Inter-Bold", Helvetica;
  font-size: 18px;
  font-weight: 700;
  height: 28px;
  left: 11px;
  letter-spacing: 0;
  line-height: 28px;
  position: absolute;
  top: 5px;
  white-space: nowrap;
  width: 18px;
}

.screen .text-wrapper-10 {
  color: #4a4a4a;
  font-family: "Inter-SemiBold", Helvetica;
  font-size: 16px;
  font-weight: 600;
  height: 24px;
  left: 64px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 19px;
  white-space: nowrap;
  width: 48px;
}

.screen .input-wrapper {
  background-color: #989898a6;
  border-radius: 9999px;
  height: 24px;
  left: 284px;
  position: absolute;
  top: 20px;
  width: 40px;
}

.screen .input-3 {
  background-color: #ffffff;
  border: 4px solid;
  border-color: #808080;
  border-radius: 9999px;
  height: 24px;
  width: 24px;
}

.screen .background-shadow-2 {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0px 1px 2px #0000000d;
  height: 64px;
  left: 20px;
  position: absolute;
  top: 590px;
  width: 344px;
}

.screen .overlap-2 {
  background-color: #ffd166a6;
  border-radius: 9999px;
  height: 24px;
  left: 284px;
  position: absolute;
  top: 20px;
  width: 40px;
}

.screen .input-4 {
  background-color: #ffffff;
  border: 4px solid;
  border-color: #ffd166;
  border-radius: 9999px;
  height: 24px;
  left: 16px;
  position: relative;
  width: 24px;
}

.screen .background-shadow-3 {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0px 1px 2px #0000000d;
  height: 64px;
  left: 20px;
  position: absolute;
  top: 662px;
  width: 344px;
}

.screen .background-2 {
  background-color: #a2d2ff;
  border-radius: 9999px;
  height: 40px;
  left: 12px;
  position: absolute;
  top: 12px;
  width: 40px;
}

.screen .overlap-3 {
  background-color: #ffd166;
  border-radius: 9999px;
  height: 24px;
  left: 284px;
  position: absolute;
  top: 20px;
  width: 40px;
}

.screen .background-shadow-4 {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0px 1px 2px #0000000d;
  height: 64px;
  left: 20px;
  position: absolute;
  top: 734px;
  width: 344px;
}

.screen .label-7 {
  color: #4a4a4a;
  font-family: "Inter-Bold", Helvetica;
  font-size: 14px;
  font-weight: 700;
  height: 17px;
  left: 20px;
  letter-spacing: 0;
  line-height: 20px;
  position: absolute;
  top: 818px;
  white-space: nowrap;
  width: 28px;
}

.screen .textarea {
  background-color: #ffffff;
  border: 2px solid;
  border-color: #e2e8f0;
  border-radius: 12px;
  height: 100px;
  left: 20px;
  overflow: scroll;
  position: absolute;
  top: 842px;
  width: 344px;
}

.screen .textarea::-webkit-scrollbar {
  display: none;
  width: 0;
}

.screen .text-wrapper-11 {
  color: #9ca3af;
  font-family: "Inter-Regular", Helvetica;
  font-size: 16px;
  font-weight: 400;
  height: 24px;
  left: 18px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  top: 12px;
  white-space: nowrap;
  width: 183px;
}

.screen .select-2 {
  align-items: center;
  background-color: #ffffff;
  border: 1px solid;
  border-color: #e2e8f0;
  border-radius: var(--size-radius-200);
  display: flex;
  gap: 8px;
  height: 42px;
  left: 19px;
  padding: var(--size-space-300) var(--size-space-300) var(--size-space-300)
    var(--size-space-400);
  position: absolute;
  top: 335px;
  width: 346px;
}

.screen .footer {
  background-color: #f8f9fa;
  height: 88px;
  left: 0;
  position: absolute;
  top: 712px;
  width: 384px;
}

.screen .button {
  all: unset;
  background-color: #ffd166a6;
  border-radius: 12px;
  box-sizing: border-box;
  height: 56px;
  left: 89px;
  position: absolute;
  top: 16px;
  width: 279px;
}

.screen .text-wrapper-12 {
  color: #4a4a4a;
  font-family: "Inter-ExtraBold", Helvetica;
  font-size: 16px;
  font-weight: 800;
  height: 20px;
  left: 106px;
  letter-spacing: 0;
  line-height: 24px;
  position: absolute;
  text-align: center;
  top: 17px;
  white-space: nowrap;
  width: 68px;
}

.screen .vector-wrapper {
  background-color: #ff1c0c33;
  border-radius: 12px;
  height: 56px;
  left: 17px;
  position: absolute;
  top: 16px;
  width: 61px;
}

.screen .vector-5 {
  height: 20px;
  left: 21px;
  position: absolute;
  top: 18px;
  width: 18px;
}
</style>