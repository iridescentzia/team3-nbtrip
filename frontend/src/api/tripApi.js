import api from '@/api'
const BASE_URL = '/api/trips'

export default {
    async getTripDetail(tripId){
        const { data } = await api.get(`${BASE_URL}/${tripId}`);
        console.log("data:" + JSON.stringify(data));
        return data
    },
    // async create(params) {
    //
    // }
    async getDiabledDates(){
        const { data } = await api.get(`${BASE_URL}/`);
        const period = data.map(item => [item.startDate, item.endDate]);
        const allDates = [];
        period.forEach(([start, end]) => {
            const current = new Date(start);
            const last = new Date(end);

            while (current <= last) {
                // 날짜 객체 복사 후 push (참조 방지)
                allDates.push(new Date(current));
                current.setDate(current.getDate() + 1);
            }
        });
        console.log("data:", allDates.map(date =>
            date.toLocaleString("ko-KR", { timeZone: "Asia/Seoul" })
        ));
        return allDates;
    },

    // 전체 여행 목록 조회
    async fetchTrips(){
        const { data } = await api.get("/trips");
        return data;
    }
}