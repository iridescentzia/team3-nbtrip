import{A as d,_ as u,e,f as a,g as c,j as n,x as k,u as f,t as h}from"./index-DnJhAHt0.js";/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const m=d("chevron-left",[["path",{d:"m15 18-6-6 6-6",key:"1wnfg3"}]]),p={class:"header"},_={class:"left"},b={class:"center"},B={key:0},y={__name:"Header",props:{title:{type:String,default:""},showBackButton:{type:Boolean,default:!0},backAction:{type:Function,default:null}},emits:["back"],setup(t,{emit:r}){const s=t,i=r,l=()=>{s.backAction?s.backAction():i("back")};return(v,o)=>(a(),e("header",p,[c("div",_,[t.showBackButton?(a(),e("button",{key:0,class:"back-button-wrapper",onClick:l},[k(f(m),{class:"back-button"})])):n("",!0)]),c("div",b,[t.title?(a(),e("h1",B,h(t.title),1)):n("",!0)]),o[0]||(o[0]=c("div",{class:"right"},null,-1))]))}},w=u(y,[["__scopeId","data-v-61c632df"]]);export{w as H};
