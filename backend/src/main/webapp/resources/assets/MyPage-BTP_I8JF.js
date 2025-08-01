import{_ as h,e as _,y as M,f as y,F as b,q as C,h as s,k as w,I as x,t as k,H as I,z as g,g as p,r as $,o as z,m as t,u as i,C as u}from"./index-9_HCdh5a.js";import{c as a}from"./createLucideIcon-CAS1lwv-.js";import{g as B,l as T}from"./memberApi-DtniH5jf.js";import{C as v}from"./chevron-right-CPHkgDSD.js";/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const q=a("briefcase-conveyor-belt",[["path",{d:"M10 20v2",key:"1n8e1g"}],["path",{d:"M14 20v2",key:"1lq872"}],["path",{d:"M18 20v2",key:"10uadw"}],["path",{d:"M21 20H3",key:"kdqkdp"}],["path",{d:"M6 20v2",key:"a9bc87"}],["path",{d:"M8 16V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v12",key:"17n9tx"}],["rect",{x:"4",y:"6",width:"16",height:"10",rx:"2",key:"1097i5"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const H=a("briefcase",[["path",{d:"M16 20V4a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16",key:"jecpp"}],["rect",{width:"20",height:"14",x:"2",y:"6",rx:"2",key:"i6l2r4"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const S=a("circle-user-round",[["path",{d:"M18 20a6 6 0 0 0-12 0",key:"1qehca"}],["circle",{cx:"12",cy:"10",r:"4",key:"1h16sb"}],["circle",{cx:"12",cy:"12",r:"10",key:"1mglay"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const V=a("house",[["path",{d:"M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8",key:"5wwlr5"}],["path",{d:"M3 10a2 2 0 0 1 .709-1.528l7-5.999a2 2 0 0 1 2.582 0l7 5.999A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z",key:"1d0kgt"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const F=a("plane",[["path",{d:"M17.8 19.2 16 11l3.5-3.5C21 6 21.5 4 21 3c-1-.5-3 0-4.5 1.5L13 8 4.8 6.2c-.5-.1-.9.1-1.1.5l-.3.5c-.2.5-.1 1 .3 1.3L9 12l-2 3H4l-1 1 3 2 2 3 1-1v-3l3-2 3.5 5.3c.3.4.8.5 1.3.3l.5-.2c.4-.3.6-.7.5-1.2z",key:"1v9wt8"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const L=a("scan",[["path",{d:"M3 7V5a2 2 0 0 1 2-2h2",key:"aa7l1z"}],["path",{d:"M17 3h2a2 2 0 0 1 2 2v2",key:"4qcy5o"}],["path",{d:"M21 17v2a2 2 0 0 1-2 2h-2",key:"6vwrx8"}],["path",{d:"M7 21H5a2 2 0 0 1-2-2v-2",key:"ioqczr"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const P=a("user",[["path",{d:"M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2",key:"975kel"}],["circle",{cx:"12",cy:"7",r:"4",key:"17ys0d"}]]),R="/assets/airplane_left-CBgKYIjn.png",A={class:"footer"},N=["onClick"],j={class:"icon"},D={class:"label"},U={__name:"Footer",setup(f){const n=g(),l=M(),r=[{name:"home",label:"홈",icon:V,path:"/home"},{name:"trip",label:"여행",icon:F,path:"/trip"},{name:"payment",label:"결제",icon:L,path:"/payment"},{name:"mypage",label:"내 정보",icon:P,path:"/mypage"}],m=_(()=>l.path),o=e=>{l.path!==e.path&&n.push(e.path)};return(e,c)=>(p(),y("footer",A,[(p(),y(b,null,C(r,d=>s("div",{key:d.name,class:I(["footer-item",{active:m.value===d.path}]),onClick:te=>o(d)},[s("div",j,[(p(),w(x(d.icon))),c[0]||(c[0]=s("br",null,null,-1))]),s("span",D,k(d.label),1)],10,N)),64))]))}},E=h(U,[["__scopeId","data-v-2d0f5acd"]]),K={class:"mypage-wrapper"},Q={class:"content"},Y={class:"profile-section"},G={class:"nickname"},J={class:"icon-section"},O={class:"icon-button"},W={class:"icon-wrapper"},X={class:"icon-button"},Z={class:"icon-wrapper"},ee={class:"icon-button"},se={class:"menu-list"},oe={__name:"MyPage",setup(f){const n=g(),l=$({nickname:"",name:""});z(async()=>{try{console.log("마이페이지 마운트 시작");const o=await B();console.log("응답 결과:",o),o?.success&&o?.data?(l.value=o.data,console.log("사용자 정보 설정 완료:",l.value)):console.error("유저 정보 조회 실패:",o?.message||"데이터 없음")}catch(o){console.error("마이페이지 API 에러:",o),(o.message?.includes("인증")||o.message?.includes("토큰"))&&(console.log("인증 오류로 로그인 페이지로 이동"),n.push("/login"))}});const r=o=>n.push(o),m=async()=>{try{console.log("로그아웃 시작"),await T(),localStorage.removeItem("accessToken"),localStorage.removeItem("refreshToken"),console.log("로그아웃 성공, 로그인 페이지로 이동"),n.push("/login")}catch(o){console.error("로그아웃 실패:",o),localStorage.removeItem("accessToken"),localStorage.removeItem("refreshToken"),n.push("/login")}};return(o,e)=>(p(),y("div",K,[s("div",Q,[e[11]||(e[11]=s("header",{class:"header"},[s("h1",null,"마이페이지")],-1)),s("div",Y,[e[4]||(e[4]=s("img",{src:R,alt:"프로필",class:"profile-img"},null,-1)),s("div",G,k(l.value.nickname||"김냥이"),1)]),s("div",J,[s("div",{class:"icon-wrapper",onClick:e[0]||(e[0]=c=>r("/my/info"))},[s("div",O,[t(i(S),{size:"28"})]),e[5]||(e[5]=s("span",null,"회원 정보",-1))]),s("div",W,[s("div",X,[t(i(H),{size:"28"})]),e[6]||(e[6]=s("span",null,"예정된 여행",-1))]),s("div",Z,[s("div",ee,[t(i(q),{size:"28"})]),e[7]||(e[7]=s("span",null,"지난 여행",-1))])]),s("div",se,[s("div",{class:"menu-item",onClick:e[1]||(e[1]=c=>r("/my/payment"))},[e[8]||(e[8]=u(" 결제 수단 관리 ",-1)),t(i(v),{class:"arrow"})]),s("div",{class:"menu-item",onClick:e[2]||(e[2]=c=>r("/faq"))},[e[9]||(e[9]=u(" 공지사항 및 FAQ ",-1)),t(i(v),{class:"arrow"})]),s("div",{class:"menu-item",onClick:e[3]||(e[3]=c=>r("/terms"))},[e[10]||(e[10]=u(" 이용 약관 ",-1)),t(i(v),{class:"arrow"})])]),s("div",{class:"logout",onClick:m},"로그아웃")]),t(E,{class:"footer"})]))}},ce=h(oe,[["__scopeId","data-v-8b0776bd"]]);export{ce as default};
