import Vue from 'vue'
import Vuex, { createLogger } from 'vuex'
import catalogs from './modules/catalogs'

Vue.use(Vuex)

const debug = process.env.NODE_ENV !== 'production'

export default new Vuex.Store({
    modules: {
        catalogs
    },
    strict: debug,
    plugins: debug ? [createLogger()] : []
})