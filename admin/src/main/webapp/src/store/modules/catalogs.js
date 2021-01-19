import _ from 'lodash'
import { CatalogAPI } from "../../api";

const state = () => ({
    initialized: false,
    stores: [],
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
        if (itemType === 'stores') {
            items = state.stores
        } else if (itemType === 'categories') {
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
    },

    getPresentation : (state) => (itemType, itemId, locale) => {

        let find = state[itemType].find(i => i.id === itemId);
        if (find && find.availableLocales && find.availableLocales[locale]) {
            return find.availableLocales[locale]
        } else return null
    }
}

const actions = {

    init({ state, commit }, role) {
        if (!state.initialized) {
            CatalogAPI.loadAllCatalog(role)
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

    getManagedItems ({ commit }, itemType) {
        CatalogAPI.getManagedItem(itemType)
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

    attachPresentation({commit}, {itemType, itemId, locale, presentation}) {

        CatalogAPI.createLocalizedPresentation(itemType, itemId, presentation.locale, presentation)
            .then(response => {
                commit('setLocale', {
                    itemType: itemType,
                    itemId: itemId,
                    locale: locale,
                    presentation: response
                })
            }).catch(error => {
                console.log('error attaching new presentation : ' + JSON.stringify(error))
            })


    },

    getPresentation({ getters, commit }, {itemType, itemId, locale}) {

       let cachedPresentation = getters.getPresentation(itemType, itemId, locale)
       if (cachedPresentation) return;

        CatalogAPI.getPresentation(itemType, itemId, locale)
            .then(presentation => commit('setLocale', {
                itemType: itemType,
                itemId: itemId,
                locale: locale,
                presentation: presentation

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

    async insertCatalogItem({ commit }, {itemType, payload}) {

        try {

            let sku = payload.sku
            let presentation = payload.presentation

            delete payload.sku
            delete payload.presentation
            let item = payload

            if (this.itemType === 'products') {
                if (sku) {
                    let insertedSKU = await CatalogAPI.upsert('skus', sku)
                    commit('addItem', {itemType: 'skus', item: insertedSKU.data})
                    item.childSKUsIds = [insertedSKU.data.id]
                }
            }

            let insertedItem = await CatalogAPI.upsert(itemType, item)
            commit('addItem', {itemType: itemType, item: insertedItem.data})
            let insertedItemId = insertedItem.data.id;

            if (itemType === 'stores' && payload.catalogsIds && payload.catalogsIds.length > 0) {

                await CatalogAPI.attachAssociatedItems(insertedItemId, itemType, "catalogs", payload.catalogsIds)

            } else if (itemType === 'catalogs' && payload.rootCategoriesIds && payload.rootCategoriesIds.length > 0) {

                await CatalogAPI.attachAssociatedItems(insertedItemId, itemType, "categories", payload.rootCategoriesIds)

            } else if (itemType === 'categories') {

                if (payload.childCategoriesIds && payload.childCategoriesIds.length > 0) {
                    await CatalogAPI.attachAssociatedItems(insertedItemId, itemType, 'categories', payload.childCategoriesIds)
                }

                if (payload.childProductsIds && payload.childProductsIds.length > 0) {
                    await CatalogAPI.attachAssociatedItems(insertedItemId, itemType, 'products', payload.childProductsIds)
                }

            } else if (itemType === 'products') {

                if (payload.categoriesIds && payload.categoriesIds.length > 0) {
                    for (const id of payload.categoriesIds) {
                        await CatalogAPI.attachAssociatedItems(id, 'categories', itemType [insertedItemId])
                    }
                }

                if (payload.discountsIds && payload.discountsIds.length > 0) {
                    await CatalogAPI.attachAssociatedItems(insertedItemId, itemType, 'discounts', payload.discountsIds)
                }

            } else if (itemType === 'skus' && payload.discountsIds && payload.discountsIds.length > 0) {
                await CatalogAPI.attachAssociatedItems(insertedItemId, itemType, 'discoounts', payload.discountsIds)
            }

            if (presentation) {
               let insertedPresentation = await CatalogAPI.createLocalizedPresentation(itemType, insertedItemId,
                   "fr", presentation)

                commit('setLocale', {
                    itemType: itemType,
                    itemId: insertedItemId,
                    locale: insertedPresentation.locale,
                    presentation: insertedPresentation
                })
            }

            commit('setAddCatalogStatus', {status: 'successful'})


        } catch (e) {
            console.log('e : ' + JSON.stringify(e))
            commit('setAddCatalogStatus', {status: 'failed', message: 'An error occurred creating ' + itemType})
        }

    },

    setCatalogActionStatus({commit}, status) {
        commit('setAddCatalogStatus', status)
    }
}

const mutations = {

    setCatalog(state, payload) {
        state.stores = payload.stores
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
        let existingItems = state[payload.itemType]
        let existingIndex = existingItems.findIndex(item => item.id === payload.itemId)
        if (existingIndex !== -1) {
            let newLocale = payload.locale
            //newLocale[payload.locale] = payload.presentation

            let item = _.cloneDeep(existingItems[existingIndex])
            item.localizedPresentation = _.union(item.localizedPresentation, [newLocale])

            let newAvailableLocale = {}
            newAvailableLocale[payload.presentation.locale] = payload.presentation;
            item.availableLocales = Object.assign({}, item.availableLocales ? item.availableLocales : {}, newAvailableLocale)
            existingItems[existingIndex] = item
            state[this.itemType] = _.cloneDeep(existingItems)
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