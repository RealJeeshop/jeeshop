import UserService from "../../api/UserService";

const state = () => ({
    loggedIn: UserService.loggedIn(),
    loading: false,
    error: null
})

const getters = {
    isLoggedIn: () => UserService.loggedIn(),
}

const actions = {

    login({commit}, data) {
        commit('setError', null)
        commit('setLoading', true)
        UserService.login(data.email, data.password, loggedIn => {
            commit('setLoading', false)
            if (loggedIn) commit('setLoggedIn', true)
            else commit('setError', "Mauvais login / mot de passe")
        })
    },

    logOut({commit}) {
        UserService.logout(() => {
            commit('setLoggedIn', false)
        })
    }
}

const mutations = {

    setLoading(state, status) {
        state.loading = status
    },

    setLoggedIn(state, status) {
        state.loggedIn = status
    },

    setError(state, message) {
        state.error = message
    }
}

export default {
    namespaced: true,
    state,
    actions,
    getters,
    mutations
}