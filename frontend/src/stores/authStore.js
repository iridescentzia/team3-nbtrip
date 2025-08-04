// stores/authStore.js
import { defineStore } from 'pinia'
import { loginMember, logoutMember, getMyInfo, setStoredToken, clearTokens } from '@/api/memberApi.js'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        accessToken: null
    }),

    actions: {
        // 로그인
        async login({ email, password, fcmToken }) {
            try {
                const response = await loginMember({ email, password, fcmToken })
                const { accessToken, userInfo } = response
                this.accessToken = accessToken
                this.user = userInfo

                // 토큰 저장
                setStoredToken(accessToken)
                localStorage.setItem('userInfo', JSON.stringify(userInfo))
                return true
            } catch (err) {
                console.error('로그인 실패:', err.message)
                throw err
            }
        },

        // 로그인 세션 복구
        async restoreSession() {
            const token = localStorage.getItem('accessToken')
            const userInfo = localStorage.getItem('userInfo')
            if (token && userInfo) {
                this.accessToken = token
                this.user = JSON.parse(userInfo)
            } else {
                this.accessToken = null
                this.user = null
            }
        },

        // 로그아웃
        async logout() {
            try {
                await logoutMember()
            } catch (err) {
                console.warn('로그아웃 중 오류:', err.message)
            } finally {
                this.accessToken = null
                this.user = null
                clearTokens()
                localStorage.removeItem('userInfo')
            }
        },

        // 사용자 정보 새로고침
        async refreshUserInfo() {
            try {
                const data = await getMyInfo()
                this.user = data
                localStorage.setItem('userInfo', JSON.stringify(data))
            } catch (err) {
                console.error('유저 정보 갱신 실패:', err.message)
            }
        },

        setToken(token) {
            this.accessToken = token
        },
        setUser(user) {
            this.user = user
        }
    }
})