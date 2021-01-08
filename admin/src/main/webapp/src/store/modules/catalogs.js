import _ from 'lodash'
import { CatalogAPI } from "../../api";

const state = () => ({
    initialized: false,
    catalogs: [],
    products: [],
    categories: [],
    skus: [],
    discounts: [],
    addCatalogStatus: null,
    error: null
})

const getters = {

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

    getPresentation({ commit }, {itemType, itemId, locale}) {
        CatalogAPI.getPresentation(itemType, itemId, locale)
            .then(response => commit('setLocale', {
                itemType: itemType,
                itemId: itemId,
                locale: locale,
                presentation: response

            })).catch(error => console.log('error : ' + JSON.stringify(error)));
    },


    upsert ({ commit, state }, { itemType, item }) {
        const existingItems = [...state[itemType]]
        commit('setAddCatalogStatus', null)
        CatalogAPI.upsert(itemType, item)
            .then((response) => {
                commit('addItem', {itemType, item: response.data})
                commit('setAddCatalogStatus', {status: 'successful'})
            })
            .catch(() => {
                commit('setAddCatalogStatus', {status: 'failed', message: 'An error occurred saving catalog item'})
                commit('setItems', {itemType, items: existingItems})
            })
    },

    async insertProductWithSku({ commit }, {product, sku, presentation, rootCategoriesIds, discountsIds}) {

        try {

            if (sku) {
                let insertedSKU = await CatalogAPI.upsert('skus', sku)
                commit('addItem', {itemType: 'skus', item: insertedSKU.data})
                product.childSKUsIds = [insertedSKU.data.id]
            }

            let insertedProduct = await CatalogAPI.upsert('products', product)
            commit('addItem', {itemType: 'products', item: insertedProduct.data})
            commit('setAddCatalogStatus', {status: 'successful'})

            if (presentation) {
                await CatalogAPI.createLocalizedPresentation('products',
                    insertedProduct.data.id, "fr_FR", presentation)
            }

            if (rootCategoriesIds) {
                for (const id of rootCategoriesIds) {
                    await CatalogAPI.attachProductToCategory(id, [insertedProduct.data.id])
                }
            }

            if (discountsIds) {

                console.log('discounts : ' + JSON.stringify(discountsIds))
            }

        } catch (e) {
            console.log('e : ' + JSON.stringify(e))
            commit('setAddCatalogStatus', {status: 'failed', message: 'An error occurred creating product'})
        }
    },

    setCatalogActionStatus({commit}, status) {
        commit('setAddCatalogStatus', status)
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

    setLocale(state, payload) {
        let existingId = state[payload.itemType].findIndex(item => item.id === payload.itemId)
        if (existingId !== -1) {

            let newLocale = {}
            newLocale[payload.locale] = payload.presentation

            let item = _.cloneDeep(state[payload.itemType][existingId])
            item.availableLocales = Object.assign({}, item.availableLocales, newLocale)
            state[payload.itemType][existingId] = _.cloneDeep(item)
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