<script setup>
const props = defineProps({
  trip: {
    type: Object,
    required: true,
  },
});
</script>

<template>
  <div class="ad-card">
    <img :src="trip.imageUrl" :alt="trip.city" class="ad-image" />
    <div class="ad-content">
      <div>
        <p class="ad-subtitle">{{ trip.title }}</p>
        <h3 class="ad-city">{{ trip.city }}</h3>
        <p class="ad-description">{{ trip.description }}</p>
      </div>
      <div class="ad-tags">
        <span v-for="tag in trip.tags" :key="tag" class="ad-tag"
          ># {{ tag }}</span
        >
      </div>
    </div>
  </div>
</template>

<style scoped>
.ad-card {
  flex: 0 0 280px;
  width: 280px;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}
.ad-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.12);
}
.ad-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
}
.ad-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex-grow: 1;
}
.ad-subtitle {
  font-size: 14px;
  color: #6b7280;
}
.ad-city {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin-top: 4px;
}
.ad-description {
  font-size: 14px;
  color: #4b5563;
  margin-top: 8px;
  line-height: 1.5;
  /* Limit text to 3 lines */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
.ad-tags {
  margin-top: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.ad-tag {
  background-color: #f3f4f6;
  color: #4b5563;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 999px;
}
</style>
