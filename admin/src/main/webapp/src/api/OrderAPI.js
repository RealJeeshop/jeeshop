import axios from 'axios'

const OrderAPI = {

    async getAll() {
        return new Promise((success, die) => {
            axios.get('/rs/orders')
                .then(response => success(response.data))
                .catch(die)
        })
    },

    async getManaged() {
        return new Promise((success, die) => {
            axios.get('/rs/orders/managed')
                .then(response => success(response.data))
                .catch(die)
        })
    },

    async getById(id) {
        return new Promise((success, die) => {
            axios.get(`/rs/orders/${id}`)
                .then(response => success(response.data))
                .catch(die)
        })
    }
}

export default OrderAPI