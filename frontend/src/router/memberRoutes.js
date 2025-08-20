export default [
  // =========== 로그인/회원가입 ===========

  // 로그인
  {
    path: '/login',
    name: 'LoginPage',
    component: () => import('@/pages/member/LoginPage.vue'),
  },

  // 회원가입
  {
    path: '/join',
    name: 'JoinPage',
    component: () => import('@/pages/member/JoinPage.vue'),
  },

  // =========== 약관 동의(회원가입 중) ===========

  // 약관 동의
  {
    path: '/agreement',
    name: 'AgreementPage',
    component: () => import('@/pages/member/AgreementPage.vue'),
  },

  // 약관 동의 - 이용 약관 동의
  {
    path: '/agreement/terms',
    name: 'AgreementTerms',
    component: () => import('@/pages/member/agreement/AgreementTermsPage.vue'),
    props: (route) => ({ viewOnly: route.query.viewOnly === 'true' }),
  },

  // 약관 동의 - 개인 정보 이용 및 수집 동의
  {
    path: '/agreement/privacy',
    name: 'AgreementPrivacy',
    component: () =>
      import('@/pages/member/agreement/AgreementPrivacyPage.vue'),
    props: (route) => ({ viewOnly: route.query.viewOnly === 'true' }),
  },

  // 약관 동의 - 마이데이터 수집 및 활용 동의
  {
    path: '/agreement/mydata',
    name: 'AgreementMyData',
    component: () => import('@/pages/member/agreement/AgreementMyDataPage.vue'),
    props: (route) => ({ viewOnly: route.query.viewOnly === 'true' }),
  },

  // 약관 동의 - 푸시 알림 발송 동의
  {
    path: '/agreement/push',
    name: 'AgreementPush',
    component: () => import('@/pages/member/agreement/AgreementPushPage.vue'),
    props: (route) => ({ viewOnly: route.query.viewOnly === 'true' }),
  },

  // ============ 마이페이지 =============

  // 마이페이지
  {
    path: '/mypage',
    name: 'MyPage',
    component: () => import('@/pages/member/MyPage.vue'),
  },

  // 회원 정보 확인 (비밀번호 입력)
  {
    path: '/mypage/info',
    name: 'MyInfoPasswordPage',
    component: () => import('@/pages/member/mypage/MyInfoPasswordPage.vue'),
  },

  // 회원 정보 수정
  {
    path: '/mypage/updateInfo',
    name: 'MyInfoUpdatePage',
    component: () => import('@/pages/member/mypage/MyInfoUpdatePage.vue'),
  },

  // 결제 수단 확인 (비밀번호 입력)
  {
    path: '/mypage/payment',
    name: 'MyPaymentPasswordPage',
    component: () => import('@/pages/member/mypage/MyPaymentPasswordPage.vue'),
  },

  // 결제 수단 수정
  {
    path: '/mypage/updatePayment',
    name: 'MyPaymentUpdatePage',
    component: () => import('@/pages/member/mypage/MyPaymentUpdatePage.vue'),
  },

  // 공지사항 및 FAQ
  {
    path: '/mypage/faq',
    name: 'MyPageFaqPage',
    component: () => import('@/pages/member/mypage/FAQPage.vue'),
  },

  // 이용 약관
  {
    path: '/mypage/terms',
    name: 'MyPageTermsPage',
    component: () => import('@/pages/member/mypage/TermsPage.vue'),
    props: { viewOnly: true },
  },

  // 지난 여행
  {
    path: '/mypage/closed-trip',
    name: 'LastTripListPage',
    component: () => import('@/pages/member/mypage/ClosedTripListPage.vue'),
  },

  // 예정된 여행
  {
    path: '/mypage/ready-trip',
    name: 'ClosedTripListPage',
    component: () => import('@/pages/member/mypage/ReadyTripListPage.vue'),
  },

  // ============ 약관 상세 보기 (viewOnly) ============
  {
    path: '/terms',
    name: 'TermsViewOnly',
    component: () => import('@/pages/member/agreement/AgreementTermsPage.vue'),
    props: { viewOnly: true },
  },
  {
    path: '/privacy',
    name: 'PrivacyViewOnly',
    component: () =>
      import('@/pages/member/agreement/AgreementPrivacyPage.vue'),
    props: { viewOnly: true },
  },
  {
    path: '/push',
    name: 'PushViewOnly',
    component: () => import('@/pages/member/agreement/AgreementPushPage.vue'),
    props: { viewOnly: true },
  },

  // ============ 여행 ============

  // 지난 여행
  {
    path: '/my/last-trip',
    name: 'LastTripPage',
    component: () => import('@/pages/member/mypage/ClosedTripListPage.vue'),
  },
];
