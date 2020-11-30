import axios from 'axios'
import CatalogAPI from "./CatalogAPI";
import UserService from "./UserService";

axios.defaults.baseURL = process.env.NODE_ENV !== 'production'
                            ? 'https://localhost:8443'
                            : 'https://localhost:8443'
axios.defaults.timeout = 1000

export {
    CatalogAPI,
    UserService

}