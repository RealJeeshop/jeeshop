import axios from 'axios'
import _ from 'lodash'

const CatalogAPI = {

    async getAll(itemType) {
        return axios.get(`/rs/${itemType}`)
    },

    async getById(itemType, id) {

        let additionalRequests = prepareRequestByItemType(itemType, id)
        return new Promise((success, die) => {
            axios.all([axios.get(`/rs/${itemType}/${id}`), CatalogAPI.getLocales(itemType, id)].concat(additionalRequests)
            ).then(axios.spread((...responses) => {

                const item = responses[0].data
                item.localizedPresentation = responses[1].data
                success(handleResponseByItemType(itemType, item, responses))

            })).catch(error => {
                console.log('error : ' + JSON.stringify(error))
                die(error)
            })
        })
    },

    getLocales(itemType, id) {
        return axios.get(`/rs/${itemType}/${id}/presentationslocales`)
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

    async upsert(itemType, item) {
        if (item.id) {
            return axios.put(`/rs/${itemType}`, item)
        } else {
            return axios.post(`/rs/${itemType}`, item)
        }
    }
}

function prepareRequestByItemType(itemType, id) {

    if (itemType === 'catalogs') {
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
    if (itemType === 'catalogs') {
        newItem.rootCategoriesIds = responses[2].data

    } else if (itemType === 'categories') {
        newItem.childCategoriesIds = responses[2].data
        newItem.childProductsIds = responses[3].data

    } else if (itemType === 'products') {
        newItem.discountsIds = responses[2].data
        newItem.childSKUsIds = responses[3].data

    } else if (itemType === 'skus') {
        newItem.discountsIds = responses[2].data
    }

    return newItem
}

export default CatalogAPI