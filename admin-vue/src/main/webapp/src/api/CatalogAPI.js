import axios from 'axios'

export default {

    async getAll(itemType) {
        return axios.get(`/rs/${itemType}`)
    },

    async getById(itemType, id) {
        return new Promise((success, die) => {
            axios.all([axios.get(`/rs/${itemType}/${id}`),
                this.getCategories(itemType, id),
                this.getLocales(itemType, id)]
            ).then(axios.spread((...responses) => {

                const item = responses[0].data
                const categories = responses[1].data
                const locales = responses[2].data

                item.localizedPresentation = locales
                item.rootCategoriesIds = categories

                success(item)

            })).catch(error => {
                console.log('error : ' + JSON.stringify(error))
                die(error)
            })
        })
    },

    async getLocales(itemType, id) {
        return axios.get(`/rs/${itemType}/${id}/presentationslocales`)
    },

    async getCategories(itemType, id) {
        return axios.get(`/rs/${itemType}/${id}/categories`)
    },

    async upsert(itemType, item) {
        if (item.id) {
            return axios.put(`/rs/${itemType}`, item)
        } else {
            return axios.post(`/rs/${itemType}`, item)
        }
    }
}