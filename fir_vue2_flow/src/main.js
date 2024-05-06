import Vue from 'vue'
import App from './App.vue'
import router from './router'
import http from "@/api/index";
import store from './store/index';

// 引入element
import ElementUI, {Message} from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';
import {parseTime} from "@/utils/tools";

// 默认点击背景不关闭弹窗
ElementUI.Dialog.props.closeOnClickModal.default = false

Vue.config.productionTip = false
// 全局方法挂载
Vue.prototype.parseTime = parseTime

Vue.prototype.$http = http;
Vue.prototype.$message = Message

// 组件注入
Vue.use(ElementUI);

new Vue({
  store,
  router,
  render: h => h(App),
}).$mount('#app')
