const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const helpers = require('./helpers');

module.exports = {
    entry: {
        'polyfills': './src/web/polyfills.ts',
        'vendor': './src/web/vendor.ts',
        'app': './src/web/main.ts'
    },

    resolve: {
        extensions: ['.ts', '.js']
    },

    module: {
        rules: [
            {
                test: /\.ts$/,
                loaders: [
                    {
                        loader: 'awesome-typescript-loader',
                        options: { configFileName: helpers.root('src', 'web', 'tsconfig.json') }
                    } , 'angular2-template-loader'
                ]
            },
            {
                test: /\.html$/,
                loader: 'html-loader'
            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                loader: 'file-loader?name=assets/[name].[hash].[ext]'
            },
            {
                test: /\.css$/,
                exclude: helpers.root('src', 'web', 'app'),
                loader: ExtractTextPlugin.extract({ fallback: 'style-loader', loader: 'css-loader?sourceMap&minimize' })
            },
            {
                test: /\.css$/,
                include: helpers.root('src', 'web', 'app'),
                loader: 'raw-loader'
            },
            {
                test: /\.scss$/,
                exclude: helpers.root('src', 'web', 'app'),
                use: ExtractTextPlugin.extract({
                    use: [
                        { loader: "css-loader" , options: { sourceMap: true, minimize: true } },
                        { loader: "sass-loader", options: { sourceMap: true } }
                    ],
                    fallback: 'style-loader'
                })
            },
            {
                test: /\.scss$/,
                include: helpers.root('src', 'web', 'app'),
                use: [ { loader: 'raw-loader' }, { loader: 'sass-loader?sourceMap' } ]
            }
        ]
    },

    plugins: [
        // Workaround for angular/angular#11580
        new webpack.ContextReplacementPlugin(
            // The (\\|\/) piece accounts for path separators in *nix and Windows
            /angular(\\|\/)core(\\|\/)@angular/,
            helpers.root('./src/web'), // location of your src
            {} // a map of your routes
        ),

        new webpack.optimize.CommonsChunkPlugin({
            name: ['app', 'vendor', 'polyfills']
        }),

        new HtmlWebpackPlugin({
            template: 'src/web/index.html'
        })
    ]
};