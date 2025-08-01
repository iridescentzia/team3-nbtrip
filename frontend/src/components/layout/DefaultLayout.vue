<template>
  <div class="view-wrapper">
    <div class="mobile-view">
      <slot></slot>
    </div>
  </div>
</template>

<style scoped>
/* 중앙 정렬 */
.view-wrapper{
  display: flex;
  justify-content: center;
  width: 100%;
  min-height: 100vh;
  background-color: white;
  padding: 2rem 0;
  z-index: 1;
}

/* 모바일 화면 */
.mobile-view{
  z-index: 1;
  width: 100%;
  max-width: 24rem;
  background-color: #f8f9fa;
  display:flex;

  flex-direction: column;
  border-radius: 1.5rem;
  box-shadow: 0 25px 50px -12px rgb(0 0 0 /0.25);
  overflow: hidden;
  position: relative;
  height: 844px;
  max-height: 100vh;
}
</style>
