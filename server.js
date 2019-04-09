const Haruna = require('./index.js')
const { existsSync } = require('fs')

let config
if (existsSync('./config.json')) {
    config = require('./config.json')
} else if (existsSync('./config.js')) {
    config = require('./config.js')
} else throw new Error('No config file found!')

const server = new Haruna(config)