module.exports = {
    entry: {
        libs : './app/lib/index.js',
        app : './app/app.js'
    },
    output: {
        filename: '[name]_bundle.js',
        path: './app/built'
    }
};