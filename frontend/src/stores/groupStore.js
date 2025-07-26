import { defineStore } from 'pinia'
import { ref, computed, reactive } from 'vue';

export const travelCreateStore = defineStore('travelCreate', () => {
    const travelName = ref("")
    const startDate = ref()
    const endDate = ref()
    const budget = ref()

    return { travelName, startDate, endDate, budget }
})
