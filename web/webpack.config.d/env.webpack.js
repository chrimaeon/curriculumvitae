;(function(config) {
    const webpack = require("webpack");

    const definePlugin = new webpack.DefinePlugin(
       {
          PRODUCTION: JSON.stringify(config.mode === "production"),
       }
    )

    config.plugins.push(definePlugin);
})(config);
