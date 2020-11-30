import UserAPI from "../../api/UserAPI";
import _ from "lodash";

const state = () => ({
    users: []
})

const getters = {

}

const actions = {

    getAll({commit}) {
        UserAPI.getAll()
            .then(response => {
                commit('setUsers', response)
            }).catch(error => {
            console.log('error fetching users: ' + JSON.stringify(error))
        })
    },
    getById({commit}, id) {
        UserAPI.getById(id)
            .then(response => {
                commit('addUser', response)
            }).catch(error => {
            console.log(`error fetching user with id ${id} due to ${JSON.stringify(error)}`)
        })
    }
}

const mutations = {

    setUsers(state, users) {
        state.users = users
    },

    addUser(state, user) {

        let existingId = state.users.findIndex(u => u.id === user.id)
        let clonedState = _.cloneDeep(state.users)
        if (existingId === -1) {
            clonedState.push(user)
        } else {
            clonedState.splice(existingId, 1, user)
        }

        state.users = clonedState
    }
}

export default {
    namespaced: true,
    state,
    actions,
    getters,
    mutations
}