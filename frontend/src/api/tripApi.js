import api from '@/api'
const BASE_URL = '/trips'

export default {
    async getTripDetail(tripId){
        const { data } = await api.get(`${BASE_URL}/${tripId}`);
        console.log("data:" + JSON.stringify(data));
        return data
    },
    async createTrip(params) {
        console.log("📤 보내는 데이터:", params);
        await api.post(`${BASE_URL}/`, params);
    },
    async getDisabledDates(){
        const { data } = await api.get(`${BASE_URL}/`);
        console.log("data:" + JSON.stringify(data));
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
    async searchNickname(Nickname){
        const { data } = await api.get(`/users/search/${Nickname}`);
        return data;
    }
}