var path = require('path');
var webpack = require('webpack');
var ExtractTextPlugin = require('extract-text-webpack-plugin');

const cssModulesLoader = [
    'css?sourceMap&-minimize',
    'modules',
    'importLoaders=1',
    'localIdentName=[name]___[local]'
].join('&');

module.exports = {
  devtool: 'eval',
  entry: [
    'webpack-dev-server/client?http://localhost:3000',
    'webpack/hot/only-dev-server',
    './client/client'
  ],
  output: {
    path: path.join(__dirname, 'dist'),
    filename: 'bundle.js',
    publicPath: '/build/'
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin()
  ],
  resolve: {
    root: path.resolve('./src'),
    extensions: ['', '.js']
  },
  module: {
    loaders: [
      { test: /\.css$/, loader: "style!css" },
      {
        test: /\.scss$/,
//	loader: ExtractTextPlugin.extract('style-loader', 'css-loader', 'sass-loader') 
        loaders: ["style", cssModulesLoader, "sass?sourceMap"]
      },
      {
        test: /\.js$/,
        loaders: ['react-hot', 'babel'],
        include: [
		path.join(__dirname, 'src/'),
		path.join(__dirname, 'client/')
	]
      }
    ]
  }
};
