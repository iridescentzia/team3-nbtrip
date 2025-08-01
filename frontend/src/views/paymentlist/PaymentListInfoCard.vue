<template>
  <div class="expense-card">
    <div class="left">
      <div class="title">{{ title }}</div>
      <div class="sub">{{ sub }}</div>
    </div>
    <div class="right">{{ amount.toLocaleString() }}원</div>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  sub: String,
  amount: Number,
});
</script>

<style scoped>
.expense-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  border-radius: 12px;
  padding: 18px 25px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  margin-bottom: 12px;
  max-width: 343px;
}
.left .title {
  font-weight: 1000;
  font-size: 16px;
  color: #4A4A4A;
}
.left .sub {
  font-size: 14px;
  color: #AAAAAA;
  margin-top: 10px;
}
.right {
  font-weight: 1000;
  font-size: 18px;
  color: #4A4A4A;
}
</style>
