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
  padding: 16px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  margin-bottom: 12px;
  font-family: sans-serif;
}
.left .title {
  font-weight: bold;
  font-size: 14px;
}
.left .sub {
  font-size: 12px;
  color: gray;
  margin-top: 4px;
}
.right {
  font-weight: bold;
  font-size: 15px;
}
</style>
