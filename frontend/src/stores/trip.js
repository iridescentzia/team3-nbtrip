import {defineStore} from 'pinia'
import axios from 'axios'
import {ref} from 'vue'

export const useTripStore = defineStore('trip', ()=>{
    const trip = ref(null)

    const fetchActiveTrip = async () =>{
        try{
            const res = await axios.get('/api/trips')
            const trips = res.data

            if(Array.isArray(trips) && trips.length > 0){
                const now = new Date()

                const activeTrips = trips
                    .filter(t => t.tripStatus === 'ACTIVE' && new Date(t.startDate) >= now)
                    .sort((a, b) => new Date(a.startDate) - new Date(b.startDate))

                trip.value = activeTrips[0] || null
            }
        }catch(e){
            console.error('진행 중인 여행 정보 가져오기 실패: ', e)
        }
    }
    return {trip, fetchActiveTrip}

})