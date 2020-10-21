const users = require('./data/users')
const catalogs = require('./data/catalogs')
const categories = require('./data/categories')
const products = require('./data/products')
const skus = require('./data/skus')
const discounts = require('./data/discounts')
const presentations = require('./data/presentations')

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
        res.json(categories)
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