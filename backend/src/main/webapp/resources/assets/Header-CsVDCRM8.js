import{_ as d,f as e,g as a,h as c,l as n,m as u,u as k,t as f}from"./index-Dtr9sYWb.js";import{c as h}from"./createLucideIcon-Da8SgmE1.js";/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const m=h("chevron-left",[["path",{d:"m15 18-6-6 6-6",key:"1wnfg3"}]]),p={class:"header"},b={class:"left"},_={class:"center"},B={key:0},y={__name:"Header",props:{title:{type:String,default:""},showBackButton:{type:Boolean,default:!0},backAction:{type:Function,default:null}},emits:["back"],setup(t,{emit:r}){const o=t,i=r,l=()=>{o.backAction?o.backAction():i("back")};return(v,s)=>(a(),e("header",p,[c("div",b,[t.showBackButton?(a(),e("button",{key:0,class:"back-button-wrapper",onClick:l},[u(k(m),{class:"back-button"})])):n("",!0)]),c("div",_,[t.title?(a(),e("h1",B,f(t.title),1)):n("",!0)]),s[0]||(s[0]=c("div",{class:"right"},null,-1))]))}},C=d(y,[["__scopeId","data-v-2ed0ba83"]]);export{C as H};
