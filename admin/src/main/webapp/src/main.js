import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import store from './store'
import vuetify from './plugins/vuetify'
import { createI18n } from 'vue-i18n'

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
import CreateProduct from "./pages/CreateProduct";
import StoreAdmin from "./pages/StoreAdmin";
import StoreAdminSettings from "./pages/StoreAdminSettings";
import LegacyFlex from './components/LegacyFlex'

import EnglishTrad from './assets/english'
import FrenchTrad from './assets/french'
import LoginPage from "@/pages/LoginPage";
import StoreAdminInfo from "@/pages/StoreAdminInfo";
const messages = {
  en: EnglishTrad,
  fr: FrenchTrad
}

// Create VueI18n instance with options
const i18n = createI18n({
  legacy: false,
  globalInjection: true,
  locale: 'en',
  messages,
})


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: HomePage, meta: { protected: true } },
    { path: '/login', component: LoginPage },
    { path: '/settings', component: Settings, meta: { protected: true } },
    { path: '/help', component: Help},
    { path: '/store', component: StoreAdmin, meta: { protected: true },
    children: [
      {
        path: 'info',
        components: {
          default: StoreAdminInfo
        }
      },
      {
        path: 'settings',
        components: {
          default: StoreAdminSettings
        }
      },
    ]},
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
          path: 'create',
          components: {
            edit: CreateProduct
          }
        },
        {
          path: ':id',
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
    next('/login')
  } else {
    next()
  }
})

const app = createApp(App)
app.component('v-flex', LegacyFlex)
app.use(router)
app.use(store)
app.use(vuetify)
app.use(i18n)
app.mount('#app')
