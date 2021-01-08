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

                const item = Object.assign(responses[0].data,
                    { localizedPresentation: responses[1].data },
                    handleResponseByItemType(itemType, responses[1].data, responses))

                let allPresentations = item.localizedPresentation
                    .map(locale => this.getPresentation(itemType, id, locale));

                axios.all(allPresentations)
                    .then(axios.spread((...responses) => {
                        for(let i=0; i < item.localizedPresentation.length; i++) {
                            let newObject = {}
                            newObject[responses[i].locale] = responses[i]
                            item.availableLocales = Object.assign(item.availableLocales ? item.availableLocales : {}, newObject)
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

    loadAllCatalog() {
        return new Promise((success, die) => {
            axios.all([
                this.getAll('catalogs'),
                this.getAll('categories'),
                this.getAll('products'),
                this.getAll('skus'),
                this.getAll('discounts'),
            ]).then(axios.spread((...responses) => {

                success(Object.assign({}, {
                    catalogs: responses[0].data,
                    categories: responses[1].data,
                    products: responses[2].data,
                    skus: responses[3].data,
                    discounts: responses[4].data,
                }))

            })).catch(die)
        })
    },

    upsert(itemType, item) {
        if (item.id) {
            return axios.put(`/rs/${itemType}`, item)
        } else {
            return axios.post(`/rs/${itemType}`, item)
        }
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