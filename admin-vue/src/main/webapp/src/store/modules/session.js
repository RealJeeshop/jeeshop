import UserService from "../../api/UserService";

const state = () => ({
    loggedIn: false,
    loading: false,
    error: null
})

const actions = {

    login({commit}, data) {
        console.log('email : ' + JSON.stringify(data.email))
        console.log('pass : ' + JSON.stringify(data.password))
        commit('setError', null)
        commit('setLoading', true)
        UserService.login(data.email, data.password, loggedIn => {
            console.log('loggedIn : ' + JSON.stringify(loggedIn))
            commit('setLoading', false)
            if (loggedIn) commit('setLoggedIn', true)
            else commit('setError', "Mauvais login / mot de passe")
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
    mutations
}