<script setup>
import {useRouter} from "vue-router";
import Header from "@/components/layout/Header.vue";
import catImage from "@/assets/img/withtokki.png";

const router = useRouter();

const handleBack = () => {
  router.back();
};
</script>

<template>
  <div class="settlement-explanation">
    <!-- 헤더 -->
    <Header title="정산 과정 안내" @back="handleBack"/>

    <div class="content-container">
      <div class="intro-section">
        <h1>정산 과정을 간단히 설명해 드릴게요!</h1>
        <p>이해를 돕기 위해 예시를 준비했어요.</p>
        <img class="cat-img" :src="catImage" alt="고양이와 토끼" />
      </div>

      <!-- Step 1 -->
      <section class="story-step">
        <h2>STEP 1 결제 기록 수집</h2>
        <p class="lead">
          여행에서 발생한 결제내역을 모두 모아요.<br />
          누가 결제했고, 누가 참여했는지 기록합니다.
        </p>
        <div class="chat">
          <div class="bubble a">A가 12,000원 결제 (점심)</div>
          <div class="bubble b">B가 20,000원 결제 (카페)</div>
          <div class="bubble c">C가 9,000원 결제 (간식)</div>
        </div>
      </section>

      <!-- Step 2 -->
      <section class="story-step">
        <h2>STEP 2 서로 줄 돈 계산</h2>
        <p class="lead">
          각 결제를 참여 인원 수로 나누어, 누가 누구에게 얼마를 주고받아야 하는지 계산해요.
        </p>
        <div class="chat">
          <div class="bubble a">A가 B에게 <b>5,000원</b> 보내야 함</div>
          <div class="bubble b">B가 C에게 <b>10,000원</b> 보내야 함</div>
          <div class="bubble c">C가 A에게 <b>3,000원</b> 보내야 함</div>
        </div>
      </section>

      <!-- Step 3 -->
      <section class="story-step">
        <h2>STEP 3 상계 후 최종 송금</h2>
        <p class="lead">
          서로 주고받을 금액을 상계(Netting)하여 송금 횟수를 최소화합니다.
        </p>
        <div class="chat">
          <div class="bubble b">B가 A에게 <b>2,000원</b> 송금하면 끝!</div>
          <div class="bubble c">C가 A에게 <b>3,000원</b> 송금하면 끝!</div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.settlement-explanation {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--theme-bg);
}

.content-container {
  flex: 1;
  overflow-y: auto;
  padding: 3rem 1.25rem 1.5rem 1.25rem;
  -webkit-overflow-scrolling: touch; /* iOS 부드러운 스크롤 */
}

.intro-section {
  text-align: center;
  margin-bottom: 0.25rem;
  padding: 1rem 0;
}

.intro-section h1 {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--theme-text);
  margin-bottom: 0.75rem;
  line-height: 1.4;
}

.intro-section p {
  color: var(--theme-text-light);
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  line-height: 1.5;
}

.cat-img {
  width: 100%;
  max-width: 200px;
  height: auto;
  border-radius: 12px;
  margin: 0 auto;
  display: block;
}

.story-step {
  margin-bottom: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.story-step h2 {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: var(--theme-text);
}

.lead {
  color: var(--theme-text-light);
  font-size: 0.875rem;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.chat {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.bubble {
  padding: 0.75rem 1rem;
  border-radius: 16px;
  max-width: 280px;
  font-size: 0.875rem;
  line-height: 1.4;
  word-break: keep-all;
}

.bubble.a {
  background: var(--theme-secondary);
  align-self: flex-start;
}

.bubble.b {
  background: #fdfae2;
  align-self: center;
}

.bubble.c {
  background: var(--theme-primary);
  align-self: flex-end;
}

.bubble b {
  color: var(--theme-blue);
  font-weight: 600;
}

/* 스크롤바 스타일링 (웹킷 브라우저) */
.content-container::-webkit-scrollbar {
  width: 4px;
}

.content-container::-webkit-scrollbar-track {
  background: transparent;
}

.content-container::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 2px;
}

.content-container::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.3);
}

/* 모바일 최적화 */
@media (max-width: 480px) {
  .content-container {
    padding: 0 0.75rem 1rem 0.75rem;
  }

  .story-step {
    padding: 0.75rem;
    margin-bottom: 1.5rem;
  }

  .bubble {
    max-width: 240px;
    padding: 0.625rem 0.875rem;
    font-size: 0.8125rem;
  }

  .intro-section h1 {
    font-size: 1.125rem;
  }
}
</style>