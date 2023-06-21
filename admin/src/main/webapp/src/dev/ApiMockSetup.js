const users = require('./data/users')
const catalogs = require('./data/catalogs')
const categories = require('./data/categories')
const products = require('./data/products')
const skus = require('./data/skus')
const discounts = require('./data/discounts')
const orders = require('./data/orders')
const mails = require('./data/mails')
const presentations = require('./data/presentations')
const _ = require('lodash')

module.exports = function (app) {

    // Users
    app.get('/rs/users', function (req, res) {
        res.json(users);
    })
    app.get('/rs/users/count', function (req, res) {
        res.send(users.length);
    })
    app.get('/rs/users/:id', function (req, res) {
        res.json(users.filter(user => user.id.toString() === req.params.id)[0])
    })
    app.get('/rs/users/current', function (req, res) {
        res.json(users[0])
    })
    app.post('/rs/users/', function (req, res) {
        res.json(users[0])
    })
    app.put('/rs/users/:id', function (req, res) {
        res.json(users.filter(user => user.id.toString() === req.params.id)[0])
    })
    app.delete('/rs/users/:id', function (req, res) {
        res.sendStatus(200)
    })
    app.head('/rs/users', function (req, res) {
        res.sendStatus(200);
    })

    // Mail templates
    app.get('/rs/mailtemplates', function (req, res) {
        res.json(mails)
    })

    app.get('/rs/mailtemplates/:id', function (req, res) {
        res.json(mails.filter(mail => mail.id.toString() === req.params.id)[0])
    })

    // Orders
    app.get('/rs/orders', function (req, res) {
        res.json(orders)
    })

    app.get('/rs/orders/count', function (req, res) {

        if (req.params.status) {
            res.json(orders.filter(o => o.status === req.params.status).length)
        } else if (req.params.skuId) {
            res.json(orders.length)
        } else if (req.params.search) {
            res.json(orders.length)
        } else {
            res.json(orders.length)
        }
    })

    app.get('/rs/orders/fixeddeliveryfee', function (req, res) {
        res.send(10.0)
    })

    app.get('/rs/orders/:id', function (req, res) {
        res.json(orders.filter(o => o.id === parseInt(req.params.id))[0])
    })

    app.post('/rs/orders', function (req, res) {
        console.log('req.body : ' + JSON.stringify(req.body))
        res.json(orders[0])
    })

    app.put('/rs/orders', function (req, res) {
        res.json(orders[0])
    })

    app.delete('/rs/orders/:id', function (req, res) {
        _.remove(orders, c => c.id === parseInt(req.params.id))
        res.status(200)
    })

    // Catalogs
    app.get('/rs/:itemType', function (req, res) {
        res.json(getItems(req));
    })
    app.get('/rs/:itemType/count', function (req, res) {
        res.send(getItems(req).length);
    })
    app.get('/rs/:itemType/:id', function (req, res) {
        res.json(getItems(req).filter(catalog => catalog.id.toString() === req.params.id)[0])
    })

    app.post('/rs/:itemType/', function (req, res) {
        res.json(getItems(req)[0])
    })
    app.put('/rs/:itemType/:id', function (req, res) {
        res.json(getItems(req).filter(user => user.id.toString() === req.params.id)[0])
    })
    app.delete('/rs/:itemType/:id', function (req, res) {
        res.sendStatus(200)
    })
    app.get('/rs/:itemType/:id/presentationslocales', function (req, res) {
        res.json(["fr_FR","en_GB"])
    })
    app.get('/rs/:itemType/:id/presentations/:locale', function (req, res) {
        res.json(presentations(req.params.itemType)[req.params.locale])
    })

    // Catalogs
    app.get('/rs/catalogs/:id/categories', function (req, res) {
        res.json(categories)
    })

    // Categories
    app.get('/rs/categories/:id/categories', function (req, res) {
        let tmp = _.cloneDeep(categories)
        _.remove(tmp, c => c.id === parseInt(req.params.id))
        res.json(tmp)
    })

    app.get('/rs/categories/:id/products', function (req, res) {
        res.json(products.slice(1))
    })

    // Products
    app.get('/rs/products/:id/skus', function (req, res) {
        res.json(skus)
    })

    app.get('/rs/products/:id/discounts', function (req, res) {
        res.json(discounts.slice(1))
    })

    // SKUS
    app.get('/rs/skus/:id/discounts', function (req, res) {
        res.json(discounts)
    })
}

function getItems(req) {

    let itemType = req.params.itemType
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
}