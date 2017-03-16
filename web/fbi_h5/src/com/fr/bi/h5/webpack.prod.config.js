const path = require('path');
var webpack = require('webpack');
var srcPath = path.join(__dirname, '/src');
module.exports = {
    devtool: 'sourcemap',
    entry: {
        entry: `${srcPath}/index.js`
    },
    output: {
        filename: "h5.js",
        sourceMapFilename: 'h5.map',
        path: __dirname + "/dist",
        publicPath: "?op=resource&encode=utf8&resource=/com/fr/bi/h5/dist/",
        library: 'H5',
        libraryTarget: 'umd'
    },
    resolve: {
        extensions: ['', '.js', '.jsx'],
        alias: {
            core: `${srcPath}/core/`,
            data: `${srcPath}/data/`,
            lib: `${srcPath}/lib/`,
            base: `${srcPath}/base/`,
            widgets: `${srcPath}/widgets/`,
            actions: `${srcPath}/actions/`,
            components: `${srcPath}/components/`,
            sources: `${srcPath}/sources/`,
            stores: `${srcPath}/stores/`,
            styles: `${srcPath}/styles/`,
            css: `${srcPath}/css/`
        }
    },
    module: {
        loaders: [
            {
                test: /\.(js|jsx)$/,
                loader: 'babel',
                include: [ `${srcPath}` ]
            },
            {
                test: /\.css$/,
                loader: 'style-loader!css-loader!autoprefixer-loader?browsers=last 2 version'
            },
            {
                test: /\.sass/,
                loader: 'style-loader!css-loader!autoprefixer-loader?browsers=last 2 version!sass-loader?outputStyle=expanded&indentedSyntax'
            },
            {
                test: /\.scss/,
                loader: 'style-loader!css-loader!autoprefixer-loader?browsers=last 2 version!sass-loader?outputStyle=expanded'
            },
            {
                test: /\.less/,
                loader: 'style-loader!css-loader!autoprefixer-loader?browsers=last 2 version!less-loader'
            },
            {
                test: /\.styl/,
                loader: 'style-loader!css-loader!autoprefixer-loader?browsers=last 2 version!stylus-loader'
            },
            {
                test: /\.(png|jpg|gif|woff|woff2)$/,
                loader: 'url-loader?limit=8192'
            },
            {
                test: /\.(mp4|ogg|svg)$/,
                loader: 'file-loader'
            }
        ]
    },
    plugins: [
    ]
}