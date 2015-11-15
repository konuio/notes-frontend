var autoprefixer = require('autoprefixer');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var path = require('path');

module.exports = {
  entry: './src-cljs/webpack/app',
  output: {
    filename: 'app.js',
    path: path.join(__dirname, 'resources/public/js/compiled')
  },
  module: {
    loaders: [
      {
        test: /\.js$/,
        include: [
          path.join(__dirname, 'src-cljs/webpack')
        ],
        loaders: ['babel']
      },
      {
        test: /\.scss/,
        include: [
          path.join(__dirname, 'src-cljs/webpack')
        ],
        loader: ExtractTextPlugin.extract('style', ['css', 'postcss', 'sass'].join('!'))
      }
    ]
  },
  plugins: [
    new ExtractTextPlugin('../../css/compiled/app.css')
  ],
  postcss: [autoprefixer]
};