import Vue from 'vue'
import Vuex, { createLogger } from 'vuex'
import catalogs from './modules/catalogs'
import session from './modules/session'

Vue.use(Vuex)

const debug = process.env.NODE_ENV !== 'production'

export default new Vuex.Store({
    modules: {
        catalogs,
        session
    },
    strict: debug,
    plugins: debug ? [createLogger()] : []
})