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
import OrderEdit from "./pages/OrderEdit";
import OrderOperations from "./pages/OrderOperations";
import UserEdit from './pages/UserEdit';

import './main.css'
import './styles/inputs.css'
import './styles/buttons.css'
import './styles/forms.scss'
import './styles/flex.scss'
import CatalogEdit from "./pages/CatalogEdit";

Vue.config.productionTip = false


const router = new VueRouter({
  mode: 'history',
  base: __dirname,
  routes: [
    { path: '/', component: HomePage },
    { path: '/settings', component: Settings, meta: { protected: true } },
    { path: '/help', component: Help},
    { path: '/orders', component: Orders, meta: { protected: true },
      children: [
        {
          path: 'operations',
          components: {
            default: OrderOperations
          }
        },
        {
          path: ':id',
          components: {
            default: OrderEdit
          }
        }
      ]
    },
    { path: '/stats', component: Stats, meta: { protected: true}},
    { path: '/users', component: Users, meta: { protected: true},
      children: [
        {
          path: 'create',
          components: {
            default: UserEdit
          }
        },
        {
          path: ':id',
          components: {
            default: UserEdit
          }
        }
      ]
    },
    { path: '/emails', component: Emails, meta: { protected: true}},
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
    }
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
