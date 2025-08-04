import{_ as d,f as e,g as a,h as c,l as n,m as u,u as k,t as f}from"./index-DAtSuver.js";import{c as h}from"./createLucideIcon-Ptc4Oz73.js";/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const m=h("chevron-left",[["path",{d:"m15 18-6-6 6-6",key:"1wnfg3"}]]),p={class:"header"},_={class:"left"},b={class:"center"},B={key:0},y={__name:"Header",props:{title:{type:String,default:""},showBackButton:{type:Boolean,default:!0},backAction:{type:Function,default:null}},emits:["back"],setup(t,{emit:r}){const o=t,i=r,l=()=>{o.backAction?o.backAction():i("back")};return(v,s)=>(a(),e("header",p,[c("div",_,[t.showBackButton?(a(),e("button",{key:0,class:"back-button-wrapper",onClick:l},[u(k(m),{class:"back-button"})])):n("",!0)]),c("div",b,[t.title?(a(),e("h1",B,f(t.title),1)):n("",!0)]),s[0]||(s[0]=c("div",{class:"right"},null,-1))]))}},C=d(y,[["__scopeId","data-v-61c632df"]]);export{C as H};
