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

    catalogs: (state, getters, rootState) => {
        return state.items.map(() => {
            return rootState.catalogs
        })
    },

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

        console.log('itemType : ' + JSON.stringify(itemType))
        console.log('item : ' + JSON.stringify(item))

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