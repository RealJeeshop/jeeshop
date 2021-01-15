import UserService from "../../api/UserService";

const state = () => ({
    loggedIn: UserService.loggedIn(),
    user: null,
    loading: false,
    error: null
})

const getters = {
    isLoggedIn: () => UserService.loggedIn(),
}

const actions = {

    login({commit}, data) {
        commit('setLoading', true)
        UserService.login(data.email, data.password).then(user => {
            commit('setLoading', false)
            if (user) commit('login', user)
            else {
                commit('logout')
                commit('setError', "Mauvais login / mot de passe")
            }

        }).catch(e => console.log('e : ' + JSON.stringify(e))  )
    },

    logOut({commit}) {
        UserService.logout(() => {
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