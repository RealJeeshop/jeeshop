const apiMockSetup = require('./src/dev/ApiMockSetup')

module.exports = {
    publicPath: '/',

    devServer: {
        overlay: {
            warnings: true,
            errors: true
        },
        setup: apiMockSetup
    }
}
