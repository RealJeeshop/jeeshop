import axios from 'axios'

const UserAPI = {

    async getAll() {
        return new Promise((success, die) => {
            axios.get('/rs/users')
                .then(response => {
                    console.log('response.data : ' + JSON.stringify(response))
                    success(response.data)
                })
                .catch(die)
        })
    },

    async getById(id) {
        return new Promise((success, die) => {
            axios.get(`/rs/users/${id}`)
                .then(response => success(response.data))
                .catch(die)
        })
    }
}

export default UserAPI