export default [
    // =========== 로그인/회원가입 ===========

    // 로그인
    {
        path: '/login',
        name: 'LoginPage',
        component: () => import('@/views/member/LoginPage.vue')
    },

    // 회원가입
    {
        path: '/join',
        name: 'JoinPage',
        component: () => import('@/views/member/JoinPage.vue')
    },

    // =========== 약관 동의(회원가입 중) ===========

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
        component: () => import('@/views/member/agreement/AgreementTerms.vue'),
        props: route => ({ viewOnly: route.query.viewOnly === 'true' })
    },

    // 약관 동의 - 개인 정보 이용 및 수집 동의
    {
        path: '/agreement/privacy',
        name: 'AgreementPrivacy',
        component: () => import('@/views/member/agreement/AgreementPrivacy.vue'),
        props: route => ({ viewOnly: route.query.viewOnly === 'true' })
    },

    // 약관 동의 - 푸시 알림 발송 동의
    {
        path: '/agreement/push',
        name: 'AgreementPush',
        component: () => import('@/views/member/agreement/AgreementPush.vue'),
        props: route => ({ viewOnly: route.query.viewOnly === 'true' })
    },

    // ============ 마이페이지 =============

    // 마이페이지
    {
        path: '/mypage',
        name: 'MyPage',
        component: () => import('@/views/member/MyPage.vue')
    },

    // 회원 정보 확인 (비밀번호 입력)
    {
        path: '/mypage/info',
        name: 'MyInfoPasswordPage',
        component: () => import('@/views/member/mypage/MyInfoPasswordPage.vue')
    },

    // 회원 정보 수정
    {
        path: '/mypage/updateInfo',
        name: 'MyInfoUpdatePage',
        component: () => import('@/views/member/mypage/MyInfoUpdatePage.vue')
    },

    // 결제 수단 확인 (비밀번호 입력)
    {
        path : '/mypage/payment',
        name: 'MyPaymentPasswordPage',
        component: () => import('@/views/member/mypage/MyPaymentPasswordPage.vue')
    },

    // 결제 수단 수정
    {
        path: '/mypage/updatePayment',
        name: 'MyPaymentUpdatePage',
        component: () => import('@/views/member/mypage/MyPaymentUpdatePage.vue')
    },

    // 공지사항 및 FAQ
    {
        path: '/mypage/faq',
        name: 'FaqPage',
        component: () => import('@/views/member/mypage/FAQ.vue')
    },

    // 이용 약관
    {
        path: '/mypage/terms',
        name: 'TermsViewOnly',
        component: () => import('@/views/member/mypage/Terms.vue'),
        props: { viewOnly: true }
    },

    // ============ 약관 상세 보기 (viewOnly) ============
    {
        path: '/terms',
        name: 'TermsViewOnly',
        component: () => import('@/views/member/agreement/AgreementTerms.vue'),
        props: { viewOnly: true }
    },
    {
        path: '/privacy',
        name: 'PrivacyViewOnly',
        component: () => import('@/views/member/agreement/AgreementPrivacy.vue'),
        props: { viewOnly: true }
    },
    {
        path: '/push',
        name: 'PushViewOnly',
        component: () => import('@/views/member/agreement/AgreementPush.vue'),
        props: { viewOnly: true }
    },

    // ============ 여행 ============

    // 지난 여행
    {
        path: '/my/last-trip',
        name: 'LastTripPage',
        component: () => import('@/views/member/mypage/LastTripPage.vue')
    }
]