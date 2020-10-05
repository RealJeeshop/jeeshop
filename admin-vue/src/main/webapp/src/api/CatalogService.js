import catalogs from "./data/catalogs.json"
import categories from "./data/categories.json"
import products from "./data/products.json"
import skus from "./data/skus.json"
import discounts from "./data/discounts.json"

export default {

    add(itemType, item, success, error) {
        console.log('itemType of add: ' + JSON.stringify(itemType))
        let items = getItems(itemType)
        if (item && items !== null) {
            items.push(item)
            success()

        } else error()
    },

    getAll(itemType, success, error) {

        console.log('itemType : ' + JSON.stringify(itemType))
        let items = getItems(itemType)
        if (items !== null) success(items)
        else error(`${itemType} is not a valid item type`)
    }
}

function getItems(itemType) {

    if (itemType === 'catalogs') {
        console.log('catalogs : ' + JSON.stringify(catalogs))
        return catalogs

    } else if (itemType === 'products') {
        return products

    } else if(itemType === 'categories') {
        return categories

    } else if (itemType === 'skus') {
        return skus

    } else if (itemType === 'discounts') {
        return discounts
    }

    return null
}
