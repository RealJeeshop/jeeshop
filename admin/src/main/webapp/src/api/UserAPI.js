import axios from 'axios'

const UserAPI = {

    async getAll() {
        return new Promise((success, die) => {
            axios.get('/rs/users')
                .then(response => {
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
    },

    async getUserInfo()  {
        return new Promise((success, die) => {
            axios.get('/rs/users/current')
                .then(result => success(result.data))
                .catch(die)
        })
    },

    async login(email, pass) {

        return new Promise((success, die) => {

            axios.post('/rs/users/administrators', {}, {
                auth: {
                    username: email,
                    password: pass
                }
            }).then(result => {
                if (result) {
                    localStorage.payload = JSON.stringify({
                        roles: result.data.roles.map(r => r.name),
                        token: result.config.headers['Authorization']
                    })
                    success(result.data)
                } else {
                    delete localStorage.payload
                    success(null)
                }
            }).catch(die)
        })



    },

    logout (cb) {
        delete localStorage.payload
        cb()
    },

    loggedIn () {
        return !!localStorage.payload
    },
}

export default UserAPI