import Vue from 'vue'
import VueRouter from 'vue-router'
import store from './store'
import './main.css'
import './lib/css/inputs.css'
import './lib/css/buttons.css'

Vue.use(VueRouter)

import App from './App.vue'
import Settings from './components/Settings.vue'
import Help from './components/Help.vue'
import HomePage from "./components/HomePage";
import Catalogs from "./components/Catalogs";
import Stats from "./components/Stats";
import Users from "./components/Users";
import Emails from "./components/Emails";
import Orders from "./components/Orders";

Vue.config.productionTip = false

const router = new VueRouter({
  mode: 'history',
  base: __dirname,
  routes: [
    { path: '/', component: HomePage },
    { path: '/settings', component: Settings },
    { path: '/help', component: Help },
    { path: '/catalogs', component: Catalogs },
    { path: '/products', component: Catalogs },
    { path: '/discounts', component: Catalogs },
    { path: '/categories', component: Catalogs },
    { path: '/skus', component: Catalogs },
    { path: '/orders', component: Orders },
    { path: '/stats', component: Stats },
    { path: '/users', component: Users },
    { path: '/emails', component: Emails },
  ]
})

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
