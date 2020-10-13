import Vue from 'vue'
import VueRouter from 'vue-router'
import store from './store'
import vuetify from './plugins/vuetify'

Vue.use(VueRouter)

import App from './App.vue'
import Settings from './pages/Settings.vue'
import Help from './pages/Help.vue'
import HomePage from "./pages/HomePage";
import Catalogs from "./pages/Catalogs";

import Stats from "./pages/Stats";
import Users from "./pages/Users";
import Emails from "./pages/Emails";
import Orders from "./pages/Orders";

import './main.css'
import './styles/inputs.css'
import './styles/buttons.css'
import './styles/forms.scss'
import './styles/flex.scss'

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
    { path: '/catalogs/create', component: Catalogs, meta: { protected: true}},
    { path: '/products', component: Catalogs, meta: { protected: true}},
    { path: '/products/:id', component: Catalogs, meta: { protected: true}},
    { path: '/products/create', component: Catalogs, meta: { protected: true}},
    { path: '/discounts', component: Catalogs, meta: { protected: true}},
    { path: '/discounts/:id', component: Catalogs, meta: { protected: true}},
    { path: '/discounts/create', component: Catalogs, meta: { protected: true}},
    { path: '/categories', component: Catalogs, meta: { protected: true}},
    { path: '/categories/:id', component: Catalogs, meta: { protected: true}},
    { path: '/categories/create', component: Catalogs, meta: { protected: true}},
    { path: '/skus', component: Catalogs, meta: { protected: true}},
    { path: '/skus/:id', component: Catalogs, meta: { protected: true}},
    { path: '/skus/create', component: Catalogs, meta: { protected: true}},
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
