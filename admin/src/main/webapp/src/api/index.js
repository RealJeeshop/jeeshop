import axios from 'axios'
import CatalogAPI from "./CatalogAPI";
import UserService from "./UserService";

axios.defaults.baseURL = process.env.NODE_ENV !== 'production'
                            ? 'http://localhost:8000'
                            : 'https://localhost:8443'

axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*';
axios.defaults.timeout = 1000

export {
    CatalogAPI,
    UserService

}