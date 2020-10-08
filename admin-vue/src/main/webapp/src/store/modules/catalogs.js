import CatalogService from '../../api/CatalogService'

const state = () => ({
    itemType: "catalogs",
    catalogs: [],
    products: [],
    categories: [],
    skus: [],
    discounts: [],
    addCatalogStatus: null
})

const getters = {

    catalogs: (state, getters, rootState) => {
        return state.items.map(() => {
            return rootState.catalogs
        })
    },

    getById: (state) => (id, itemType) => {
        let items = state.catalogs
        if (itemType === 'categories') {
            items = state.categories
        } else if (itemType === 'products') {
            items = state.products
        } else if (itemType === 'skus') {
            items = state.skus
        } else if (itemType === 'discounts') {
            items = state.discounts
        }

         return items.find(item => {
             return item.id === parseInt(id)
        })
    }
}

const actions = {

    getItems ({ commit }, itemType) {
        CatalogService.getAll(itemType,items => {
            commit('setItems', {itemType, items})
        })
    },

    setItemType({ commit }, itemType) {
        commit('setItemType', itemType)
    },

    add ({ commit, state }, itemType, item) {

        const existingItems = [...state[itemType]]
        commit('setAddCatalogStatus', null)
        commit('addItem', {itemType, item})
        CatalogService.add(itemType, item,
            () => commit('setAddCatalogStatus', 'successful'),
            () => {
                commit('setAddCatalogStatus', 'failed')
                commit('setItems', {itemType, items: existingItems})
            }
        )
    }
}

const mutations = {

    setItems(state, payload) {
        state[payload.itemType] = payload.items
    },

    setItemType(state, itemType) {
        state.itemType = itemType
    },

    addItem (state, payload) {
        state[payload.itemType].push(payload.item)
    },

    setAddCatalogStatus (state, status) {
        state.addCatalogStatus = status
    }
}

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}