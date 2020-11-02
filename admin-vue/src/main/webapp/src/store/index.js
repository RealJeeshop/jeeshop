import Vue from 'vue'
import Vuex, { createLogger } from 'vuex'
import catalogs from './modules/catalogs'
import orders from './modules/orders'
import users from './modules/users'
import session from './modules/session'

Vue.use(Vuex)

const debug = process.env.NODE_ENV !== 'production'

export default new Vuex.Store({
    modules: {
        catalogs,
        session,
        orders,
        users
    },
    strict: debug,
    plugins: debug ? [createLogger()] : []
})