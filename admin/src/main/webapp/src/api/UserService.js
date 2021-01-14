import axios from "axios";

export default {

    async login(email, pass) {

        return new Promise((success, die) => {

                axios.head('/rs/users/administrators', {
                    auth: {
                        username: email,
                        password: pass
                    }
                }).then(result => {
                    if (result) {
                        localStorage.token = btoa(`${email}:${pass}`)
                        success(true)
                    } else {
                        delete localStorage.token
                        success(false)
                    }
                }).catch(die)
        })



    },

    getToken () {
        return localStorage.token
    },

    logout (cb) {
        delete localStorage.token
        if (cb) cb()
        this.onChange(false)
    },

    loggedIn () {
        return !!localStorage.token
    },


    onChange () {}
}