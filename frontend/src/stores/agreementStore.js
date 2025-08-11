import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAgreementStore = defineStore('agreement', () => {
    // 약관별 체크 상태
    const agreements = ref({
        terms: false,
        privacy: false,
        myData: false,
        push: false
    })

    // 개별 항목 체크
    const check = (id) => {
        if (agreements.value.hasOwnProperty(id)) {
            agreements.value[id] = true
        }
    }

    // 전체 초기화
    const reset = () => {
        agreements.value = {
            terms: false,
            privacy: false,
            myData: false,
            push: false
        }
    }

    return {
        agreements,
        check,
        reset,
    }
})