import{R as O}from"./vaadin-CrJv2mxs.js";import{u as I,f as x,w as V,s as R,x as D}from"./vendors-1xcxepPC.js";(function(){const e=document.createElement("link").relList;if(e&&e.supports&&e.supports("modulepreload"))return;for(const a of document.querySelectorAll('link[rel="modulepreload"]'))i(a);new MutationObserver(a=>{for(const n of a)if(n.type==="childList")for(const r of n.addedNodes)r.tagName==="LINK"&&r.rel==="modulepreload"&&i(r)}).observe(document,{childList:!0,subtree:!0});function t(a){const n={};return a.integrity&&(n.integrity=a.integrity),a.referrerPolicy&&(n.referrerPolicy=a.referrerPolicy),a.crossOrigin==="use-credentials"?n.credentials="include":a.crossOrigin==="anonymous"?n.credentials="omit":n.credentials="same-origin",n}function i(a){if(a.ep)return;a.ep=!0;const n=t(a);fetch(a.href,n)}})();window.Vaadin=window.Vaadin||{};window.Vaadin.featureFlags=window.Vaadin.featureFlags||{};window.Vaadin.featureFlags.exampleFeatureFlag=!1;window.Vaadin.featureFlags.collaborationEngineBackend=!1;window.Vaadin.featureFlags.webPush=!1;window.Vaadin.featureFlags.formFillerAddon=!1;window.Vaadin.featureFlags.reactRouter=!1;const F="modulepreload",_=function(o,e){return new URL(o,e).href},b={},E=function(e,t,i){let a=Promise.resolve();if(t&&t.length>0){const n=document.getElementsByTagName("link");a=Promise.all(t.map(r=>{if(r=_(r,i),r in b)return;b[r]=!0;const c=r.endsWith(".css"),g=c?'[rel="stylesheet"]':"";if(!!i)for(let m=n.length-1;m>=0;m--){const y=n[m];if(y.href===r&&(!c||y.rel==="stylesheet"))return}else if(document.querySelector(`link[href="${r}"]${g}`))return;const f=document.createElement("link");if(f.rel=c?"stylesheet":F,c||(f.as="script",f.crossOrigin=""),f.href=r,document.head.appendChild(f),c)return new Promise((m,y)=>{f.addEventListener("load",m),f.addEventListener("error",()=>y(new Error(`Unable to preload CSS for ${r}`)))})}))}return a.then(()=>e()).catch(n=>{const r=new Event("vite:preloadError",{cancelable:!0});if(r.payload=n,window.dispatchEvent(r),!r.defaultPrevented)throw n})};var C,d;(function(o){o.CONNECTED="connected",o.LOADING="loading",o.RECONNECTING="reconnecting",o.CONNECTION_LOST="connection-lost"})(d||(d={}));class P{constructor(e){this.stateChangeListeners=new Set,this.loadingCount=0,this.connectionState=e,this.serviceWorkerMessageListener=this.serviceWorkerMessageListener.bind(this),navigator.serviceWorker&&(navigator.serviceWorker.addEventListener("message",this.serviceWorkerMessageListener),navigator.serviceWorker.ready.then(t=>{var i;(i=t.active)===null||i===void 0||i.postMessage({method:"Vaadin.ServiceWorker.isConnectionLost",id:"Vaadin.ServiceWorker.isConnectionLost"})}))}addStateChangeListener(e){this.stateChangeListeners.add(e)}removeStateChangeListener(e){this.stateChangeListeners.delete(e)}loadingStarted(){this.state=d.LOADING,this.loadingCount+=1}loadingFinished(){this.decreaseLoadingCount(d.CONNECTED)}loadingFailed(){this.decreaseLoadingCount(d.CONNECTION_LOST)}decreaseLoadingCount(e){this.loadingCount>0&&(this.loadingCount-=1,this.loadingCount===0&&(this.state=e))}get state(){return this.connectionState}set state(e){if(e!==this.connectionState){const t=this.connectionState;this.connectionState=e,this.loadingCount=0;for(const i of this.stateChangeListeners)i(t,this.connectionState)}}get online(){return this.connectionState===d.CONNECTED||this.connectionState===d.LOADING}get offline(){return!this.online}serviceWorkerMessageListener(e){typeof e.data=="object"&&e.data.id==="Vaadin.ServiceWorker.isConnectionLost"&&(e.data.result===!0&&(this.state=d.CONNECTION_LOST),navigator.serviceWorker.removeEventListener("message",this.serviceWorkerMessageListener))}}const A=o=>!!(o==="localhost"||o==="[::1]"||/^127\.\d+\.\d+\.\d+$/u.exec(o)),w=window;if(!(!((C=w.Vaadin)===null||C===void 0)&&C.connectionState)){let o;A(window.location.hostname)?o=!0:o=navigator.onLine,w.Vaadin||(w.Vaadin={}),w.Vaadin.connectionState=new P(o?d.CONNECTED:d.CONNECTION_LOST)}function h(o,e,t,i){var a=arguments.length,n=a<3?e:i===null?i=Object.getOwnPropertyDescriptor(e,t):i,r;if(typeof Reflect=="object"&&typeof Reflect.decorate=="function")n=Reflect.decorate(o,e,t,i);else for(var c=o.length-1;c>=0;c--)(r=o[c])&&(n=(a<3?r(n):a>3?r(e,t,n):r(e,t))||n);return a>3&&n&&Object.defineProperty(e,t,n),n}/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const $={attribute:!0,type:String,converter:I,reflect:!1,hasChanged:x},k=(o=$,e,t)=>{const{kind:i,metadata:a}=t;let n=globalThis.litPropertyMetadata.get(a);if(n===void 0&&globalThis.litPropertyMetadata.set(a,n=new Map),n.set(t.name,o),i==="accessor"){const{name:r}=t;return{set(c){const g=e.get.call(this);e.set.call(this,c),this.requestUpdate(r,g,o)},init(c){return c!==void 0&&this.P(r,void 0,o),c}}}if(i==="setter"){const{name:r}=t;return function(c){const g=this[r];e.call(this,c),this.requestUpdate(r,g,o)}}throw Error("Unsupported decorator location: "+i)};function u(o){return(e,t)=>typeof t=="object"?k(o,e,t):((i,a,n)=>{const r=a.hasOwnProperty(n);return a.constructor.createProperty(n,r?{...i,wrapped:!0}:i),r?Object.getOwnPropertyDescriptor(a,n):void 0})(o,e,t)}/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const B={ATTRIBUTE:1,CHILD:2,PROPERTY:3,BOOLEAN_ATTRIBUTE:4,EVENT:5,ELEMENT:6},M=o=>(...e)=>({_$litDirective$:o,values:e});class U{constructor(e){}get _$AU(){return this._$AM._$AU}_$AT(e,t,i){this._$Ct=e,this._$AM=t,this._$Ci=i}_$AS(e,t){return this.update(e,t)}update(e,t){return this.render(...t)}}/**
 * @license
 * Copyright 2018 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const j=M(class extends U{constructor(o){var e;if(super(o),o.type!==B.ATTRIBUTE||o.name!=="class"||((e=o.strings)==null?void 0:e.length)>2)throw Error("`classMap()` can only be used in the `class` attribute and must be the only part in the attribute.")}render(o){return" "+Object.keys(o).filter(e=>o[e]).join(" ")+" "}update(o,[e]){var i,a;if(this.st===void 0){this.st=new Set,o.strings!==void 0&&(this.nt=new Set(o.strings.join(" ").split(/\s/).filter(n=>n!=="")));for(const n in e)e[n]&&!((i=this.nt)!=null&&i.has(n))&&this.st.add(n);return this.render(e)}const t=o.element.classList;for(const n of this.st)n in e||(t.remove(n),this.st.delete(n));for(const n in e){const r=!!e[n];r===this.st.has(n)||(a=this.nt)!=null&&a.has(n)||(r?(t.add(n),this.st.add(n)):(t.remove(n),this.st.delete(n)))}return V}}),T="css-loading-indicator";var p;(function(o){o.IDLE="",o.FIRST="first",o.SECOND="second",o.THIRD="third"})(p||(p={}));class l extends R{static create(){var e,t;const i=window;return!((e=i.Vaadin)===null||e===void 0)&&e.connectionIndicator||(i.Vaadin||(i.Vaadin={}),i.Vaadin.connectionIndicator=document.createElement("vaadin-connection-indicator"),document.body.appendChild(i.Vaadin.connectionIndicator)),(t=i.Vaadin)===null||t===void 0?void 0:t.connectionIndicator}constructor(){super(),this.firstDelay=450,this.secondDelay=1500,this.thirdDelay=5e3,this.expandedDuration=2e3,this.onlineText="Online",this.offlineText="Connection lost",this.reconnectingText="Connection lost, trying to reconnect...",this.offline=!1,this.reconnecting=!1,this.expanded=!1,this.loading=!1,this.loadingBarState=p.IDLE,this.applyDefaultThemeState=!0,this.firstTimeout=0,this.secondTimeout=0,this.thirdTimeout=0,this.expandedTimeout=0,this.lastMessageState=d.CONNECTED,this.connectionStateListener=()=>{this.expanded=this.updateConnectionState(),this.expandedTimeout=this.timeoutFor(this.expandedTimeout,this.expanded,()=>{this.expanded=!1},this.expandedDuration)}}render(){return D`
      <div class="v-loading-indicator ${this.loadingBarState}" style=${this.getLoadingBarStyle()}></div>

      <div
        class="v-status-message ${j({active:this.reconnecting})}"
      >
        <span class="text"> ${this.renderMessage()} </span>
      </div>
    `}connectedCallback(){var e;super.connectedCallback();const t=window;!((e=t.Vaadin)===null||e===void 0)&&e.connectionState&&(this.connectionStateStore=t.Vaadin.connectionState,this.connectionStateStore.addStateChangeListener(this.connectionStateListener),this.updateConnectionState()),this.updateTheme()}disconnectedCallback(){super.disconnectedCallback(),this.connectionStateStore&&this.connectionStateStore.removeStateChangeListener(this.connectionStateListener),this.updateTheme()}get applyDefaultTheme(){return this.applyDefaultThemeState}set applyDefaultTheme(e){e!==this.applyDefaultThemeState&&(this.applyDefaultThemeState=e,this.updateTheme())}createRenderRoot(){return this}updateConnectionState(){var e;const t=(e=this.connectionStateStore)===null||e===void 0?void 0:e.state;return this.offline=t===d.CONNECTION_LOST,this.reconnecting=t===d.RECONNECTING,this.updateLoading(t===d.LOADING),this.loading?!1:t!==this.lastMessageState?(this.lastMessageState=t,!0):!1}updateLoading(e){this.loading=e,this.loadingBarState=p.IDLE,this.firstTimeout=this.timeoutFor(this.firstTimeout,e,()=>{this.loadingBarState=p.FIRST},this.firstDelay),this.secondTimeout=this.timeoutFor(this.secondTimeout,e,()=>{this.loadingBarState=p.SECOND},this.secondDelay),this.thirdTimeout=this.timeoutFor(this.thirdTimeout,e,()=>{this.loadingBarState=p.THIRD},this.thirdDelay)}renderMessage(){return this.reconnecting?this.reconnectingText:this.offline?this.offlineText:this.onlineText}updateTheme(){if(this.applyDefaultThemeState&&this.isConnected){if(!document.getElementById(T)){const e=document.createElement("style");e.id=T,e.textContent=this.getDefaultStyle(),document.head.appendChild(e)}}else{const e=document.getElementById(T);e&&document.head.removeChild(e)}}getDefaultStyle(){return`
      @keyframes v-progress-start {
        0% {
          width: 0%;
        }
        100% {
          width: 50%;
        }
      }
      @keyframes v-progress-delay {
        0% {
          width: 50%;
        }
        100% {
          width: 90%;
        }
      }
      @keyframes v-progress-wait {
        0% {
          width: 90%;
          height: 4px;
        }
        3% {
          width: 91%;
          height: 7px;
        }
        100% {
          width: 96%;
          height: 7px;
        }
      }
      @keyframes v-progress-wait-pulse {
        0% {
          opacity: 1;
        }
        50% {
          opacity: 0.1;
        }
        100% {
          opacity: 1;
        }
      }
      .v-loading-indicator,
      .v-status-message {
        position: fixed;
        z-index: 251;
        left: 0;
        right: auto;
        top: 0;
        background-color: var(--lumo-primary-color, var(--material-primary-color, blue));
        transition: none;
      }
      .v-loading-indicator {
        width: 50%;
        height: 4px;
        opacity: 1;
        pointer-events: none;
        animation: v-progress-start 1000ms 200ms both;
      }
      .v-loading-indicator[style*='none'] {
        display: block !important;
        width: 100%;
        opacity: 0;
        animation: none;
        transition: opacity 500ms 300ms, width 300ms;
      }
      .v-loading-indicator.second {
        width: 90%;
        animation: v-progress-delay 3.8s forwards;
      }
      .v-loading-indicator.third {
        width: 96%;
        animation: v-progress-wait 5s forwards, v-progress-wait-pulse 1s 4s infinite backwards;
      }

      vaadin-connection-indicator[offline] .v-loading-indicator,
      vaadin-connection-indicator[reconnecting] .v-loading-indicator {
        display: none;
      }

      .v-status-message {
        opacity: 0;
        width: 100%;
        max-height: var(--status-height-collapsed, 8px);
        overflow: hidden;
        background-color: var(--status-bg-color-online, var(--lumo-primary-color, var(--material-primary-color, blue)));
        color: var(
          --status-text-color-online,
          var(--lumo-primary-contrast-color, var(--material-primary-contrast-color, #fff))
        );
        font-size: 0.75rem;
        font-weight: 600;
        line-height: 1;
        transition: all 0.5s;
        padding: 0 0.5em;
      }

      vaadin-connection-indicator[offline] .v-status-message,
      vaadin-connection-indicator[reconnecting] .v-status-message {
        opacity: 1;
        background-color: var(--status-bg-color-offline, var(--lumo-shade, #333));
        color: var(
          --status-text-color-offline,
          var(--lumo-primary-contrast-color, var(--material-primary-contrast-color, #fff))
        );
        background-image: repeating-linear-gradient(
          45deg,
          rgba(255, 255, 255, 0),
          rgba(255, 255, 255, 0) 10px,
          rgba(255, 255, 255, 0.1) 10px,
          rgba(255, 255, 255, 0.1) 20px
        );
      }

      vaadin-connection-indicator[reconnecting] .v-status-message {
        animation: show-reconnecting-status 2s;
      }

      vaadin-connection-indicator[offline] .v-status-message:hover,
      vaadin-connection-indicator[reconnecting] .v-status-message:hover,
      vaadin-connection-indicator[expanded] .v-status-message {
        max-height: var(--status-height, 1.75rem);
      }

      vaadin-connection-indicator[expanded] .v-status-message {
        opacity: 1;
      }

      .v-status-message span {
        display: flex;
        align-items: center;
        justify-content: center;
        height: var(--status-height, 1.75rem);
      }

      vaadin-connection-indicator[reconnecting] .v-status-message span::before {
        content: '';
        width: 1em;
        height: 1em;
        border-top: 2px solid
          var(--status-spinner-color, var(--lumo-primary-color, var(--material-primary-color, blue)));
        border-left: 2px solid
          var(--status-spinner-color, var(--lumo-primary-color, var(--material-primary-color, blue)));
        border-right: 2px solid transparent;
        border-bottom: 2px solid transparent;
        border-radius: 50%;
        box-sizing: border-box;
        animation: v-spin 0.4s linear infinite;
        margin: 0 0.5em;
      }

      @keyframes v-spin {
        100% {
          transform: rotate(360deg);
        }
      }
    `}getLoadingBarStyle(){switch(this.loadingBarState){case p.IDLE:return"display: none";case p.FIRST:case p.SECOND:case p.THIRD:return"display: block";default:return""}}timeoutFor(e,t,i,a){return e!==0&&window.clearTimeout(e),t?window.setTimeout(i,a):0}static get instance(){return l.create()}}h([u({type:Number})],l.prototype,"firstDelay",void 0);h([u({type:Number})],l.prototype,"secondDelay",void 0);h([u({type:Number})],l.prototype,"thirdDelay",void 0);h([u({type:Number})],l.prototype,"expandedDuration",void 0);h([u({type:String})],l.prototype,"onlineText",void 0);h([u({type:String})],l.prototype,"offlineText",void 0);h([u({type:String})],l.prototype,"reconnectingText",void 0);h([u({type:Boolean,reflect:!0})],l.prototype,"offline",void 0);h([u({type:Boolean,reflect:!0})],l.prototype,"reconnecting",void 0);h([u({type:Boolean,reflect:!0})],l.prototype,"expanded",void 0);h([u({type:Boolean,reflect:!0})],l.prototype,"loading",void 0);h([u({type:String})],l.prototype,"loadingBarState",void 0);h([u({type:Boolean})],l.prototype,"applyDefaultTheme",null);customElements.get("vaadin-connection-indicator")===void 0&&customElements.define("vaadin-connection-indicator",l);l.instance;var L;const S=window;S.Vaadin||(S.Vaadin={});(L=S.Vaadin).registrations||(L.registrations=[]);S.Vaadin.registrations.push({is:"@vaadin/common-frontend",version:"0.0.18"});class N extends Error{}const v=window.document.body,s=window;class W{constructor(e){this.response=void 0,this.pathname="",this.isActive=!1,this.baseRegex=/^\//,this.navigation="",v.$=v.$||[],this.config=e||{},s.Vaadin=s.Vaadin||{},s.Vaadin.Flow=s.Vaadin.Flow||{},s.Vaadin.Flow.clients={TypeScript:{isActive:()=>this.isActive}};const t=document.head.querySelector("base");this.baseRegex=new RegExp(`^${(document.baseURI||t&&t.href||"/").replace(/^https?:\/\/[^/]+/i,"")}`),this.appShellTitle=document.title,this.addConnectionIndicator()}get serverSideRoutes(){return[{path:"(.*)",action:this.action}]}loadingStarted(){this.isActive=!0,s.Vaadin.connectionState.loadingStarted()}loadingFinished(){this.isActive=!1,s.Vaadin.connectionState.loadingFinished(),!s.Vaadin.listener&&(s.Vaadin.listener={},document.addEventListener("click",e=>{e.target&&(e.target.hasAttribute("router-link")?this.navigation="link":e.composedPath().some(t=>t.nodeName==="A")&&(this.navigation="client"))},{capture:!0}))}get action(){return async e=>{if(this.pathname=e.pathname,s.Vaadin.connectionState.online)try{await this.flowInit()}catch(t){if(t instanceof N)return s.Vaadin.connectionState.state=d.CONNECTION_LOST,this.offlineStubAction();throw t}else return this.offlineStubAction();return this.container.onBeforeEnter=(t,i)=>this.flowNavigate(t,i),this.container.onBeforeLeave=(t,i)=>this.flowLeave(t,i),this.container}}async flowLeave(e,t){const{connectionState:i}=s.Vaadin;return this.pathname===e.pathname||!this.isFlowClientLoaded()||i.offline?Promise.resolve({}):new Promise(a=>{this.loadingStarted(),this.container.serverConnected=n=>{a(t&&n?t.prevent():{}),this.loadingFinished()},v.$server.leaveNavigation(this.getFlowRoutePath(e),this.getFlowRouteQuery(e))})}async flowNavigate(e,t){return this.response?new Promise(i=>{this.loadingStarted(),this.container.serverConnected=(a,n)=>{t&&a?i(t.prevent()):t&&t.redirect&&n?i(t.redirect(n.pathname)):(this.container.style.display="",i(this.container)),this.loadingFinished()},this.container.serverPaused=()=>{this.loadingFinished()},v.$server.connectClient(this.getFlowRoutePath(e),this.getFlowRouteQuery(e),this.appShellTitle,history.state,this.navigation),this.navigation="history"}):Promise.resolve(this.container)}getFlowRoutePath(e){return decodeURIComponent(e.pathname).replace(this.baseRegex,"")}getFlowRouteQuery(e){return e.search&&e.search.substring(1)||""}async flowInit(){if(!this.isFlowClientLoaded()){this.loadingStarted(),this.response=await this.flowInitUi();const{pushScript:e,appConfig:t}=this.response;typeof e=="string"&&await this.loadScript(e);const{appId:i}=t;await(await E(()=>import("./FlowBootstrap-CHUuW4WK.js"),__vite__mapDeps([]),import.meta.url)).init(this.response),typeof this.config.imports=="function"&&(this.injectAppIdScript(i),await this.config.imports());const n=`flow-container-${i.toLowerCase()}`,r=document.querySelector(n);r?this.container=r:(this.container=document.createElement(n),this.container.id=i),v.$[i]=this.container;const c=await E(()=>import("./FlowClient-BZ2ixoyw.js"),__vite__mapDeps([]),import.meta.url);await this.flowInitClient(c),this.loadingFinished()}return this.container&&!this.container.isConnected&&(this.container.style.display="none",document.body.appendChild(this.container)),this.response}async loadScript(e){return new Promise((t,i)=>{const a=document.createElement("script");a.onload=()=>t(),a.onerror=i,a.src=e,document.body.appendChild(a)})}injectAppIdScript(e){const t=e.substring(0,e.lastIndexOf("-")),i=document.createElement("script");i.type="module",i.setAttribute("data-app-id",t),document.body.append(i)}async flowInitClient(e){return e.init(),new Promise(t=>{const i=setInterval(()=>{Object.keys(s.Vaadin.Flow.clients).filter(n=>n!=="TypeScript").reduce((n,r)=>n||s.Vaadin.Flow.clients[r].isActive(),!1)||(clearInterval(i),t())},5)})}async flowInitUi(){const e=s.Vaadin&&s.Vaadin.TypeScript&&s.Vaadin.TypeScript.initial;return e?(s.Vaadin.TypeScript.initial=void 0,Promise.resolve(e)):new Promise((t,i)=>{const n=new XMLHttpRequest,r=`?v-r=init&location=${encodeURIComponent(this.getFlowRoutePath(location))}&query=${encodeURIComponent(this.getFlowRouteQuery(location))}`;n.open("GET",r),n.onerror=()=>i(new N(`Invalid server response when initializing Flow UI.
        ${n.status}
        ${n.responseText}`)),n.onload=()=>{const c=n.getResponseHeader("content-type");c&&c.indexOf("application/json")!==-1?t(JSON.parse(n.responseText)):n.onerror()},n.send()})}addConnectionIndicator(){l.create(),s.addEventListener("online",()=>{if(!this.isFlowClientLoaded()){s.Vaadin.connectionState.state=d.RECONNECTING;const e=new XMLHttpRequest;e.open("HEAD","sw.js"),e.onload=()=>{s.Vaadin.connectionState.state=d.CONNECTED},e.onerror=()=>{s.Vaadin.connectionState.state=d.CONNECTION_LOST},setTimeout(()=>e.send(),50)}}),s.addEventListener("offline",()=>{this.isFlowClientLoaded()||(s.Vaadin.connectionState.state=d.CONNECTION_LOST)})}async offlineStubAction(){const e=document.createElement("iframe");e.setAttribute("src","./offline-stub.html"),e.setAttribute("style","width: 100%; height: 100%; border: 0"),this.response=void 0;let i;const a=()=>{i!==void 0&&(s.Vaadin.connectionState.removeStateChangeListener(i),i=void 0)};return e.onBeforeEnter=(n,r,c)=>{i=()=>{s.Vaadin.connectionState.online&&(a(),c.render(n,!1))},s.Vaadin.connectionState.addStateChangeListener(i)},e.onBeforeLeave=(n,r,c)=>{a()},e}isFlowClientLoaded(){return this.response!==void 0}}const{serverSideRoutes:q}=new W({imports:()=>E(()=>import("./generated-flow-imports-lkKRifEF.js"),__vite__mapDeps([0,1,2]),import.meta.url)}),H=[...q],G=new O(document.querySelector("#outlet"));G.setRoutes(H);export{M as e,U as i,B as t};
function __vite__mapDeps(indexes) {
  if (!__vite__mapDeps.viteFileDeps) {
    __vite__mapDeps.viteFileDeps = ["./generated-flow-imports-lkKRifEF.js","./vendors-1xcxepPC.js","./vaadin-CrJv2mxs.js"]
  }
  return indexes.map((i) => __vite__mapDeps.viteFileDeps[i])
}
