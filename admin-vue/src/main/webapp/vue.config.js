const apiMockSetup = require('./src/dev/ApiMockSetup')

module.exports = {
    publicPath: process.env.NODE_ENV === 'production'
      ? '/jeeshop-admin/'
      : '/',

    devServer: {
        overlay: {
            warnings: true,
            errors: true
        },
        setup: apiMockSetup
    }
}
