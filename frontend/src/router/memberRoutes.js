export default [
    // 로그인
    {
        path: '/login',
        name: 'LoginPage',
        component: () => import('@/views/member/LoginPage.vue')
    },

    // 약관 동의
    {
        path: '/agreement',
        name: 'AgreementPage',
        component: () => import('@/views/member/AgreementPage.vue')
    },

    // 약관 동의 - 이용 약관 동의
    {
        path: '/agreement/terms',
        name: 'AgreementTerms',
        component: () => import('@/views/member/agreement/AgreementTerms.vue')
    },

    // 약관 동의 - 개인 정보 이용 및 수집 동의
    {
        path: '/agreement/privacy',
        name: 'AgreementPrivacy',
        component: () => import('@/views/member/agreement/AgreementPrivacy.vue')
    },

    // 약관 동의 - 푸시 알림 발송 동의
    {
        path: '/agreement/push',
        name: 'AgreementPush',
        component: () => import('@/views/member/agreement/AgreementPush.vue')
    },

    // 회원가입
    {
        path: '/join',
        name: 'JoinPage',
        component: () => import('@/views/member/JoinPage.vue')
    },

    // 마이페이지
    {
        path: '/mypage',
        name: 'MyPage',
        component: () => import('@/views/member/MyPage.vue')
    },

    // 회원 정보 확인 (비밀번호 입력)
    {
        path: '/my/info',
        name: 'MyInfoPasswordPage',
        component: () => import('@/views/member/mypage/MyInfoPasswordPage.vue')
    },

    // 회원 정보 수정
    {
        path: '/my/update',
        name: 'MyInfoUpdatePage',
        component: () => import('@/views/member/mypage/MyInfoUpdatePage.vue')
    },

    // 지난 여행
    {
        path: '/my/last-trip',
        name: 'LastTripPage',
        component: () => import('@/views/member/mypage/LastTripPage.vue')
    }
]