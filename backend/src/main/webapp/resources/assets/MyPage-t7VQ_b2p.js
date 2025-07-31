import{_ as m,q as k,x as _,c as M,e as h,f as p,F as b,p as w,g as s,k as x,I as C,t as f,H as I,r as $,o as B,y as a,u as c,B as v}from"./index-S6hHY3P1.js";import{c as o}from"./createLucideIcon-Yd_lUDC3.js";import{C as y}from"./chevron-right-DyGbOhiW.js";/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const z=o("briefcase-conveyor-belt",[["path",{d:"M10 20v2",key:"1n8e1g"}],["path",{d:"M14 20v2",key:"1lq872"}],["path",{d:"M18 20v2",key:"10uadw"}],["path",{d:"M21 20H3",key:"kdqkdp"}],["path",{d:"M6 20v2",key:"a9bc87"}],["path",{d:"M8 16V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v12",key:"17n9tx"}],["rect",{x:"4",y:"6",width:"16",height:"10",rx:"2",key:"1097i5"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const q=o("briefcase",[["path",{d:"M16 20V4a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16",key:"jecpp"}],["rect",{width:"20",height:"14",x:"2",y:"6",rx:"2",key:"i6l2r4"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const H=o("circle-user-round",[["path",{d:"M18 20a6 6 0 0 0-12 0",key:"1qehca"}],["circle",{cx:"12",cy:"10",r:"4",key:"1h16sb"}],["circle",{cx:"12",cy:"12",r:"10",key:"1mglay"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const T=o("house",[["path",{d:"M15 21v-8a1 1 0 0 0-1-1h-4a1 1 0 0 0-1 1v8",key:"5wwlr5"}],["path",{d:"M3 10a2 2 0 0 1 .709-1.528l7-5.999a2 2 0 0 1 2.582 0l7 5.999A2 2 0 0 1 21 10v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z",key:"1d0kgt"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const V=o("plane",[["path",{d:"M17.8 19.2 16 11l3.5-3.5C21 6 21.5 4 21 3c-1-.5-3 0-4.5 1.5L13 8 4.8 6.2c-.5-.1-.9.1-1.1.5l-.3.5c-.2.5-.1 1 .3 1.3L9 12l-2 3H4l-1 1 3 2 2 3 1-1v-3l3-2 3.5 5.3c.3.4.8.5 1.3.3l.5-.2c.4-.3.6-.7.5-1.2z",key:"1v9wt8"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const F=o("scan",[["path",{d:"M3 7V5a2 2 0 0 1 2-2h2",key:"aa7l1z"}],["path",{d:"M17 3h2a2 2 0 0 1 2 2v2",key:"4qcy5o"}],["path",{d:"M21 17v2a2 2 0 0 1-2 2h-2",key:"6vwrx8"}],["path",{d:"M7 21H5a2 2 0 0 1-2-2v-2",key:"ioqczr"}]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const A=o("user",[["path",{d:"M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2",key:"975kel"}],["circle",{cx:"12",cy:"7",r:"4",key:"17ys0d"}]]),L="/assets/airplane_left-CBgKYIjn.png",P={class:"footer"},R=["onClick"],S={class:"icon"},N={class:"label"},j={__name:"Footer",setup(g){const l=k(),i=_(),n=[{name:"home",label:"홈",icon:T,path:"/home"},{name:"trip",label:"여행",icon:V,path:"/trip"},{name:"payment",label:"결제",icon:F,path:"/payment"},{name:"mypage",label:"내 정보",icon:A,path:"/mypage"}],u=M(()=>i.path),t=e=>{i.path!==e.path&&l.push(e.path)};return(e,r)=>(p(),h("footer",P,[(p(),h(b,null,w(n,d=>s("div",{key:d.name,class:I(["footer-item",{active:u.value===d.path}]),onClick:se=>t(d)},[s("div",S,[(p(),x(C(d.icon))),r[0]||(r[0]=s("br",null,null,-1))]),s("span",N,f(d.label),1)],10,R)),64))]))}},D=m(j,[["__scopeId","data-v-0ee8ff80"]]),U={class:"mypage-wrapper"},E={class:"content"},K={class:"profile-section"},Q={class:"nickname"},Y={class:"icon-section"},G={class:"icon-button"},J={class:"icon-wrapper"},O={class:"icon-button"},W={class:"icon-wrapper"},X={class:"icon-button"},Z={class:"menu-list"},ee={__name:"MyPage",setup(g){const l=k(),i=$({nickname:"",name:""});B(async()=>{try{const t=await getMyInfo();console.log("응답 결과:",t.data),t.data?.success&&t.data?.data?i.value=t.data.data:console.error("유저 정보 조회 실패:",t.data?.message||"데이터 없음")}catch(t){console.error("마이페이지 API 에러:",t),t.response?.status===401&&l.push("/login")}});const n=t=>l.push(t),u=async()=>{try{const t=localStorage.getItem("accessToken");await axios.post("/api/auth/logout",{},{headers:{Authorization:`Bearer ${t}`}}),localStorage.removeItem("accessToken"),l.push("/login")}catch(t){console.error("로그아웃 실패:",t)}};return(t,e)=>(p(),h("div",U,[s("div",E,[e[11]||(e[11]=s("header",{class:"header"},[s("h1",null,"마이페이지")],-1)),s("div",K,[e[4]||(e[4]=s("img",{src:L,alt:"프로필",class:"profile-img"},null,-1)),s("div",Q,f(i.value.nickname||"김냥이"),1)]),s("div",Y,[s("div",{class:"icon-wrapper",onClick:e[0]||(e[0]=r=>n("/my/info"))},[s("div",G,[a(c(H),{size:"28"})]),e[5]||(e[5]=s("span",null,"회원 정보",-1))]),s("div",J,[s("div",O,[a(c(q),{size:"28"})]),e[6]||(e[6]=s("span",null,"예정된 여행",-1))]),s("div",W,[s("div",X,[a(c(z),{size:"28"})]),e[7]||(e[7]=s("span",null,"지난 여행",-1))])]),s("div",Z,[s("div",{class:"menu-item",onClick:e[1]||(e[1]=r=>n("/my/payment"))},[e[8]||(e[8]=v(" 결제 수단 관리 ",-1)),a(c(y),{class:"arrow"})]),s("div",{class:"menu-item",onClick:e[2]||(e[2]=r=>n("/faq"))},[e[9]||(e[9]=v(" 공지사항 및 FAQ ",-1)),a(c(y),{class:"arrow"})]),s("div",{class:"menu-item",onClick:e[3]||(e[3]=r=>n("/terms"))},[e[10]||(e[10]=v(" 이용 약관 ",-1)),a(c(y),{class:"arrow"})])]),s("div",{class:"logout",onClick:u},"로그아웃")]),a(D,{class:"footer"})]))}},ne=m(ee,[["__scopeId","data-v-f8f06d2f"]]);export{ne as default};
