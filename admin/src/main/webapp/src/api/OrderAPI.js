import axios from 'axios'

const OrderAPI = {

    async getAll() {
        return new Promise((success, die) => {
            axios.get('/rs/orders')
                .then(response => {
                    console.log('response.data : ' + JSON.stringify(response))
                    success(response.data)
                })
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