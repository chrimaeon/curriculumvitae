const webpack = require("webpack")

const definePlugin = new webpack.DefinePlugin(
   {
      PRODUCTION: config.mode === "production",
   }
)

config.plugins.push(definePlugin)
