<template>
  <div class="travel-card" @click="handleClick">
    <div class="card-content">
      <div class="travel-info">
        <div class="travel-title">
          {{ tripName }}
        </div>
        <div class="travel-dates">
          {{ formattedDate }}
        </div>
      </div>
      <div class="arrow-icon">
        <ChevronRight :size="24" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ChevronRight } from 'lucide-vue-next'

// Props 정의
const props = defineProps({
  tripName: {
    type: String,
    required: true,
    default: '서울 우정 여행'
  },
  startDate: {
    type: String,
    required: true,
    default: '2025-07-10'
  },
  endDate: {
    type: String,
    required: true,
    default: '2025-07-12'
  },
  tripId: {
    type: Number,
    required: false
  }
})

// Events 정의
const emit = defineEmits(['click'])

// 날짜 포맷팅
const formattedDate = computed(() => {
  const formatDate = (dateStr) => {
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}.${month}.${day}`
  }

  return `${formatDate(props.startDate)} ~ ${formatDate(props.endDate)}`
})

// 클릭 핸들러
const handleClick = () => {
  emit('click', {
    tripId: props.tripId,
    tripName: props.tripName,
    startDate: props.startDate,
    endDate: props.endDate
  })
}
</script>

<style scoped>
.travel-card {
  width: 342px;
  height: 91px;
  background: white;
  box-shadow: 0px 4px 4px -3px rgba(0, 0, 0, 0.25);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.travel-card:hover {
  box-shadow: 0px 6px 8px -3px rgba(0, 0, 0, 0.3);
  transform: translateY(-1px);
}

.travel-card:active {
  transform: translateY(0);
  box-shadow: 0px 4px 4px -3px rgba(0, 0, 0, 0.25);
}

.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  height: 100%;
  box-sizing: border-box;
}

.travel-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.travel-title {
  color: #575757;
  font-size: 24px;
  font-family: Inter, sans-serif;
  font-weight: 700;
  line-height: 24px;
  word-wrap: break-word;
  margin: 0;
}

.travel-dates {
  opacity: 0.8;
  color: #575757;
  font-size: 14px;
  font-family: Inter, sans-serif;
  font-weight: 400;
  line-height: 20px;
  word-wrap: break-word;
  margin: 0;
}

.arrow-icon {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #575757;
  transition: transform 0.2s ease;
}

.travel-card:hover .arrow-icon {
  transform: translateX(2px);
}

/* 반응형 대응 */
@media (max-width: 400px) {
  .travel-card {
    width: 100%;
    max-width: 342px;
  }

  .travel-title {
    font-size: 20px;
    line-height: 22px;
  }

  .travel-dates {
    font-size: 12px;
  }
}
</style>