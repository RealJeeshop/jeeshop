import axios from 'axios'
import _ from 'lodash'

const CatalogAPI = {

    async getAll(itemType) {
        return axios.get(`/rs/${itemType}`)
    },

    async getManagedItem(itemType, storeId) {
        return axios.get(`/rs/${itemType}/managed`)
    },

    async getById(itemType, id) {

        let additionalRequests = prepareRequestByItemType(itemType, id)
        return new Promise((success, die) => {
            axios.all([axios.get(`/rs/${itemType}/${id}`), CatalogAPI.getLocales(itemType, id)].concat(additionalRequests)
            ).then(axios.spread((...responses) => {

                const item = Object.assign(handleResponseByItemType(itemType, responses[0].data, responses),
                    { localizedPresentation: responses[1].data })

                let allPresentations = item.localizedPresentation
                    .map(locale => this.getPresentation(itemType, id, locale));

                axios.all(allPresentations)
                    .then(axios.spread((...responses) => {
                        for(let i=0; i < item.localizedPresentation.length; i++) {
                            let newObject = {}
                            newObject[responses[i].locale] = responses[i]
                            let availableLocales = Object.assign(item.availableLocales ? item.availableLocales : {}, newObject);
                            item.availableLocales = availableLocales
                        }

                        success(item)
                    }))
                    .catch(die)

            })).catch(error => {
                console.log('error : ' + JSON.stringify(error))
                die(error)
            })
        })
    },

    getLocales(itemType, id) {
        return axios.get(`/rs/${itemType}/${id}/presentationslocales`)
    },

    getPresentation(itemType, id, locale) {
        return new Promise((success, die) => {
            axios.get(`/rs/${itemType}/${id}/presentations/${locale}`)
                .then(response => success(response.data))
                .catch(die)
        })
    },

    updatePresentation(itemType, id, locale, presentation) {
        return new Promise((success, die) => {
            axios.put(`/rs/${itemType}/${id}/presentations/${locale}`, presentation)
                .then(response => success(response.data))
                .catch(die)
        })
    },

    getCatalogs(itemType, id) {
      return axios.get(`/rs/${itemType}/${id}/catalogs`)
    },

    getCategories(itemType, id) {
        return axios.get(`/rs/${itemType}/${id}/categories`)
    },

    getDiscounts(itemType, id) {
        return axios.get(`/rs/${itemType}/${id}/discounts`)
    },

    getSKUs(itemType, id) {
        return axios.get(`/rs/${itemType}/${id}/skus`)
    },

    getProducts(itemType, id) {
        return axios.get(`/rs/${itemType}/${id}/products`)
    },

    loadAllCatalog(storeId) {
        console.log('[IMPLEMENT ME] storeId : ' + JSON.stringify(storeId))
        return new Promise((success, die) => {
            axios.all([
                this.getAll('catalogs'),
                this.getAll('categories'),
                this.getAll('products'),
                this.getAll('skus'),
                this.getAll('discounts'),
                this.getAll('stores'),
            ]).then(axios.spread((...responses) => {

                success(Object.assign({}, {
                    catalogs: responses[0].data,
                    categories: responses[1].data,
                    products: responses[2].data,
                    skus: responses[3].data,
                    discounts: responses[4].data,
                    stores: responses[5].data,
                }))

            })).catch(die)
        })
    },

    upsert(itemType, item) {

        delete item.rootCategories
        delete item.discounts
        delete item.childSKUs
        delete item.childProducts
        delete item.childCategories
        delete item.localizedPresentation

        if (item.id) {
            return axios.put(`/rs/${itemType}`, item)
        } else {
            return axios.post(`/rs/${itemType}`, item)
        }
    },

    attachAssociatedItems(itemId, itemType, targetItemType, itemIds) {
        return new Promise((success, die) => {
            axios.put(`/rs/${itemType}/${itemId}/${targetItemType}`, itemIds)
                .then(response => success(response.data))
                .catch(die)
        })
    },

    attachDiscountsToProduct(productId, discountsIds) {
        return new Promise((success, die) => {
            axios.put(`/rs/products/${productId}/discounts`, discountsIds)
                .then(response => success(response.data))
                .catch(die)
        })
    },


    createLocalizedPresentation(itemType, itemId, locale, presentation) {
        return new Promise((success, die) => {
            axios.post(`/rs/${itemType}/${itemId}/presentations/${locale}`, presentation)
                .then(response => success(response.data))
                .catch(die)
        })
    }

}

function prepareRequestByItemType(itemType, id) {

    if (itemType === 'stores') {
        return [
            CatalogAPI.getCatalogs(itemType, id)
        ]
    } else if (itemType === 'catalogs') {
        return [
            CatalogAPI.getCategories(itemType, id)
        ]
    } else if (itemType === 'categories') {
        return [
            CatalogAPI.getCategories(itemType, id),
            CatalogAPI.getProducts(itemType, id)
        ]
    } else if (itemType === 'products') {
        return [
            CatalogAPI.getDiscounts(itemType, id),
            CatalogAPI.getSKUs(itemType, id)
        ]
    } else if (itemType === 'skus') {
        return [
            CatalogAPI.getDiscounts(itemType, id)
        ]
    } else return []
}

function handleResponseByItemType(itemType, item, responses) {

    let newItem = _.cloneDeep(item)
    if (itemType === 'stores') {
        newItem.catalogs = responses[2].data

    } else if (itemType === 'catalogs') {
        newItem.rootCategories = responses[2].data

    } else if (itemType === 'categories') {
        newItem.childCategories = responses[2].data
        newItem.childProducts = responses[3].data

    } else if (itemType === 'products') {
        newItem.discounts = responses[2].data
        newItem.childSKUs = responses[3].data

    } else if (itemType === 'skus') {
        newItem.discounts = responses[2].data
    }

    return newItem
}

export default CatalogAPI