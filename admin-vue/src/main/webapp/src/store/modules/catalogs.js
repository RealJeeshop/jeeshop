import CatalogService from '../../api/CatalogService'

const state = () => ({
    catalogs: [],
    addCatalogStatus: null
})

const getters = {
    catalogs: (state, getters, rootState) => {
        return state.items.map(() => {
            return rootState.catalogs
        })
    },
}

const actions = {
    getCatalogs ({ commit }) {
        CatalogService.getAll(catalogs => {
            console.log('catalogs : ' + JSON.stringify(catalogs))
            commit('setCatalogs', catalogs)
        })
    },
    add ({ commit, state }, catalog) {
        const existingCatalogs = [...state.catalogs]
        commit('setAddCatalogStatus', null)
        commit('addCatalog', catalog)
        CatalogService.add(
            catalog,
            () => commit('setAddCatalogStatus', 'successful'),
            () => {
                commit('setAddCatalogStatus', 'failed')
                commit('setCatalogs', existingCatalogs)
            }
        )
    }
}

const mutations = {

    setCatalogs(state, catalogs) {
        state.catalogs = catalogs
    },

    addCatalog (state, catalog) {
        state.catalogs.push(catalog)
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