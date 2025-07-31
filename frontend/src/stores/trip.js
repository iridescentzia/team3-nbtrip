import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import tripApi from '@/api/tripApi' // 전체 default 객체로 불러오기

export const useTripStore = defineStore('trip', () => {
  const trips = ref([])

  const fetchTrips = async () => {
    const data = await tripApi.fetchTrips()  
    trips.value = Array.isArray(data) ? data : []
  }

  const currentTrip = computed(() => {
    const now = Date.now()
    return trips.value.find(trip => {
      return (
        trip.tripStatus === 'ACTIVE' &&
        now >= trip.startDate &&
        now <= trip.endDate
      )
    })
  })

  return {
    trips,
    fetchTrips,
    currentTrip,
  }
})
