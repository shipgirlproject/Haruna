const Haruna = require('.')
const { existsSync } = require('fs')
if (!existsSync('./config.json') || !existsSync('./config.js')) throw new Error('No config file found!')

let config
if (existsSync('./config.json')) {
    config = require('./config.json')
} else {
    config = require('./config.js')
}

const server = new Haruna(config)