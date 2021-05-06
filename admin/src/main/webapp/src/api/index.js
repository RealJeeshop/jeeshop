import axios from 'axios'
import CatalogAPI from "./CatalogAPI";
import MailsAPI from "./MailsAPI";
import OrderAPI from "./OrderAPI";
import UserAPI from "./UserAPI";
import MediasAPI from "./MediasService"

axios.defaults.baseURL = process.env.NODE_ENV !== 'production'
                            ? 'http://localhost:8000'
                            : 'https://localhost:8443'

axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
axios.defaults.timeout = 10000

axios.interceptors.request.use(function (config) {
    const payload = localStorage.getItem('payload')
    if (payload) {
        config.headers.Authorization = JSON.parse(payload).token
    }
    return config;
});

export {
    CatalogAPI,
    MailsAPI,
    OrderAPI,
    UserAPI,
    MediasAPI

}