import { defineStore } from 'pinia'
import { ref } from 'vue';

export const useTravelCreateStore = defineStore('travelCreate', () => {
    const tripName = ref("")
    const startDate = ref()
    const endDate = ref()
    const budget = ref()

    return { tripName, startDate, endDate, budget }
})
