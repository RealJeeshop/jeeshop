import catalogs from "./data/catalogs.json"

export default {

    add(catalog, success, error) {
        if (catalog) {
            catalogs.push(catalog)
            success()
        } else error()
    },

    getAll(success) {
        success(catalogs)
    }
}
