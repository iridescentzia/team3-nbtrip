// stores/settlementStore.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
    getMySettlementDetails,
    transferMoney,
    getSettlementSummary,
    getSettlementsByTripId,
    requestSettlement
} from '@/api/settlementApi'

export const useSettlementStore = defineStore('settlement', () => {
    // ✅ 상태 (State)
    const summaryData = ref(null)         // SummaryView용
    const mySettlementData = ref(null)    // DetailView + Notice용
    const groupSettlementData = ref(null) // RequestView용
    const selectedMember = ref(null)      // RequestView 드롭다운용
    const showTransferModal = ref(false)  // DetailView 모달용
    const isLoading = ref(false)
    const error = ref(null)

    // ✅ 계산된 속성 (Getters)
    const totalReceiveAmount = computed(() => {
        if (!mySettlementData.value?.toReceive) return 0
        return mySettlementData.value.toReceive.reduce((sum, tx) => sum + tx.amount, 0)
    })

    const totalSendAmount = computed(() => {
        if (!mySettlementData.value?.toSend) return 0
        return mySettlementData.value.toSend.reduce((sum, tx) => sum + tx.amount, 0)
    })

    const netBalance = computed(() => {
        return totalReceiveAmount.value - totalSendAmount.value
    })

    // RequestView 계산된 속성
    const toReceiveList = computed(() => {
        if (!groupSettlementData.value?.settlements) return []
        return groupSettlementData.value.settlements.filter(
            (tx) => tx.receiverNickname === selectedMember.value
        )
    })

    const toSendList = computed(() => {
        if (!groupSettlementData.value?.settlements) return []
        return groupSettlementData.value.settlements.filter(
            (tx) => tx.senderNickname === selectedMember.value
        )
    })

    // ✅ 액션 (Actions)

    // SummaryView용 - 정산 요약 조회
    const fetchSummary = async (tripId) => {
        isLoading.value = true
        error.value = null

        try {
            // 최소 로딩 시간 800ms 보장
            const minLoadingTime = new Promise(resolve => setTimeout(resolve, 800))

            const [response] = await Promise.all([
                getSettlementSummary(tripId),
                minLoadingTime
            ])

            summaryData.value = response.data
            return response.data
        } catch (err) {
            console.error('정산 요약 정보 로딩 실패:', err)

            if (err.response?.status === 403) {
                error.value = '그룹장만 정산을 요청할 수 있습니다.'
                // 3초 후 여행 상세 페이지로 리다이렉트 로직은 컴포넌트에서 처리
            } else if (err.response?.status === 404) {
                error.value = '여행 정보를 찾을 수 없습니다.'
            } else {
                error.value = '데이터를 불러오는 데 실패했습니다.'
            }
            throw err
        } finally {
            isLoading.value = false
        }
    }

    // SummaryView용 - 정산 생성 요청
    const createSettlement = async (tripId) => {
        try {
            const response = await requestSettlement({ tripId })
            return response.data
        } catch (err) {
            console.error('정산 생성 실패:', err)
            throw err
        }
    }

    // RequestView용 - 그룹 정산 데이터 조회
    const fetchGroupSettlement = async (tripId) => {
        isLoading.value = true
        error.value = null

        try {
            // 최소 로딩 시간 700ms 보장
            const minLoadingTime = new Promise(resolve => setTimeout(resolve, 700))

            const [settlementsResponse, summaryResponse] = await Promise.all([
                getSettlementsByTripId(tripId),
                getSettlementSummary(tripId),
                minLoadingTime
            ])

            // 안전한 데이터 처리
            const settlements = settlementsResponse.data || []
            const summary = summaryResponse.data

            // 데이터 검증
            if (!Array.isArray(settlements)) {
                console.error('settlements is not an array:', settlements)
                error.value = '정산 데이터 형식이 올바르지 않습니다.'
                return
            }

            if (settlements.length === 0) {
                error.value = '아직 정산이 생성되지 않았습니다. 정산을 먼저 생성해주세요.'
                return
            }

            // 멤버 목록 추출
            const membersSet = new Set()
            settlements.forEach(s => {
                if (s.senderNickname) membersSet.add(s.senderNickname)
                if (s.receiverNickname) membersSet.add(s.receiverNickname)
            })

            groupSettlementData.value = {
                settlements: settlements,
                members: Array.from(membersSet),
                tripName: summary.tripName,
                totalAmount: summary.totalAmount,
                transactions: settlements
            }

            // 첫 번째 멤버 자동 선택
            if (Array.from(membersSet).length > 0) {
                selectedMember.value = Array.from(membersSet)[0]
            }

            return groupSettlementData.value
        } catch (err) {
            console.error('정산 정보 로딩 실패:', err)

            if (err.response?.status === 403) {
                error.value = '그룹장만 정산을 조회할 수 있습니다.'
            } else {
                error.value = '데이터를 불러오는 데 실패했습니다.'
            }
            throw err
        } finally {
            isLoading.value = false
        }
    }

    // DetailView + Notice용 - 개인 정산 내역 조회
    const fetchMySettlement = async (tripId) => {
        isLoading.value = true
        error.value = null

        try {
            const response = await getMySettlementDetails(tripId)
            mySettlementData.value = response.data
            return response.data
        } catch (err) {
            console.error('개인 정산 정보 로딩 실패:', err)
            error.value = '데이터를 불러오는 데 실패했습니다.'
            throw err
        } finally {
            isLoading.value = false
        }
    }

    // DetailView용 - 송금 실행
    const executeTransfer = async () => {
        if (!mySettlementData.value?.toSend) {
            throw new Error('보낼 정산 데이터가 없습니다.')
        }

        try {
            const settlementIdsToSend = mySettlementData.value.toSend.map(tx => tx.settlementId)
            const response = await transferMoney({ settlementIds: settlementIdsToSend })
            return response.data
        } catch (err) {
            console.error('송금 실패:', err)
            throw err
        }
    }

    // 모달 관리
    const openTransferModal = () => {
        if (totalSendAmount.value === 0) {
            throw new Error('보낼 돈이 없습니다.')
        }
        showTransferModal.value = true
    }

    const closeTransferModal = () => {
        showTransferModal.value = false
    }

    // 데이터 초기화
    const clearAllData = () => {
        summaryData.value = null
        mySettlementData.value = null
        groupSettlementData.value = null
        selectedMember.value = null
        showTransferModal.value = false
        error.value = null
    }

    const clearSummaryData = () => {
        summaryData.value = null
    }

    const clearMySettlementData = () => {
        mySettlementData.value = null
    }

    const clearGroupSettlementData = () => {
        groupSettlementData.value = null
        selectedMember.value = null
    }

    return {
        // State
        summaryData,
        mySettlementData,
        groupSettlementData,
        selectedMember,
        showTransferModal,
        isLoading,
        error,

        // Getters
        totalReceiveAmount,
        totalSendAmount,
        netBalance,
        toReceiveList,
        toSendList,

        // Actions
        fetchSummary,
        createSettlement,
        fetchGroupSettlement,
        fetchMySettlement,
        executeTransfer,
        openTransferModal,
        closeTransferModal,
        clearAllData,
        clearSummaryData,
        clearMySettlementData,
        clearGroupSettlementData
    }
})