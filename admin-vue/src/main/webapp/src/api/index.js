import axios from 'axios'
import CatalogAPI from "./CatalogAPI";
import UserService from "./UserService";

const debug = process.env.NODE_ENV !== 'production'

axios.defaults.baseURL = debug ? 'http://localhost:8080' : 'http://localhost:8080/jeeshop-admin'
axios.defaults.timeout = 1000

export {
    CatalogAPI,
    UserService

}