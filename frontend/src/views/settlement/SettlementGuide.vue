<script setup>
import {useRouter} from "vue-router";
import Header from "@/components/layout/Header2.vue";
</script>

<template>
  <div class="settlement-explanation">
    <!-- 헤더 -->
    <header title="정산 과정 안내" />
      <h1>N빵트립의 정산 과정을 간단히 설명해 드릴게요!</h1>
      <p>우리 서비스의 정산 프로세스를 간단히 설명합니다.</p>

    <!-- Step 1 -->
    <section class="story-step">
      <h2>STEP 1 — 결제 기록 수집</h2>
      <p class="lead">
        여행이나 모임에서 발생한 결제내역을 모두 모아요.<br />
        누가 결제했고, 누가 참여했는지 기록합니다.
      </p>
      <div class="chat">
        <div class="bubble a">A: 12,000원 결제 (점심)</div>
        <div class="bubble b">B: 20,000원 결제 (카페)</div>
        <div class="bubble c">C: 9,000원 결제 (간식)</div>
      </div>
    </section>

    <!-- Step 2 -->
    <section class="story-step">
      <h2>STEP 2 — 채무표 만들기</h2>
      <p class="lead">
        각 결제를 참여 인원 수로 나누어, 누가 누구에게 얼마를 주고받아야 하는지 계산해요.
      </p>
      <div class="chat">
        <div class="bubble a">A: B에게 <b>5,000원</b> 줄게</div>
        <div class="bubble b">B: C에게 <b>10,000원</b> 줄게</div>
        <div class="bubble c">C: A에게 <b>3,000원</b> 줄게</div>
      </div>
    </section>

    <!-- Step 3 -->
    <section class="story-step">
      <h2>STEP 3 — 상계 후 최종 송금</h2>
      <p class="lead">
        서로 주고받을 금액을 상계(Netting)하여 송금 횟수를 최소화합니다.
      </p>
      <div class="chat">
        <div class="bubble b">B: A에게 <b>2,000원</b> 송금하면 끝!</div>
        <div class="bubble c">C: A에게 <b>3,000원</b> 송금하면 끝!</div>
      </div>
    </section>

    <!-- 버튼 -->
    <div class="btn-area">
      <button class="back-btn" @click="goBack">뒤로가기</button>
    </div>
  </div>
</template>

<style scoped>
.story-step {
  margin-bottom: 40px;
}
.story-step h2 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 6px;
}
.lead {
  color: #475569;
  font-size: 15px;
  margin-bottom: 14px;
}

.chat {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.bubble {
  padding: 10px 14px;
  border-radius: 16px;
  max-width: 420px;
  font-size: 14px;
  line-height: 1.4;
}
.bubble.a {
  background: #e0f2fe;
  align-self: flex-start;
}
.bubble.b {
  background: #e9d5ff;
  align-self: center;
}
.bubble.c {
  background: #dcfce7;
  align-self: flex-end;
}
</style>