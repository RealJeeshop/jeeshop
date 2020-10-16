import _ from 'lodash'
import { CatalogAPI } from "../../api";

const state = () => ({
    initialized: false,
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

    init({ state, commit }) {
        if (!state.initialized) {
            CatalogAPI.loadAllCatalog()
                .then(payload => {
                    commit('setCatalog', payload)

                }).catch(error => {
                    console.log('error : ' + JSON.stringify(error))
                })
        }
    },

    getItems ({ commit }, itemType) {
        CatalogAPI.getAll(itemType)
            .then(response => {
                commit('setItems', {itemType: itemType, items: response.data})
            })
            .catch(error => {
                console.log('error : ' + JSON.stringify(error))
            });
    },

    async getItemById({ commit }, {itemType, itemId}) {
        let item = await CatalogAPI.getById(itemType, itemId)
        commit('addItem', {itemType, item})
    },

    getLocales({ commit }, itemType, id) {
        CatalogAPI.getLocales(itemType, id)
            .then(response => {
                commit('setLocales', {itemType: itemType, locales: response.data})
            })
            .catch(error => {
                console.log('error : ' + JSON.stringify(error))
            });
    },


    upsert ({ commit, state }, { itemType, item }) {
        const existingItems = [...state[itemType]]
        commit('setAddCatalogStatus', null)
        CatalogAPI.upsert(itemType, item)
            .then((response) => {
                commit('addItem', {itemType, item: response.data})
                commit('setAddCatalogStatus', 'successful')
            })
            .catch(() => {
                commit('setAddCatalogStatus', 'failed')
                commit('setItems', {itemType, items: existingItems})
            })
    }
}

const mutations = {

    setCatalog(state, payload) {
        state.catalogs = payload.catalogs
        state.categories = payload.categories
        state.products = payload.products
        state.skus = payload.skus
        state.discounts = payload.discounts
        state.initialized = true
    },

    setItems(state, payload) {
        state[payload.itemType] = payload.items
    },

    setLocales(state, payload) {
        let existingId = state[payload.itemType].findIndex(item => item.id === payload.item.id)
        if (existingId !== -1) {

            let item = _.cloneDeep(state[payload.itemType][existingId])
            item.availableLocale = payload.locales
            state[payload.itemType][existingId] = item
        }
    },

    addItem (state, payload) {
        let existingId = state[payload.itemType].findIndex(item => item.id === payload.item.id)

        let clonedState = _.cloneDeep(state[payload.itemType])
        if (existingId === -1) {
            clonedState.push(payload.item)
        } else {
            clonedState.splice(existingId, 1, payload.item)
        }

        state[payload.itemType] = clonedState
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