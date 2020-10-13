import catalogs from "./data/catalogs.json"
import categories from "./data/categories.json"
import products from "./data/products.json"
import skus from "./data/skus.json"
import discounts from "./data/discounts.json"
import _ from 'lodash'

function getNextId(items)  {
    return Math.max.apply(Math, items.map((o) => { return o.id })) + 1
}

export default {

    add(itemType, item, success, error) {
        let items = getItems(itemType)
        if (item && items !== null) {

            if (!item.id) {
                item.id = getNextId(items)
                items.push(item)
            } else {
                let itemIndex = items.findIndex((i) => i.id === item.id)
                items[itemIndex] = item
            }
            success(item)

        } else error()
    },

    getAll(itemType, success, error) {

        let items = getItems(itemType)
        if (items !== null) success(_.cloneDeep(items))
        else error(`${itemType} is not a valid item type`)
    }
}


function getItems(itemType) {

    if (itemType === 'catalogs') {
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
