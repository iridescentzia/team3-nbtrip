import{B as u,_ as w,e as i,f as l,g as d,j as k,x as b,u as C,t as g}from"./index-8gf4eXaq.js";/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const f=e=>e.replace(/([a-z0-9])([A-Z])/g,"$1-$2").toLowerCase(),B=e=>e.replace(/^([A-Z])|[\s-_]+(\w)/g,(t,o,s)=>s?s.toUpperCase():o.toLowerCase()),_=e=>{const t=B(e);return t.charAt(0).toUpperCase()+t.slice(1)},v=(...e)=>e.filter((t,o,s)=>!!t&&t.trim()!==""&&s.indexOf(t)===o).join(" ").trim();/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */var c={xmlns:"http://www.w3.org/2000/svg",width:24,height:24,viewBox:"0 0 24 24",fill:"none",stroke:"currentColor","stroke-width":2,"stroke-linecap":"round","stroke-linejoin":"round"};/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const x=({size:e,strokeWidth:t=2,absoluteStrokeWidth:o,color:s,iconNode:n,name:a,class:r,...p},{slots:h})=>u("svg",{...c,width:e||c.width,height:e||c.height,stroke:s||c.stroke,"stroke-width":o?Number(t)*24/Number(e):t,class:v("lucide",...a?[`lucide-${f(_(a))}-icon`,`lucide-${f(a)}`]:["lucide-icon"]),...p},[...n.map(m=>u(...m)),...h.default?[h.default()]:[]]);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const y=(e,t)=>(o,{slots:s})=>u(x,{...o,iconNode:t,name:e},s);/**
 * @license lucide-vue-next v0.525.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const A=y("chevron-left",[["path",{d:"m15 18-6-6 6-6",key:"1wnfg3"}]]),L={class:"header"},$={class:"left"},j={class:"center"},H={key:0},I={__name:"Header",props:{title:{type:String,default:""},showBackButton:{type:Boolean,default:!0},backAction:{type:Function,default:null}},emits:["back"],setup(e,{emit:t}){const o=e,s=t,n=()=>{o.backAction?o.backAction():s("back")};return(a,r)=>(l(),i("header",L,[d("div",$,[e.showBackButton?(l(),i("button",{key:0,class:"back-button-wrapper",onClick:n},[b(C(A),{class:"back-button"})])):k("",!0)]),d("div",j,[e.title?(l(),i("h1",H,g(e.title),1)):k("",!0)]),r[0]||(r[0]=d("div",{class:"right"},null,-1))]))}},V=w(I,[["__scopeId","data-v-2ed0ba83"]]);export{V as H,y as c};
