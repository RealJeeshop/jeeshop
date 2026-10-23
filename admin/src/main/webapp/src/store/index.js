import { createLogger, createStore } from 'vuex'
import catalogs from './modules/catalogs'
import orders from './modules/orders'
import users from './modules/users'
import mails from './modules/mails'
import session from './modules/session'
import createPersistedState from "vuex-persistedstate";

const debug = process.env.NODE_ENV !== 'production'

export default createStore({
    modules: {
        catalogs,
        session,
        orders,
        mails,
        users
    },
    strict: debug,
    plugins: debug ? [createLogger(), createPersistedState()] : []
})