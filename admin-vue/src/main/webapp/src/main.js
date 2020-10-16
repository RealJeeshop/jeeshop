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
import CatalogEdit from "./components/CatalogEdit";

Vue.config.productionTip = false


const router = new VueRouter({
  mode: 'history',
  base: __dirname,
  routes: [
    { path: '/', component: HomePage },
    { path: '/settings', component: Settings, meta: { protected: true} },
    { path: '/help', component: Help},
    { path: '/:itemType', component: Catalogs, meta: { protected: true},
      children: [
        {
          path: ':id',
          components: {
            edit: CatalogEdit
          }
        },
        {
          path: 'create',
          components: {
            edit: CatalogEdit
          }
        }
      ]
    },
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
