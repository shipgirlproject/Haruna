const fs = require('fs')
module.exports = {
    Haruna: require('./src/haruna.js'),
    version: JSON.parse(fs.readdirSync('./package.json')).version
}