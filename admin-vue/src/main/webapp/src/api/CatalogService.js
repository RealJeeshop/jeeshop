const catalogs = [{
    id: "1",
    name: "Super catalogue"
}];

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