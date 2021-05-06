import axios from 'axios'

const MailsAPI = {

    async getAll() {
        return new Promise((success, die) => {
            axios.get('/rs/mailtemplates')
                .then(response => {
                    success(response.data)
                })
                .catch(die)
        })
    },

    async getById(id) {
        return new Promise((success, die) => {
            axios.get(`/rs/mailtemplates/${id}`)
                .then(response => success(response.data))
                .catch(die)
        })
    }
}

export default MailsAPI