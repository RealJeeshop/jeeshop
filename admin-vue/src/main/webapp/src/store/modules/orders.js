import OrderAPI from "../../api/OrderAPI";
import _ from "lodash";

const state = () => ({
    orders: []
})

const getters = {

}

const actions = {

    getAll({commit}) {
        OrderAPI.getAll()
            .then(response => {
                commit('setOrders', response)
            }).catch(error => {
                console.log('error fetching orders: ' + JSON.stringify(error))
            })
    },
    getById({commit}, id) {
        OrderAPI.getById(id)
            .then(response => {
                commit('addOrder', response)
            }).catch(error => {
                console.log(`error fetching order with id ${id} due to ${JSON.stringify(error)}`)
            })
    }
}

const mutations = {

    setOrders(state, orders) {
        state.orders = orders
    },

    addOrder(state, order) {

        let existingId = state.orders.findIndex(o => o.id === order.id)
        let clonedState = _.cloneDeep(state.orders)
        if (existingId === -1) {
            clonedState.push(order)
        } else {
            clonedState.splice(existingId, 1, order)
        }

        state.orders = clonedState
    }
}

export default {
    namespaced: true,
    state,
    actions,
    getters,
    mutations
}