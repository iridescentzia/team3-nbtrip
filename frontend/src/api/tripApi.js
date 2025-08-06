import api from '@/api'
const BASE_URL = '/trips'

export default {
    async getCurrentTripId() {
        const { data } = await api.get(`${BASE_URL}/current`);
        console.log('data:', data);
        return data;
    },
    async getTripDetail(tripId){
        const { data } = await api.get(`${BASE_URL}/${tripId}`);
        console.log("get tripDetail: " + tripId);
        console.log("getTripDetail: ", data)
        return data
    },
    async createTrip(params) {
        console.log('보내는 데이터:', params);
        await api.post(`${BASE_URL}/`, params);
    },
    async getDisabledDates(){
        const { data } = await api.get(`${BASE_URL}/`);
        console.log("data:" + JSON.stringify(data));

        // tripId가 있으면 해당 tripId 제외
        const filteredData = tripId
            ? data.filter(item => item.tripId !== tripId)
            : data;

        const period = filteredData.map(item => [item.startDate, item.endDate]);
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
            date.toLocaleString('ko-KR', { timeZone: 'Asia/Seoul' })
        ));
        return allDates;
    },
    async isAvailableDate(startDate, endDate) {
        const response = await api.get(`${BASE_URL}/`);
        const data = response.data;

        console.log("data:", data);

        // 기간 배열 만들기
        const period = data.map(item => [
            item.startDate,
            item.endDate
        ]);

        console.log(`주어진 일정: ${startDate} ~ ${endDate}`);
        console.log('period:', JSON.stringify(period));

        // 겹치는 날짜 있는지 확인
        for (const [start, end] of period) {
            console.log(`내가 가진 일정 시작: ${start}, 끝: ${end}`);
            if (
                (start <= startDate && startDate <= end) ||  // 새 일정 시작이 기존 기간 안에 있음
                (start <= endDate && endDate <= end) ||      // 새 일정 끝이 기존 기간 안에 있음
                (startDate <= start && end <= endDate)       // 기존 기간이 새 일정에 완전히 포함됨
            ) {
                return false;  // 겹치는 기간 발견
            }
        }

        return true; // 겹치는 기간 없음
    },
    async acceptInvitation(tripId){
        await api.put(`${BASE_URL}/${tripId}/join`, {})
    },
    async searchNickname(Nickname){
        const { data } = await api.get(`/users/search/${Nickname}`);
        return data;
    },
    // 전체 여행 목록 조회
    async fetchTrips() {
        const { data } = await api.get(`${BASE_URL}`);
        return data;
    },
    async getUserId() {
        const { data } = await api.get(`${BASE_URL}/getId`);
        return data;
    },
    async getTripsByStatus(status) {
        const { data } = await api.get(`${BASE_URL}/status/${status}`);
        return data;
    },
    async updateTrip(param){
        await api.put(`${BASE_URL}/${param.tripId}/update`, param);
    },
    async closeTrip(tripId){
        await api.put(`${BASE_URL}/${tripId}/status`);
    }

}