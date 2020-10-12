import Vue from 'vue'
import VueRouter from 'vue-router'
import store from './store'
import vuetify from './plugins/vuetify'

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

import './main.css'
import './styles/inputs.css'
import './styles/buttons.css'

Vue.config.productionTip = false


const router = new VueRouter({
  mode: 'history',
  base: __dirname,
  routes: [
    { path: '/', component: HomePage },
    { path: '/settings', component: Settings, meta: { protected: true} },
    { path: '/help', component: Help},
    { path: '/catalogs', component: Catalogs, meta: { protected: true} },
    { path: '/catalogs/:id', component: Catalogs, meta: { protected: true}},
    { path: '/products', component: Catalogs, meta: { protected: true}},
    { path: '/products/:id', component: Catalogs, meta: { protected: true}},
    { path: '/discounts', component: Catalogs, meta: { protected: true}},
    { path: '/discounts/:id', component: Catalogs, meta: { protected: true}},
    { path: '/categories', component: Catalogs, meta: { protected: true}},
    { path: '/categories/:id', component: Catalogs, meta: { protected: true}},
    { path: '/skus', component: Catalogs, meta: { protected: true}},
    { path: '/skus/:id', component: Catalogs, meta: { protected: true}},
    { path: '/orders', component: Orders, meta: { protected: true}},
    { path: '/stats', component: Stats, meta: { protected: true}},
    { path: '/users', component: Users, meta: { protected: true}},
    { path: '/emails', component: Emails, meta: { protected: true}},
  ]
})

router.beforeEach((to, from, next) => {
  if(to.matched.some(record => record.meta.protected)) {
    if (store.getters["session/isLoggedIn"]) {
      next()
      return
    }
    next('/')
  } else {
    next()
  }
})

new Vue({
  el: '#app',
  router,
  store,
  vuetify,
  render: h => h(App)
})
