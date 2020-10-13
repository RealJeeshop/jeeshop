const users = require('./src/dev/data/users')
const catalogs = require('./src/dev/data/catalogs')
const presentations = require('./src/dev/data/presentations')
const categories = require('./src/dev/data/categories')

module.exports = {
    publicPath: process.env.NODE_ENV === 'production'
      ? '/jeeshop-admin/'
      : '/',
    devServer: {
        overlay: {
            warnings: true,
            errors: true
        },
        setup: function (app) {
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
            app.get('/rs/catalogs', function (req, res) {
                res.json(catalogs);
            })
            app.get('/rs/catalogs/count', function (req, res) {
                res.send(catalogs.length);
            })
            app.get('/rs/catalogs/:id', function (req, res) {
                res.json(catalogs.filter(catalog => catalog.id.toString() === req.params.id)[0])
            })

            app.post('/rs/catalogs/', function (req, res) {
                res.json(catalogs[0])
            })
            app.put('/rs/catalogs/:id', function (req, res) {
                res.json(catalogs.filter(user => user.id.toString() === req.params.id)[0])
            })
            app.delete('/rs/catalogs/:id', function (req, res) {
                res.sendStatus(200)
            })
            app.get('/rs/catalogs/:id/presentationslocales', function (req, res) {
                res.json(["fr_FR","en_GB"])
            })
            app.get('/rs/catalogs/:id/presentations/:locale', function (req, res) {
                res.json(presentations("catalog")[req.params.locale])
            })
            app.get('/rs/catalogs/:id/categories', function (req, res) {
                res.json(categories)
            })
        }
    }
}
