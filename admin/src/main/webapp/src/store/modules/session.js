import { UserAPI } from "../../api";

const state = () => ({
    loggedIn: UserAPI.loggedIn(),
    user: null,
    loading: false,
    error: null
})

const getters = {
    isLoggedIn: () => UserAPI.loggedIn(),
    isUserInRole: (state) => (role) => {

        let roles = state.user
            ? state.user.roles.map(r => r.name)
            : JSON.parse(localStorage.getItem("payload")).roles

        if (roles)
            return roles.findIndex((r) => r === role) !== -1
        else
            return false
    }
}

const actions = {

    getUserInfo({commit}) {
        UserAPI.getUserInfo().then(response => {
            commit('login', response)
        }).catch(error => console.log('error : ' + JSON.stringify(error))  )
    },

    login({commit}, data) {
        commit('setLoading', true)
        UserAPI.login(data.email, data.password).then(user => {
            commit('setLoading', false)
            if (user) commit('login', user)
            else {
                commit('logout')
                commit('setError', "Mauvais login / mot de passe")
            }

        }).catch(e => {

            if (e.message.indexOf("401") !== -1) {
                commit('setError', "Mauvais login / mot de passe")
            } else {
                commit('setError', "Une erreur est survenue, veuillez réessayer !")
            }

            console.log('e : ' + JSON.stringify(e))
        }  )
    },

    logOut({commit}) {
        UserAPI.logout(() => {
            commit('logout')
        })
    }
}

const mutations = {

    setLoading(state, status) {
       if (status) state.error = null
        state.loading = status
    },

    login(state, user) {
        state.user = user
        state.loggedIn = true
    },

    setError(state, message) {
        state.error = message
    },

    setUser(state, user) {
        state.user = user
    },

    logout(state) {
        state.user = null
        state.loggedIn = false
    }
}

export default {
    namespaced: true,
    state,
    actions,
    getters,
    mutations
}