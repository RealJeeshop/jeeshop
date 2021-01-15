import axios from "axios";

export default {

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
                        success(true)
                    } else {
                        delete localStorage.payload
                        success(false)
                    }
                }).catch(die)
        })



    },

    getToken () {
        return localStorage.payload
    },

    logout (cb) {
        delete localStorage.payload
        if (cb) cb()
        this.onChange(false)
    },

    loggedIn () {
        return !!localStorage.payload
    },


    onChange () {}
}