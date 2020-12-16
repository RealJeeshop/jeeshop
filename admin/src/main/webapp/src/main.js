import Vue from 'vue'
import VueRouter from 'vue-router'
import store from './store'
import vuetify from './plugins/vuetify'
import VueI18n from "vue-i18n";

Vue.use(VueRouter)
Vue.use(VueI18n)

import App from './App.vue'
import Settings from './pages/Settings.vue'
import Help from './pages/Help.vue'
import HomePage from "./pages/HomePage";
import Catalogs from "./pages/Catalogs";

import Stats from "./pages/Stats";
import Users from "./pages/Users";
import Emails from "./pages/Emails";
import EmailEdit from './pages/EmailEdit';
import EmailOperations from './pages/EmailOperations';
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

// Ready translated locale messages
const messages = {
  en: {
    login: {
      title: 'Please login'
    }
  },
  fr: {
    message: {
      hello: 'Veuillez vous authentifier'
    }
  }
}

// Create VueI18n instance with options
const i18n = new VueI18n({
  locale: 'en',
  messages,
})


const router = new VueRouter({
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
    { path: '/emails', component: Emails, meta: { protected: true},
      children: [
        {
          path: 'operations',
          components: {
            default: EmailOperations
          }
        },
        {
          path: 'create',
          components: {
            default: EmailEdit
          }
        },
        {
          path: ':id',
          components: {
            default: EmailEdit
          }
        }
      ]
    },
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
  i18n,
  render: h => h(App)
})
