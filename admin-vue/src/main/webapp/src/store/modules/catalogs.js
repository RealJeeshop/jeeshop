import CatalogService from '../../api/CatalogService'

const state = () => ({
    catalogs: [],
    products: [],
    categories: [],
    skus: [],
    discounts: [],
    addCatalogStatus: null
})

const getters = {

    getById: (state) => (id, itemType) => {
        console.log("Loading item ...")
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

    upsert ({ commit, state }, { itemType, item }) {
        const existingItems = [...state[itemType]]
        commit('setAddCatalogStatus', null)
        CatalogService.add(itemType, item,
            (upsertedItem) => {
                commit('addItem', {itemType, item: upsertedItem})
                commit('setAddCatalogStatus', 'successful')
            },
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

    addItem (state, payload) {
        let existingId = state[payload.itemType].findIndex(item => item.id === payload.item.id)

        if (existingId === -1) {
            state[payload.itemType].push(payload.item)
        } else {
            state[payload.itemType][existingId] = payload.item
        }
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